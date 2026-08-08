package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;
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
            // 检查是否为信箱界面
            if (!(receiver.containerMenu instanceof MailMenu mailMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                return;
            }
            // 获取物品
            ItemStack output = null;
            String content = null;
            String filePath = null;
            Set<MailKey> sets = MailSystem.getAllMails();
            for (MailKey mailKey : sets) {
                if (mailKey.sender().equals(sender)) {
                    output = MailSystem.getMail(mailKey);
                    filePath = MemoViewerItem.getFilePath(output);
                    content = MailSystem.getContent(filePath);
                    // 移除对应的信件
                    MailSystem.removeMail(mailKey);
                    break;
                }
            }

            // 设置物品
            if (output == null) {
                SimpleCardMemo.LOGGER.error("Memo not found");
                Channel.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> receiver),
                        new MailStatusSend(MailStatus.EMPTY_RECEIVE)
                );
                return;
            }
            mailMenu.getItemStackHandler().setStackInSlot(MailMenu.OUTPUT_SLOT, output);
            Channel.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> receiver),
                    new MemoPacketSave(filePath, content)
            );
            Channel.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> receiver),
                    new MailStatusSend(MailStatus.SUCCESS_RECEIVE)
            );
        });
        context.setPacketHandled(true);
    }
}
