package com.github.rd806.simplecardmemo.memo;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.MemoContentCache;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class CacheSystem {
    // 查看缓存
    public static void getCache() {
        MemoContentCache memoCache = ClientSetup.clientContentCache;
        Set<String> set = memoCache.getCache().keySet();
        Player player = Minecraft.getInstance().player;
        if (player == null) { return; }
        if (set.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".command.client_cache.empty"),
                    false
            );
        } else {
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".command.client_cache.info")
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

    // 带缓存的加载
    public static String getMemoContentWithCache(MemoInfo memoInfo, MemoContentCache memoCache) {
        String filePath = memoInfo.getMemoPath();
        // 使用 LRU 缓存机制
        String content = memoCache.get(filePath);
        if (content != null) { return content; }
        // 未命中则加载
        if (memoInfo.isExternal()) {
            content = MemoLoader.loadText(memoInfo);
        } else {
            content = getResourceString(memoInfo);
        }
        // 更新缓冲区
        if (content == null) {
            content = I18n.get(SimpleCardMemo.MODID + ".gui.viewer_screen.error", filePath);
        } else {
            memoCache.put(filePath, content);
        }
        return content;
    }

    // 不带缓存的加载
    public static String getMemoContent(MemoInfo memoInfo, MemoContentCache memoCache) {
        String filePath = memoInfo.getMemoPath();
        String content;
        // 先从本地加载
        if (memoInfo.isExternal()) {
            content = MemoLoader.loadText(memoInfo);
        } else {
            content = getResourceString(memoInfo);
        }
        // 本地加载失败则访问缓存
        if (content == null) {
            // 获取缓存
            content = memoCache.get(filePath);
            if (content == null) {
                content = I18n.get(SimpleCardMemo.MODID + ".gui.viewer_screen.error", filePath);
            }
        } else {
            memoCache.put(filePath, content);
        }
        return content;
    }

    // 从资源包中加载
    private static String getResourceString(MemoInfo memoInfo) {
        String filepath = memoInfo.getMemoPath();
        if (filepath == null) {
            SimpleCardMemo.LOGGER.error("The Memo Path is null!");
            return null;
        }
        try {
            // 从资源包中加载
            ResourceLocation location = ResourceLocation.parse(filepath);
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(location).orElse(null);
            // 检查来源
            if (resource != null) {
                StringBuilder content = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line).append("\n");
                    }
                }
                return content.toString();
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to load file from resources: {}", filepath);
        }
        return null;
    }
}
