package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MailSend implements CustomPacketPayload {

    public static final Type<MailSend> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "mail_send"));

    private final String content;
    private final String receiver;
    private final String message;

    public MailSend(String content, String receiver, String message) {
        this.content = content;
        this.receiver = receiver;
        this.message = message;
    }

    public static final StreamCodec<FriendlyByteBuf, MailSend> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MailSend packet) {
            buf.writeUtf(packet.content);
            buf.writeUtf(packet.receiver);
            buf.writeUtf(packet.message);
        }

        @Override
        public @NotNull MailSend decode(FriendlyByteBuf buf) {
            String content = buf.readUtf();
            String receiver = buf.readUtf();
            String message = buf.readUtf();
            return new MailSend(content, receiver, message);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MailSend packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 检查是否为管理器界面
                if (!(player.containerMenu instanceof MailMenu mailMenu)) {
                    SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                    return;
                }
                ItemStack input = mailMenu.getItemStackHandler().getStackInSlot(MailMenu.INPUT_SLOT);
                // 复制一份对象存入服务器缓存
                ItemStack mail = input.copy();
                if (mail.isEmpty()) {
                    SimpleCardMemo.LOGGER.error("Memo is empty: {}", input);
                    return;
                }
                // 存入邮件系统
                MailKey key = new MailKey(player.getName().getString(), packet.receiver, System.currentTimeMillis(), mail.getHoverName().getString());
                MailSystem.putMail(key, mail);
                MailSystem.putContent(key, packet.content);
                // 消耗物品
                SimpleCardMemo.LOGGER.info("Mail {}:{} has been added!", key.sender() + "->" + key.receiver(), key.name());
                input.shrink(1);
                // 发送成功消息
                Channel.sendMailStatus(player, MailStatus.SUCCESS_SEND);
                sendMessage(player, packet.receiver, packet.message);}
        });
    }

    // 向目标玩家发送消息
    private static void sendMessage(ServerPlayer sender, String target, String message) {
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
