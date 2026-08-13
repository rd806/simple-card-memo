package com.github.rd806.simplecardmemo.network.editor;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public record NewMemo(ItemStack stack) implements CustomPacketPayload {

    public static final Type<NewMemo> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "new_memo"));

    public static final StreamCodec<RegistryFriendlyByteBuf, NewMemo> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.OPTIONAL_STREAM_CODEC,  // 使用 ItemStack 的编解码器
                    NewMemo::stack,
                    NewMemo::new
            );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final NewMemo packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ItemStack stack = packet.stack;
                // 消耗和产出物品
                if (GetExistMemo.consumeItem(player.getInventory(),
                        new ItemStack(ModItems.MEMO_EDITOR.value()), 1)) {
                    player.getInventory().add(stack);
                }
            }
        });
    }
}
