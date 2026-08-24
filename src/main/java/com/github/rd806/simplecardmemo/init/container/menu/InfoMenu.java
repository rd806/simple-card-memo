package com.github.rd806.simplecardmemo.init.container.menu;

import com.github.rd806.simplecardmemo.init.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InfoMenu extends AbstractContainerMenu {

    private static int index;

    public InfoMenu(int id, Inventory inventory, FriendlyByteBuf ignoredBuf) {
        this(id, inventory, index);
    }

    // 服务端
    public InfoMenu(int id, Inventory ignoredInventory, int memoIndex) {
        // 指定该菜单对应的 MenuType
        super(ModMenus.INFO_MENU.get(), id);
        index = memoIndex;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) { return true; }

    public int getMemoIndex() { return index; }
}
