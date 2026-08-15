package com.github.rd806.simplecardmemo.network.command;

import com.github.rd806.simplecardmemo.memo.CacheSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientCommand {

    private final CommandType commandType;

    public ClientCommand(CommandType commandType) { this.commandType = commandType; }

    public void encode(FriendlyByteBuf buf) { buf.writeEnum(commandType); }

    public static ClientCommand decode(FriendlyByteBuf buf) {
        CommandType commandType = buf.readEnum(CommandType.class);
        return new ClientCommand(commandType);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            switch (commandType) {
                case CACHE_INFO -> CacheSystem.getCache();
                case CACHE_CLEAR -> CacheSystem.clearCache();
            }
        }));
        context.setPacketHandled(true);
    }
}
