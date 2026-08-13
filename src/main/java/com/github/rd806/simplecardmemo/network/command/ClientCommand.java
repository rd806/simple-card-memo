package com.github.rd806.simplecardmemo.network.command;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class ClientCommand implements CustomPacketPayload {

    public static final Type<ClientCommand> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "client_command"));

    private final CommandType commandType;

    public ClientCommand(CommandType commandType) { this.commandType = commandType; }

    public static final StreamCodec<FriendlyByteBuf, ClientCommand> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, ClientCommand packet) {
            buf.writeEnum(packet.commandType);
        }

        @Override
        public @NotNull ClientCommand decode(FriendlyByteBuf buf) {
            CommandType commandType = buf.readEnum(CommandType.class);
            return new ClientCommand(commandType);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final ClientCommand packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Dist.CLIENT.isClient()) {
                switch (packet.commandType) {
                    case CACHE_INFO -> CacheSystem.getInfo();
                    case CACHE_CLEAR -> ClientSetup.clientCache.clear();
                }
            }
        });
    }
}
