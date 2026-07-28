package com.github.rd806.simplecardmemo.items.memomanager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MemoManagerItem extends Item {

    public MemoManagerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_manager.tooltip")
                .withStyle(ChatFormatting.GRAY));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            // 打开管理器界面
            Minecraft.getInstance().setScreen(new MemoManagerScreen());
        }
        // 消耗一个背包中的 Memo Viewer
        return InteractionResultHolder.success(stack);
    }
}
