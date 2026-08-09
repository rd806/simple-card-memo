package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoPacketSave {

    private final String key;
    private final String content;

    public MemoPacketSave(String key, String content) {
        this.key = key;
        this.content = content;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(key);
        buffer.writeUtf(content);
    }

    public static MemoPacketSave decode(FriendlyByteBuf buffer) {
        String key = buffer.readUtf();
        String content = buffer.readUtf();
        return new MemoPacketSave(key, content);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientSetup.clientCache.put(key, content)));
        context.setPacketHandled(true);
    }
}
