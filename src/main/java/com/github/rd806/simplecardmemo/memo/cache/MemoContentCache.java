package com.github.rd806.simplecardmemo.memo.cache;

public class MemoContentCache {

    private static MemoLRUCache<String, String> cache;

    public MemoContentCache() { cache = new MemoLRUCache<>(); }

    public MemoLRUCache<String, String> getCache() { return cache; }

    public void put(String key, String value) { cache.put(key, value); }
    public String get(String key) { return cache.get(key); }

    public void clear() { cache.clear(); }
}
