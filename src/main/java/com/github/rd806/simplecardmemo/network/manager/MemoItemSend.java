package com.github.rd806.simplecardmemo.network.manager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MemoItemSend implements CustomPacketPayload {

    public static final Type<MemoItemSend> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "memo_packet_save"));

    private final String key;
    private final String content;

    public MemoItemSend(String key, String content) {
        this.key = key;
        this.content = content;
    }

    public static final StreamCodec<FriendlyByteBuf, MemoItemSend> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MemoItemSend packet) {
            buf.writeUtf(packet.key);
            buf.writeUtf(packet.content);
        }

        @Override
        public @NotNull MemoItemSend decode(FriendlyByteBuf buf) {
            String key = buf.readUtf();
            String content = buf.readUtf();
            return new MemoItemSend(key, content);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MemoItemSend packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Dist.CLIENT.isClient()) {
                ClientSetup.clientContentCache.put(packet.key, packet.content);
            }
        });
    }
}
