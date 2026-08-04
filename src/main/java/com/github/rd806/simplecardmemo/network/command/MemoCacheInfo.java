package com.github.rd806.simplecardmemo.network.command;

import com.github.rd806.simplecardmemo.memo.cache.ClientMemoCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoCacheInfo {

    public MemoCacheInfo() {}

    public void encode(FriendlyByteBuf ignoredBuffer) {}

    public static MemoCacheInfo decode(FriendlyByteBuf ignoredBuffer) {
        return new MemoCacheInfo();
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientMemoCache::getInfo));
        context.setPacketHandled(true);
    }
}
