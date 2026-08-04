package com.github.rd806.simplecardmemo.memo.cache;

import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMemoCache {

    private static final Map<String, ItemStack> memoCache = new ConcurrentHashMap<>();
    private static final Map<String, String> contentCache = new ConcurrentHashMap<>();

    // 添加
    public static void addMemo(String key, ItemStack stack, String content) {
        memoCache.put(key, stack);
        contentCache.put(key, content);
    }

    // 取出
    public static ItemStack retrieveMemoItem(String key) { return memoCache.get(key); }
    public static String retrieveMemoContent(String key) { return contentCache.get(key); }

    // 显示所有信件
    public static Set<String> getMemoKeys() {
        return memoCache.keySet();
    }

    // 删除
    public static void removeMemo(String key) {
        memoCache.remove(key);
        contentCache.remove(key);
    }

    // 清理
    public static void clearMemoCache() {
        memoCache.clear();
        contentCache.clear();
    }
}
