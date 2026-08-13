package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.screen.MailScreen;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MailStatusSend implements CustomPacketPayload {

    public static final Type<MailStatusSend> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "mail_status_send"));

    private final MailStatus status;

    public MailStatusSend(MailStatus status) { this.status = status; }

    public static final StreamCodec<FriendlyByteBuf, MailStatusSend> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MailStatusSend packet) {
            buf.writeEnum(packet.status);
        }

        @Override
        public @NotNull MailStatusSend decode(FriendlyByteBuf buf) {
            MailStatus status = buf.readEnum(MailStatus.class);
            return new MailStatusSend(status);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MailStatusSend packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Dist.CLIENT.isClient()) { MailScreen.setStatus(packet.status); }
        });
    }
}
