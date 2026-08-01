package com.github.rd806.simplecardmemo.items;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoSenderItem extends Item {

    private static final String INVENTORY = "inventory";

    public MemoSenderItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_sender.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在服务端执行打开界面的逻辑
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            // 获取当前物品在玩家物品栏的槽位
            int slotIndex = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            ContainerData data = new SimpleContainerData(slotIndex);
            // 定义菜单提供者
            SimpleMenuProvider provider = new SimpleMenuProvider(
                    (windowId, inv, p) -> new MailMenu(windowId, inv, stack.copy(), data),
                    Component.translatable(SimpleCardMemo.MODID + ".item.memo_sender.screen")
            );
            // 打开GUI，并额外写入槽位和ItemStack数据
            NetworkHooks.openScreen(serverPlayer, provider, (buffer) -> {
                buffer.writeInt(slotIndex);
                buffer.writeItem(stack.copy());
            });
        }
        return InteractionResultHolder.success(stack);
    }

    // 从物品中获取槽位信息
    public static ItemStackHandler getInventory(ItemStack stack) {
        // 使用 Capability 或直接操作 NBT
        ItemStackHandler handler = new ItemStackHandler(2);
        handler.deserializeNBT(stack.getOrCreateTag().getCompound(INVENTORY));
        return handler;
    }

    // 保存到物品
    public static void setInventory(ItemStack stack) {
        ItemStackHandler handler = getInventory(stack);
        stack.getOrCreateTag().put(INVENTORY, handler.serializeNBT());
    }
}
