package com.github.rd806.simplecardmemo.network.command;

import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoCacheClear {

    public MemoCacheClear() {}

    public void encode(FriendlyByteBuf ignoredBuffer) {}

    public static MemoCacheClear decode(FriendlyByteBuf ignoredBuffer) {
        return new MemoCacheClear();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.clientCache.clear()));
        context.setPacketHandled(true);
    }
}
