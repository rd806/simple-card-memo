package com.github.rd806.simplecardmemo.network.send;

import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.items.memoviewer.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.network.GetExistMemo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class MemoPacketReceive {

    private final UUID sender;

    public MemoPacketReceive(UUID sender) {
        this.sender = sender;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUUID(sender);
    }

    public static MemoPacketReceive decode(FriendlyByteBuf buffer) {
        return new MemoPacketReceive(buffer.readUUID());
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer receiver = contextSupplier.get().getSender();
            if (receiver == null) { return; }
            ServerMemoCache cache = ServerMemoCache.getInstance();
            // 获取物品信息
            ItemStack memo = cache.retrieveMemoItem(sender, receiver.getUUID());
            if (!memo.getItem().equals(ModItems.MEMO_VIEWER.get())) {
                return;
            } else {
                boolean isLocal = MemoViewerItem.getTextSource(memo);
                String filePath = MemoViewerItem.getFilePath(memo);
                String content = cache.retrieveMemoContent(sender, receiver.getUUID());
                if (isLocal) {
                    CacheSystem.put(filePath, content);
                }
            }
            GetExistMemo.receiverItem(receiver, memo);
        });
        context.setPacketHandled(true);
    }
}
