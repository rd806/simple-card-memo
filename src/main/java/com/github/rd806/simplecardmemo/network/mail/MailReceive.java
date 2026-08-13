package com.github.rd806.simplecardmemo.network.mail;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class MailReceive implements CustomPacketPayload {

    public static final Type<MailReceive> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "mail_receive"));

    private final String sender;

    public MailReceive(String sender) { this.sender = sender; }

    public static final StreamCodec<FriendlyByteBuf, MailReceive> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MailReceive packet) {
            buf.writeUtf(packet.sender);
        }

        @Override
        public @NotNull MailReceive decode(FriendlyByteBuf buf) {
            String sender = buf.readUtf();
            return new MailReceive(sender);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MailReceive packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 检查是否为信箱界面
                if (!(player.containerMenu instanceof MailMenu mailMenu)) {
                    SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                    return;
                }
                // 获取物品
                ItemStack output = null;
                String content = null;
                String filePath = null;
                Set<MailKey> sets = MailSystem.getAllMails();
                for (MailKey mailKey : sets) {
                    // 匹配信件
                    if ((mailKey.sender().equals(packet.sender) || packet.sender.isEmpty())
                            && mailKey.receiver().equals(player.getName().getString())) {
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
                    Channel.sendMailStatus(player, MailStatus.EMPTY_RECEIVE);
                    return;
                }
                mailMenu.getItemStackHandler().setStackInSlot(MailMenu.OUTPUT_SLOT, output);
                Channel.saveMemoPacket(player, filePath, content);
                Channel.sendMailStatus(player, MailStatus.SUCCESS_RECEIVE);
            }
        });
    }
}
