package com.github.rd806.simplecardmemo.network.manager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MemoListGet implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MemoListGet> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "memo_list_get"));

    public MemoListGet() {}

    public static final StreamCodec<FriendlyByteBuf, MemoListGet> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buffer, @NotNull MemoListGet value) {}

        @Override
        public @NotNull MemoListGet decode(@NotNull FriendlyByteBuf buffer) {
            return new MemoListGet();
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MemoListGet ignoredPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                Channel.sendMemoList(player, ServerSetup.serverConfig.getMemoList());
            }
        });
    }
}
