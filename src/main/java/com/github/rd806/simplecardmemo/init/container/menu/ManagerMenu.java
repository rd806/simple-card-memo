package com.github.rd806.simplecardmemo.init.container.menu;

import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ManagerMenu extends AbstractContainerMenu {
    // 容器
    private final ItemStackHandler itemStackHandler;
    // 输入槽索引
    public static final int INPUT_SLOT = 0;
    public static final int OUTPUT_SLOT = 1;
    private static final int PLAYER_INVENTORY_START = 2;

    // 客户端
    public ManagerMenu(int id, Inventory inventory, FriendlyByteBuf ignoredBuf) {
        this(id, inventory);
    }

    // 服务端
    public ManagerMenu(int id, Inventory inventory) {
        // 指定该菜单对应的 MenuType
        super(ModMenus.MANAGER_MENU.value(), id);
        // 获取数据
        this.itemStackHandler = new ItemStackHandler(2);
        // 添加玩家背包与快捷栏
        addPlayerInventory(inventory);
        // 添加额外物品栏
        addSlot();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        // 获取转移的物品
        ItemStack stack = slot.getItem();
        ItemStack copy = stack.copy();
        // 自定义槽位（0-1）<-> 玩家背包（2-38）
        if (index < PLAYER_INVENTORY_START) {
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

    // 关闭界面返还物品
    @Override
    public void removed(@NotNull Player player) {
        if (!player.level().isClientSide) {
            returnItem(player, INPUT_SLOT);
            returnItem(player, OUTPUT_SLOT);
        }
        // 同步玩家背包到客户端
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.inventoryMenu.broadcastChanges();
        }
        super.removed(player);
    }

    // 创建发送槽位
    private void addSlot() {
        // 发送槽位
        this.addSlot(new SlotItemHandler(itemStackHandler, INPUT_SLOT, 55, 119) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                // 只允许放入 Memo Viewer
                return stack.getItem().equals(ModItems.MEMO_VIEWER.value());
            }
        });
        // 接收槽位
        this.addSlot(new SlotItemHandler(itemStackHandler, OUTPUT_SLOT, 109, 119) {
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
                        154 + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inventory,
                    col,
                    8 + col * 18,
                    212
            ));
        }
    }

    // 返回物品
    private void returnItem(Player player, int slotIndex) {
        ItemStack item = this.itemStackHandler.getStackInSlot(slotIndex);
        if (player.isDeadOrDying()) {
            // 死亡时直接掉落
            player.drop(item, false);
        } else {
            // 尝试加入背包
            boolean addItem = player.getInventory().add(item);
            // 背包已满，掉落在地上
            if (!addItem) {
                player.drop(item, false);
            }
        }
    }

    public ItemStackHandler getItemStackHandler() {
        return itemStackHandler;
    }
}
