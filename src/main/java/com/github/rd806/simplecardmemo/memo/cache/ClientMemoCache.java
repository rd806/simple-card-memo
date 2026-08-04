package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Set;

public class ClientMemoCache {

    private static MemoLRUCache<String, String> cache = new MemoLRUCache<>();
    // 最新文件
    private static ItemStack lastMemo = ModCreativeModeTabs.memoGuide();

    public static void put(String key, String value) { cache.put(key, value); }
    public static String get(String key) { return cache.get(key); }

    public static void setLastMemo(ItemStack memo) { ClientMemoCache.lastMemo = memo; }
    public static ItemStack getLastMemo() { return lastMemo; }

    // 查看缓存
    @OnlyIn(Dist.CLIENT)
    public static void getInfo() {
        Set<String> set = cache.keySet();
        Player player = Minecraft.getInstance().player;
        if (player == null) { return; }
        if (set.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".command.cache.empty"),
                    false
            );
        } else {
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".command.cache.info"),
                    false
            );
            for (String key : set) {
                player.displayClientMessage(Component.literal("- " + key), false);
            }
        }
    }

    // 刷新缓存
    @OnlyIn(Dist.CLIENT)
    public static void clear() {
        cache = new MemoLRUCache<>();
        // 提示信息
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".command.cache.clear"),
                    false
            );
        }
    }

    // 带缓存的加载
    @OnlyIn(Dist.CLIENT)
    public static String getMemoContentWithCache(MemoInfo memoInfo) {
        String filePath = memoInfo.getMemoPath();
        // 使用 LRU 缓存机制
        String content = get(filePath);
        // 未命中则加载
        if (content == null) {
            content = MemoLoader.loadText(memoInfo);
        }
        // 更新缓冲区
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            put(filePath, content);
        }
        return content;
    }

    // 不带缓存的加载
    @OnlyIn(Dist.CLIENT)
    public static String getMemoContent(MemoInfo memoInfo) {
        String filePath = memoInfo.getMemoPath();
        // 重新获取文本
        String content = MemoLoader.loadText(memoInfo);
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            put(filePath, content);
        }
        return content;
    }
}
