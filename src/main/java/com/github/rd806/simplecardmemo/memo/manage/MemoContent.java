package com.github.rd806.simplecardmemo.memo.manage;

import com.github.rd806.simplecardmemo.init.container.screen.MemoViewerScreen;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.MemoContentCache;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@OnlyIn(Dist.CLIENT)
public class MemoContent {

    // 文本加载线程
    public static final ExecutorService CONTENT_LOADER = Executors.newFixedThreadPool(2, r -> {
        Thread t = new Thread(r, "SimpleCardMemo MemoLoader");
        t.setDaemon(true);
        return t;
    });
    private static CompletableFuture<Void> contentFuture;

    public static String content = null;
    public static MemoViewerScreen memoViewerScreen = null;

    // 发送邮件内容
    public static void sendMailContent(MemoInfo memoInfo, String target, String message, MemoContentCache memoCache) {
        getMemoContentWithCache(memoInfo, memoCache);
        CompletableFuture.allOf(contentFuture).thenAccept(
                ignore -> Minecraft.getInstance().execute(() -> Channel.sendMail(content, target, message))
        );
    }

    // 设置界面
    public static void setMemoScreen(ItemStack item, MemoContentCache memoCache) {
        memoViewerScreen = ClientSetup.clientScreenCache.get(item);

        if (memoViewerScreen == null) {
            MemoInfo memoInfo = MemoViewerItem.getMemoInfo(item);
            getMemoContentWithCache(memoInfo, memoCache);
            CompletableFuture.allOf(contentFuture).thenAccept(ignore -> {
                memoViewerScreen = new MemoViewerScreen(content, memoInfo);
                ClientSetup.clientScreenCache.put(item, memoViewerScreen);
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(memoViewerScreen));
            });
        } else {
            Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(memoViewerScreen));
        }
    }

    // 重载文本界面
    public static void reloadMemoScreen(MemoInfo memoInfo, MemoContentCache memoCache) {
        getMemoContent(memoInfo, memoCache);
        CompletableFuture.allOf(contentFuture).thenAccept(
                ignore -> Minecraft.getInstance().execute(() -> memoViewerScreen.reload())
        );
    }

    // 带缓存的加载
    private static void getMemoContentWithCache(MemoInfo memoInfo, MemoContentCache memoCache) {
        // 异步加载
        contentFuture = CompletableFuture.runAsync(() -> {
            String filePath = memoInfo.getMemoPath();
            // 使用 LRU 缓存机制
            content = memoCache.get(filePath);
            if (content == null) {
                // 未命中则加载
                if (memoInfo.isExternal()) {
                    content = MemoLoader.loadFromExternal(memoInfo);
                } else {
                    Minecraft mc = Minecraft.getInstance();
                    String lang = mc.getLanguageManager().getSelected();
                    ResourceManager res = mc.getResourceManager();
                    content = MemoLoader.loadFromResource(memoInfo, lang, res);
                }
            }
            // 更新缓冲区
            if (content == null) {
                content = I18n.get("gui.simplecardmemo.viewer_screen.error", filePath);
            } else {
                memoCache.put(filePath, content);
            }
        }, CONTENT_LOADER);
    }

    // 不带缓存的加载
    private static void getMemoContent(MemoInfo memoInfo, MemoContentCache memoCache) {
        contentFuture = CompletableFuture.runAsync(() -> {
            String filePath = memoInfo.getMemoPath();
            // 先从本地加载
            if (memoInfo.isExternal()) {
                content = MemoLoader.loadFromExternal(memoInfo);
            } else {
                Minecraft mc = Minecraft.getInstance();
                String lang = mc.getLanguageManager().getSelected();
                ResourceManager res = mc.getResourceManager();
                content = MemoLoader.loadFromResource(memoInfo, lang, res);
            }
            // 本地加载失败则访问缓存
            if (content == null) {
                // 获取缓存
                content = memoCache.get(filePath);
                if (content == null) {
                    content = I18n.get("gui.simplecardmemo.viewer_screen.error", filePath);
                }
            } else {
                memoCache.put(filePath, content);
            }
        }, CONTENT_LOADER);
    }

    // 查看缓存
    public static void getCache() {
        MemoContentCache memoCache = ClientSetup.clientContentCache;
        Set<String> set = memoCache.getCache().keySet();
        Player player = Minecraft.getInstance().player;
        if (player == null) { return; }
        if (set.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.simplecardmemo.command.client_cache.empty"),
                    false
            );
        } else {
            player.displayClientMessage(
                    Component.translatable("message.simplecardmemo.command.client_cache.info")
                            .withStyle(ChatFormatting.GREEN),
                    false
            );
            for (String key : set) {
                player.displayClientMessage(Component.literal("§a▍ §7" + key), false);
            }
        }
    }

    // 清理缓存
    public static void clearCache() {
        ClientSetup.clientContentCache.clear();
        ClientSetup.clientScreenCache.clear();
    }
}
