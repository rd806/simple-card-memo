package com.github.rd806.simplecardmemo.container.menu;

import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.init.ModMenus;
import com.github.rd806.simplecardmemo.items.MemoManagerItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ManagerMenu extends AbstractContainerMenu {


    private final ItemStack stack;
    private final Player player;
    private final int slotIndex;
    private ContainerData data;
    // 输入槽索引
    private static final int INPUT_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = 2;

    // 客户端
    public ManagerMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                buf.readInt(),
                inventory.player.getItemInHand(InteractionHand.MAIN_HAND),
                new SimpleContainerData(1));
    }

    // 服务端
    public ManagerMenu(int id, Inventory inventory, int slotIndex, ItemStack itemStack, ContainerData data) {
        // 指定该菜单对应的 MenuType
        super(ModMenus.MANAGER_MENU.get(), id);
        // 保存数据
        this.player = inventory.player;
        this.stack = itemStack;
        this.slotIndex = slotIndex;
        this.data = data;
        // 注册数据同步槽
        addDataSlots(data);
        // 添加玩家背包与快捷栏
        addPlayerInventory(inventory);
        // 添加输入栏
        addSendSlot(MemoManagerItem.getInventory(stack));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
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

    // 创建发送槽位
    private void addSendSlot(IItemHandler handler) {
        this.addSlot(new SlotItemHandler(handler, INPUT_SLOT, 55, 128) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                // 限制只能放入特定物品
                return stack.equals(ModCreativeModeTabs.newMemo(), false);
            }

            @Override
            public void setChanged() {
                super.setChanged();
                // 物品变化时保存到NBT
                MemoManagerItem.setInventory(stack);
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
                        157 + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inventory,
                    col,
                    8 + col * 18,
                    215
            ));
        }
    }
}
