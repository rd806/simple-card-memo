package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.Set;
import java.util.function.Supplier;

public class MailReceive {

    private final String sender;

    public MailReceive(String sender) {
        this.sender = sender;
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(sender);
    }

    public static MailReceive decode(FriendlyByteBuf buffer) {
        String sender = buffer.readUtf();
        return new MailReceive(sender);
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
                if (mailKey.sender().equals(sender) && mailKey.receiver().equals(receiver.getName().getString())) {
                    output = MailSystem.getMail(mailKey);
                    filePath = MemoViewerItem.getFilePath(output);
                    content = MailSystem.getContent(mailKey);
                    // 移除对应的信件
                    MailSystem.removeMail(mailKey);
                    MailSystem.removeContent(mailKey);
                    break;
                }
            }
            // 设置物品
            if (output == null) {
                SimpleCardMemo.LOGGER.error("Memo not found");
                Channel.sendMailStatus(receiver, MailStatus.EMPTY_RECEIVE);
                return;
            }
            mailMenu.getItemStackHandler().setStackInSlot(MailMenu.OUTPUT_SLOT, output);
            Channel.sendToClientCache(receiver, filePath, content);
            Channel.sendMailStatus(receiver, MailStatus.SUCCESS_RECEIVE);
        });
        context.setPacketHandled(true);
    }
}
