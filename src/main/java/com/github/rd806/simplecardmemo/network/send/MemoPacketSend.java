package com.github.rd806.simplecardmemo.network.send;

import com.github.rd806.simplecardmemo.network.GetExistMemo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MemoPacketSend {

    private final ItemStack memo;
    private final String content;
    private final UUID receiver;

    public MemoPacketSend(ItemStack memo, String content, UUID receiver) {
        this.memo = memo;
        this.content = content;
        this.receiver = receiver;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItemStack(memo, false);
        buffer.writeUtf(content);
        buffer.writeUUID(receiver);
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacketSend decode(FriendlyByteBuf buffer) {
        ItemStack memo = buffer.readItem();
        String content = buffer.readUtf();
        UUID receiver = buffer.readUUID();
        return new MemoPacketSend(memo, content, receiver);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) { return; }
            // 目标物品
            if (!GetExistMemo.hasItemStack(sender, memo)) { return; }
            boolean consumeItem = GetExistMemo.consumeItemStack(sender, memo, 1);
            // 发送物品
            if (consumeItem) {
                ServerMemoCache.getInstance().addMemo(sender.getUUID(), receiver, memo, content);
            }
        });
        context.setPacketHandled(true);
    }
}
