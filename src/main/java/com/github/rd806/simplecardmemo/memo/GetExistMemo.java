package com.github.rd806.simplecardmemo.memo;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public class GetExistMemo {

    // 消耗指定的 ItemStack
    public static boolean consumeItem(Inventory inventory, ItemStack targetItem, int removeAmount) {
        // 统计背包中该物品的总数量
        int totalCount = 0;
        // 遍历玩家主背包
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.equals(targetItem, false)) {
                totalCount += stack.getCount();
                // 如果已经达到需求，可以提前跳出，提高效率
                if (totalCount >= removeAmount) {
                    break;
                }
            }
        }
        // 如果总数不够，直接返回false
        if (totalCount < removeAmount) {
            return false;
        }
        // 开始移除物品 (从第一个找到的槽位开始扣减)
        int remainingToRemove = removeAmount;
        for (int slot = 0; slot < inventory.getContainerSize(); slot++) {
            ItemStack stack = inventory.getItem(slot);
            if (stack.equals(targetItem, false)) {
                int stackSize = stack.getCount();
                if (stackSize <= remainingToRemove) {
                    // 如果这个槽位的物品数量小于等于需要移除的数量，直接清空该槽位
                    inventory.setItem(slot, ItemStack.EMPTY);
                    remainingToRemove -= stackSize;
                } else {
                    // 如果这个槽位的物品数量大于需要移除的数量，只移除一部分
                    stack.shrink(remainingToRemove);
                    remainingToRemove = 0;
                }
                // 当剩余待移除数量为0时，结束循环
                if (remainingToRemove == 0) {
                    break;
                }
            }
        }
        return true;
    }

    // 设置物品
    public static ItemStack setMemo(MemoInfo memoInfo) {
        if (memoInfo != null) {
            ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
            // 设置显示名
            viewer.setHoverName(Component.literal(memoInfo.getMemoName()));
            // 设置 NBT 数据
            MemoViewerItem.setDisplayName(viewer, memoInfo.getMemoName());
            MemoViewerItem.setFilePath(viewer, memoInfo.getMemoPath());
            MemoViewerItem.setAuthor(viewer, memoInfo.getMemoAuthor());
            MemoViewerItem.setTextSource(viewer, memoInfo.isExternal());
            MemoViewerItem.setLastModified(viewer, memoInfo.getLastModified());
            // 发送物品
            return viewer;
        }
        SimpleCardMemo.LOGGER.error("The target Memo is null!");
        return ItemStack.EMPTY;
    }
}
