package com.github.rd806.simplecardmemo.network.get;

import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MemoListGet {

    public MemoListGet() {}

    public void encode(FriendlyByteBuf ignoredBuf) {}

    public static MemoListGet decode(FriendlyByteBuf ignoredBuf) { return new MemoListGet(); }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            Channel.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new MemoListReceive(ServerSetup.serverConfig.getMemoList())
            );
        });
        context.setPacketHandled(true);
    }
}
