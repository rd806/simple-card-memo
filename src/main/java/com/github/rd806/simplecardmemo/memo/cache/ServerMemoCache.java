package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.memo.MemoInfo;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMemoCache {

    private static final ServerMemoCache INSTANCE = new ServerMemoCache();
    private final Map<String, MemoInfo> memoCache = new ConcurrentHashMap<>();
    private final Map<String, String> contentCache = new ConcurrentHashMap<>();

    public static ServerMemoCache getInstance() {
        return INSTANCE;
    }

    // 添加
    public void addMemo(String key, MemoInfo memoInfo, String content) {
        memoCache.put(key, memoInfo);
        contentCache.put(key, content);
    }

    // 取出
    public MemoInfo retrieveMemoItem(String key) {
        return memoCache.get(key);
    }

    public String retrieveMemoContent(String key) {
        return contentCache.get(key);
    }

    // 显示所有信件
    public Set<String> getMemoKeys() {
        return memoCache.keySet();
    }

    // 清除
    public void clearMemo(String senderId, String receiverId) {
        String key = senderId + ":" + receiverId;
        memoCache.remove(key);
        contentCache.remove(contentCache.remove(key));
    }
}
