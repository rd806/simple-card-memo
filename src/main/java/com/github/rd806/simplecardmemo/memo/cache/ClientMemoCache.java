package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.MemoLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientMemoCache {

    private static MemoLRUCache<String, String> cache = new MemoLRUCache<>();
    // 临时文件
    private static final MemoInfo tempMemo = new MemoInfo(
            "temp", "temp.md", "Default", true, 0);
    // 最新文件
    private static ItemStack lastMemo = ModCreativeModeTabs.memoGuide();

    public static void put(String key, String value) { cache.put(key, value); }
    public static String get(String key) { return cache.get(key); }

    public static MemoInfo getTempMemo() { return tempMemo; }

    public static void setLastMemo(ItemStack memo) { ClientMemoCache.lastMemo = memo; }
    public static ItemStack getLastMemo() { return lastMemo; }

    // 刷新缓存
    public static void clear() {
        cache = new MemoLRUCache<>();
    }

    // 带缓存的加载
    public static String getMemoContentWithCache(MemoInfo memoInfo) {
        String filePath = memoInfo.getMemoPath();
        // 使用 LRU 缓存机制
        String content = get(filePath);
        // 未命中则加载
        if (content == null) {
            content = MemoLoader.loadText(memoInfo);
        }
        // 更新缓冲区
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            put(filePath, content);
        }
        return content;
    }

    // 不带缓存的加载
    public static String getMemoContent(MemoInfo memoInfo) {
        String filePath = memoInfo.getMemoPath();
        // 重新获取文本
        String content = MemoLoader.loadText(memoInfo);
        if (content == null) {
            content = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                    .append(filePath).getString();
        } else {
            put(filePath, content);
        }
        return content;
    }
}
