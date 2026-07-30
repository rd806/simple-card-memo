package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.memo.MemoLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class CacheSystem {

    private static MemoLRUCache<String, String> cache = new MemoLRUCache<>();
    private static ItemStack lastMemo = ModCreativeModeTabs.memoGuide();

    public static void put(String key, String value) { cache.put(key, value); }
    public static String get(String key) { return cache.get(key); }

    public static void setMemo(ItemStack memo) { CacheSystem.lastMemo = memo; }
    public static ItemStack getMemo() { return lastMemo; }

    // 刷新缓存
    public static void clear() {
        cache = new MemoLRUCache<>();
    }

    // 带缓存的加载
    public static String getMemoContentWithCache(String filePath, boolean isLocalFile) {
        // 使用 LRU 缓存机制
        String content = CacheSystem.get(filePath);
        // 未命中则加载
        if (content == null) {
            content = MemoLoader.loadText(filePath, isLocalFile);
        }
        // 更新缓冲区
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            CacheSystem.put(filePath, content);
        }
        return content;
    }

    // 不带缓存的加载
    public static String getMemoContent(String filePath, boolean isLocalFile) {
        String content = MemoLoader.loadText(filePath, isLocalFile);
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            CacheSystem.put(filePath, content);
        }
        return content;
    }
}
