package com.github.rd806.simplecardmemo.network.create;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.items.memoviewer.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class CreateExistMemo {

    public static boolean consumeItem(ServerPlayer player, Item targetItem, int removeAmount) {
        // 统计背包中该物品的总数量
        int totalCount = 0;
        // 遍历玩家主背包
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.getItem().equals(targetItem) && stack.getTag() == null) {
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
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!stack.isEmpty() && stack.getItem().equals(targetItem)) {
                int stackSize = stack.getCount();
                if (stackSize <= remainingToRemove) {
                    // 如果这个槽位的物品数量小于等于需要移除的数量，直接清空该槽位
                    player.getInventory().setItem(slot, ItemStack.EMPTY);
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

    // 发送物品
    public static void getItem(ServerPlayer player, MemoInfo memoInfo) {
        if (memoInfo == null) {
            return;
        }
        if (player != null) {
            ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
            // 设置显示名
            viewer.setHoverName(Component.literal(memoInfo.getMemoName()));
            // 设置 NBT 数据
            MemoViewerItem.setDisplayName(viewer, memoInfo.getMemoName());
            MemoViewerItem.setFilePath(viewer, memoInfo.getMemoName());
            MemoViewerItem.setAuthor(viewer, memoInfo.getMemoAuthor());
            MemoViewerItem.setTextSource(viewer, memoInfo.isLocalFile());
            MemoViewerItem.setLastModified(viewer, memoInfo.getLastModified());
            // 发送物品
            player.getInventory().add(viewer);
            player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export.success"),
                    false
            );
        }
    }
}
