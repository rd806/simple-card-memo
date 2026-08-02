package com.github.rd806.simplecardmemo.network.send;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.items.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.ClientMemoCache;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import com.github.rd806.simplecardmemo.memo.cache.ServerMemoCache;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoPacketReceive {

    private final String sender;

    public MemoPacketReceive(String sender) {
        this.sender = sender;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(sender);
    }

    public static MemoPacketReceive decode(FriendlyByteBuf buffer) {
        String sender = buffer.readUtf();
        return new MemoPacketReceive(sender);
    }

    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer receiver = contextSupplier.get().getSender();
            if (receiver == null) { return; }
            ServerMemoCache cache = ServerMemoCache.getInstance();
            // 检查是否为信箱界面
            if (!(receiver.containerMenu instanceof MailMenu mailMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                return;
            }
            // 获取物品信息
            String key = sender + ":" + receiver.getDisplayName().getString();
            MemoInfo memoInfo = cache.retrieveMemoItem(key);
            if (memoInfo == null) {
                SimpleCardMemo.LOGGER.error("Memo not found: {}", key);
            }
            ItemStack output = GetExistMemo.setMemo(memoInfo);
            // 设置物品
            mailMenu.getItemStackHandler().setStackInSlot(MailMenu.OUTPUT_SLOT, output);
            // 设置内容缓存
            boolean isLocal = MemoViewerItem.getTextSource(output);
            String filePath = MemoViewerItem.getFilePath(output);
            String content = cache.retrieveMemoContent(key);
            if (isLocal) {
                ClientMemoCache.put(filePath, content);
            }
            cache.clearMemo(sender, receiver.getName().getString());
        });
        context.setPacketHandled(true);
    }
}
