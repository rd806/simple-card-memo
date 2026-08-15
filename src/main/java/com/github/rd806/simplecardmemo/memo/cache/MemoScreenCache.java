package com.github.rd806.simplecardmemo.memo.cache;

import com.github.rd806.simplecardmemo.init.container.screen.MemoViewerScreen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MemoScreenCache {

    private static MemoLRUCache<ItemStack, MemoViewerScreen> cache;

    public MemoScreenCache() { cache = new MemoLRUCache<>(); }

    public MemoLRUCache<ItemStack, MemoViewerScreen> getCache() { return cache; }

    public void put(ItemStack item, MemoViewerScreen screen) { cache.put(item, screen); }
    public MemoViewerScreen get(ItemStack item) { return cache.get(item); }

    public void clear() { cache.clear(); }
}
