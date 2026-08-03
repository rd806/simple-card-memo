package com.github.rd806.simplecardmemo.network.send;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.items.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.ServerMemoCache;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoPacketSend {

    private final ItemStack stack;
    private final String content;
    private final String receiver;

    public MemoPacketSend(ItemStack stack, String content, String receiver) {
        this.stack = stack;
        this.content = content;
        this.receiver = receiver;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeItemStack(stack, false);
        buffer.writeUtf(content);
        buffer.writeUtf(receiver);
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacketSend decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        String content = buffer.readUtf();
        String receiver = buffer.readUtf();
        return new MemoPacketSend(stack, content, receiver);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer sender = context.getSender();
            if (sender == null) { return; }
            // 检查是否为管理器界面
            if (!(sender.containerMenu instanceof MailMenu mailMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                return;
            }
            ItemStack input = mailMenu.getItemStackHandler().getStackInSlot(MailMenu.INPUT_SLOT);
            MemoInfo memoInfo = MemoViewerItem.getMemoInfo(input);
            // 目标物品
            if (!input.isEmpty()) {
                String key = sender.getDisplayName().getString() + ":" + receiver;
                ServerMemoCache.getInstance().addMemo(key, memoInfo, content);
                SimpleCardMemo.LOGGER.info("Memo {} has been added!", key);
                input.shrink(1);
                sendMessage(sender, receiver);
            } else {
                SimpleCardMemo.LOGGER.error("The input memo is empty!");
            }
        });
        context.setPacketHandled(true);
    }

    // 向目标玩家发送消息
    private void sendMessage(ServerPlayer sender, String target) {
        // 获取 MinecraftServer 实例
        MinecraftServer server = sender.getServer();
        if (server == null) {
            SimpleCardMemo.LOGGER.error("The server is null!");
            return;
        }
        // 获取 PlayerList（管理所有玩家的类）
        PlayerList playerList = server.getPlayerList();
        // 遍历所有在线玩家并比对名称（不区分大小写）
        for (ServerPlayer player : playerList.getPlayers()) {
            if (player.getName().getString().equalsIgnoreCase(target)) {
                // 构造发送消息
                String message = I18n.get(SimpleCardMemo.MODID + ".memo_mail.send");
                message = sender.getName().getString() + message;
                player.displayClientMessage(Component.translatable(message), false);
            }
        }
    }
}
