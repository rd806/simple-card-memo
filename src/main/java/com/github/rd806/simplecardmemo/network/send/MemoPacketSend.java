package com.github.rd806.simplecardmemo.network.send;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.init.MailStatus;
import com.github.rd806.simplecardmemo.memo.cache.ServerMemoCache;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MemoPacketSend {

    private final String content;
    private final String receiver;
    private final String message;

    public MemoPacketSend(String content, String receiver, String message) {
        this.content = content;
        this.receiver = receiver;
        this.message = message;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(content);
        buffer.writeUtf(receiver);
        buffer.writeUtf(message);
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacketSend decode(FriendlyByteBuf buffer) {
        String content = buffer.readUtf();
        String receiver = buffer.readUtf();
        String message = buffer.readUtf();
        return new MemoPacketSend(content, receiver, message);
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
            // 复制一份对象存入服务器缓存
            ItemStack mail = input.copy();
            // 目标物品
            if (mail.isEmpty()) {
                SimpleCardMemo.LOGGER.error("Memo is empty: {}", input);
                return;
            }
            String key = sender.getName().getString() + "->" + receiver;
            ServerMemoCache.addMemo(key, mail, content);
            SimpleCardMemo.LOGGER.info("Mail {}:{} has been added!", key, mail.getHoverName().getString());
            input.shrink(1);
            Channel.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> sender),
                    new MailStatusSend(MailStatus.SUCCESS_SEND)
            );
            sendMessage(sender, receiver, message);
        });
        context.setPacketHandled(true);
    }

    // 向目标玩家发送消息
    private void sendMessage(ServerPlayer sender, String target, String message) {
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
                player.displayClientMessage(Component.literal(message), false);
            }
        }
    }
}
