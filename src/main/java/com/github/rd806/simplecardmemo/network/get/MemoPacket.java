package com.github.rd806.simplecardmemo.network.get;

import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoPacket {

    private final MemoInfo memoInfo;

    public MemoPacket(MemoInfo memoInfo) {
        this.memoInfo = memoInfo;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(memoInfo.getMemoName());
        buffer.writeUtf(memoInfo.getMemoPath());
        buffer.writeUtf(memoInfo.getMemoAuthor());
        buffer.writeBoolean(memoInfo.isLocalFile());
        buffer.writeLong(memoInfo.getLastModified());
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacket decode(FriendlyByteBuf buffer) {
        String memoName = buffer.readUtf();
        String memoPath = buffer.readUtf();
        String author = buffer.readUtf();
        boolean isLocalFile = buffer.readBoolean();
        long lastModified = buffer.readLong();
        MemoInfo memoInfo = new MemoInfo(memoName, memoPath, author, isLocalFile, lastModified);
        return new MemoPacket(memoInfo);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            // 目标物品
            Item targetItem = ModItems.MEMO_VIEWER.get();
            boolean consumeItem = GetExistMemo.consumeItem(player, targetItem, 1);
            if (consumeItem) {
                player.displayClientMessage(
                        Component.translatable("simplecardmemo.item.memo_manager.export.success"),
                        false
                );
                GetExistMemo.getItem(player, memoInfo);
            } else {
                player.displayClientMessage(
                        Component.translatable("simplecardmemo.item.memo_manager.export.fail"),
                        false
                );
            }
        });
        context.setPacketHandled(true);
    }
}
