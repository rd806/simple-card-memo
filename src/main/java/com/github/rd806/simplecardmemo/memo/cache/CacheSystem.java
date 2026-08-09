package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import net.minecraft.client.Minecraft;
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
    public static void getInfo(MemoCache memoCache) {
        Set<String> set = memoCache.getCache().keySet();
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

    // 带缓存的加载
    public static String getMemoContentWithCache(MemoInfo memoInfo, MemoCache memoCache) {
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
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            memoCache.put(filePath, content);
        }
        return content;
    }

    // 不带缓存的加载
    public static String getMemoContent(MemoInfo memoInfo, MemoCache memoCache) {
        String filePath = memoInfo.getMemoPath();
        // 重新获取文本
        String content = MemoLoader.loadText(memoInfo);
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
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
