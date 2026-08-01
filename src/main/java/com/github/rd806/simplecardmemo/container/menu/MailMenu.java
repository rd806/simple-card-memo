package com.github.rd806.simplecardmemo.container.menu;

import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MailMenu extends AbstractContainerMenu {

    private final ItemStackHandler itemStackHandler = new ItemStackHandler(2);

    // 输入槽索引
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    public static final int PLAYER_INVENTORY_START = 2;

    // 客户端
    public MailMenu(int id, Inventory inventory, FriendlyByteBuf ignoredBuf) {
        this(id, inventory);
    }

    // 服务端
    public MailMenu(int id, Inventory inventory) {
        // 指定该菜单对应的 MenuType
        super(ModMenus.MAIL_MENU.get(), id);
        // 保存数据
        // 添加玩家背包与快捷栏
        addPlayerInventory(inventory);
        // 添加输入栏
        addSendSlot(itemStackHandler);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        Slot slot = this.slots.get(slotIndex);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        // 获取转移的物品
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        // 自定义槽位（0）<-> 玩家背包（1-38）
        if (slotIndex < PLAYER_INVENTORY_START) {
            // 从自定义槽位移到玩家背包
            if (!this.moveItemStackTo(stack, PLAYER_INVENTORY_START, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else {
            // 从玩家背包移到自定义槽位（只允许移到输入槽）
            if (!this.moveItemStackTo(stack, INPUT_SLOT, INPUT_SLOT + 1, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        return copy;
    }

    // 检测界面能否继续开启
    @Override
    public boolean stillValid(@NotNull Player player) {
        // 始终保持打开
        return true;
    }

    // 创建槽位
    private void addSendSlot(ItemStackHandler handler) {
        // 发送槽位
        this.addSlot(new SlotItemHandler(handler, INPUT_SLOT, 44, 33) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                // 限制只能放入特定物品
                return stack.getItem().equals(ModItems.MEMO_VIEWER.get());
            }
        });
        // 接收槽位
        this.addSlot(new SlotItemHandler(handler, OUTPUT_SLOT, 116, 54) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
    }

    // 加入玩家背包
    private void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                // 对应 GUI 贴图里的物品栏实际位置（从左往右数的第一个格子位置为准）
                this.addSlot(new Slot(
                        inventory,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inventory,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }
}
