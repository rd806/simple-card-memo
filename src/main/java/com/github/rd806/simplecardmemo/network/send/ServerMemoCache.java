package com.github.rd806.simplecardmemo.network.send;

import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerMemoCache {

    private static final ServerMemoCache INSTANCE = new ServerMemoCache();
    private final Map<String, ItemStack> memoCache = new ConcurrentHashMap<>();
    private final Map<String, String> contentCache = new ConcurrentHashMap<>();

    public static ServerMemoCache getInstance() {
        return INSTANCE;
    }

    // 添加
    public void addMemo(UUID senderId, UUID receiverId, ItemStack memo, String content) {
        String key = senderId.toString() + ":" + receiverId.toString();
        memoCache.put(key, memo);
        contentCache.put(key, content);
    }

    // 取出
    public ItemStack retrieveMemoItem(UUID senderId, UUID receiverId) {
        String key = senderId.toString() + ":" + receiverId.toString();
        return memoCache.remove(key);
    }

    public String retrieveMemoContent(UUID senderId, UUID receiverId) {
        String key = senderId.toString() + ":" + receiverId.toString();
        return contentCache.remove(contentCache.remove(key));
    }
}
