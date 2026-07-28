package com.github.rd806.simplecardmemo.init.memo;

import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import net.minecraft.world.item.ItemStack;

public class LatestMemo {

    // 上一次打开的备忘录
    private static ItemStack memo = ModCreativeModeTabs.memoGuide();

    public static void setMemo(ItemStack memo) { LatestMemo.memo = memo; }
    public static ItemStack getMemo() { return memo; }
}
