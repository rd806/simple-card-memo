package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.config.CommonConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class MemoLRUCache<K, V> extends LinkedHashMap<K, V> {
    private static int MAX_CAPACITY = 5;

    public MemoLRUCache() {
        super(16, 0.75f, true);
        MAX_CAPACITY = CommonConfig.CACHE_SIZE.get();
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > MAX_CAPACITY;
    }
}
