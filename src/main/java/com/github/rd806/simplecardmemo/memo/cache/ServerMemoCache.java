package com.github.rd806.simplecardmemo.memo.cache;

import java.util.Set;

public class ServerMemoCache {

    private static final MemoLRUCache<String, String> cache = new MemoLRUCache<>();

    public static void put(String key, String value) { cache.put(key, value); }
    public static String get(String key) { return cache.get(key); }

    public static Set<String> getAll() { return cache.keySet(); }
}
