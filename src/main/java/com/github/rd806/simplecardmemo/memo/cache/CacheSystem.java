package com.github.rd806.simplecardmemo.memo.cache;

public class CacheSystem {

    public static MemoLRUCache<String, String> cache = new MemoLRUCache<>();

    public static void put(String key, String value) { cache.put(key, value); }
    public static String get(String key) { return cache.get(key); }
}
