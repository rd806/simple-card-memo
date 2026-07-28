package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.Config;

import java.util.LinkedHashMap;

public class MemoLRUCache<K, V> extends LinkedHashMap<K, V> {
    private static int MAX_CAPACITY = 5;

    public MemoLRUCache() {
        super(16, 0.75f, true);
        MAX_CAPACITY = Config.CACHE_SIZE.get();
    }

    @Override
    protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
        return size() > MAX_CAPACITY;
    }
}
