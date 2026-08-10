package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.init.container.screen.MailScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MailStatusSend {

    private final MailStatus status;

    public MailStatusSend(MailStatus status) {
        this.status = status;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(status);
    }

    public static MailStatusSend decode(FriendlyByteBuf buffer) {
        MailStatus status = buffer.readEnum(MailStatus.class);
        return new MailStatusSend(status);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> MailScreen.setStatus(status)));
        context.setPacketHandled(true);
    }
}
