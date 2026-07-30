package com.github.rd806.simplecardmemo.container.menu;

import com.github.rd806.simplecardmemo.init.ModMenus;
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

public class MailMenu extends AbstractContainerMenu {

    private final ItemStack itemStack;
    private final Player player;
    private final int slotIndex;
    private ContainerData data;

    // 输入槽索引
    private static final int INPUT_SLOT = 0;

    // 客户端
    public MailMenu(int id, Inventory inventory, FriendlyByteBuf buf) {
        this(id, inventory,
                buf.readInt(),
                inventory.player.getItemInHand(InteractionHand.MAIN_HAND),
                new SimpleContainerData(1));
    }

    // 服务端
    public MailMenu(int id, Inventory inventory, int slotIndex, ItemStack itemStack, ContainerData data) {
        // 指定该菜单对应的 MenuType
        super(ModMenus.MAIL_MENU.get(), id);
        // 保存数据
        this.player = inventory.player;
        this.itemStack = itemStack;
        this.slotIndex = slotIndex;
        this.data = data;
        // 注册数据同步槽
        addDataSlots(data);
        // 添加玩家背包与快捷栏
        addPlayerInventory(inventory);
    }

    @Override
    public ItemStack quickMoveStack(@NotNull Player player, int i) {
        return null;
    }

    // 检测界面能否继续开启
    @Override
    public boolean stillValid(@NotNull Player player) {
        // 始终保持打开
        return true;
    }

    private void addSendSlot(IItemHandler handler) {
        this.addSlot(new SlotItemHandler(handler, INPUT_SLOT, 77, 31));
    }

    // 加入玩家背包
    private void addPlayerInventory(Inventory inv) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                // 对应 GUI 贴图里的物品栏实际位置（从左往右数的第一个格子位置为准）
                this.addSlot(new Slot(
                        inv,
                        col + row * 9 + 9,
                        8 + col * 18,
                        84 + row * 18
                ));
            }
        }
        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(
                    inv,
                    col,
                    8 + col * 18,
                    142
            ));
        }
    }
}
