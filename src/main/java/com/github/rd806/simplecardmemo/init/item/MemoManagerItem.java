package com.github.rd806.simplecardmemo.init.item;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoManagerItem extends Item implements MenuProvider {

    public MemoManagerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.general.tooltip")
                .withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_manager.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在客户端执行打开界面的逻辑
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 打开GUI，并额外写入槽位和ItemStack数据
            NetworkHooks.openScreen(serverPlayer, this, buffer -> {});
        }
        return InteractionResultHolder.success(stack);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable(SimpleCardMemo.MODID + ".item.memo_manager.screen");
    }

    // 当玩家打开界面时创建 Menu
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ManagerMenu(id, inventory);
    }
}
