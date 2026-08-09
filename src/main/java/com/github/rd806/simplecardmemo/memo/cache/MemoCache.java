package com.github.rd806.simplecardmemo.memo.cache;

import net.minecraft.world.item.ItemStack;

public class MemoCache {

    private static MemoLRUCache<String, String> cache;
    // 最新文件
    private static ItemStack lastMemo;

    public MemoCache() {
        cache = new MemoLRUCache<>();
    }

    public MemoLRUCache<String, String> getCache() { return cache; }

    public void put(String key, String value) { cache.put(key, value); }
    public String get(String key) { return cache.get(key); }

    public void setLastMemo(ItemStack memo) { MemoCache.lastMemo = memo; }
    public ItemStack getLastMemo() { return lastMemo; }

    public void clear() { cache = new MemoLRUCache<>(); }
}
