package com.github.rd806.simplecardmemo.network;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.init.value.MemoSource;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.network.command.ClientCommand;
import com.github.rd806.simplecardmemo.network.command.CommandType;
import com.github.rd806.simplecardmemo.network.editor.NewMemo;
import com.github.rd806.simplecardmemo.network.editor.OpenInfo;
import com.github.rd806.simplecardmemo.network.editor.OpenManager;
import com.github.rd806.simplecardmemo.network.mail.MailReceive;
import com.github.rd806.simplecardmemo.network.mail.MailSend;
import com.github.rd806.simplecardmemo.network.mail.MailStatusSend;
import com.github.rd806.simplecardmemo.network.manager.MemoListGet;
import com.github.rd806.simplecardmemo.network.manager.MemoListSend;
import com.github.rd806.simplecardmemo.network.manager.MemoItemGet;
import com.github.rd806.simplecardmemo.network.manager.MemoItemSend;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.List;

public class Channel {
    public static final ResourceLocation CHANNEL_ID =
            ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "main");

    // 注册网络数据包
    public static void register(final RegisterPayloadHandlersEvent event) {
        // 获取注册器
        final PayloadRegistrar registrar = event.registrar(CHANNEL_ID.getNamespace())
                .versioned("1.2.1")
                .optional();

        registrar.playToServer(NewMemo.TYPE, NewMemo.STREAM_CODEC, NewMemo::handle);
        registrar.playToServer(OpenInfo.TYPE, OpenInfo.STREAM_CODEC, OpenInfo::handle);
        registrar.playToServer(OpenManager.TYPE, OpenManager.STREAM_CODEC, OpenManager::handle);

        registrar.playToServer(MemoListGet.TYPE, MemoListGet.STREAM_CODEC, MemoListGet::handle);
        registrar.playToClient(MemoListSend.TYPE, MemoListSend.STREAM_CODEC, MemoListSend::handle);

        registrar.playToServer(MemoItemGet.TYPE, MemoItemGet.STREAM_CODEC, MemoItemGet::handle);
        registrar.playToClient(MemoItemSend.TYPE, MemoItemSend.STREAM_CODEC, MemoItemSend::handle);

        registrar.playToServer(MailSend.TYPE, MailSend.STREAM_CODEC, MailSend::handle);
        registrar.playToServer(MailReceive.TYPE, MailReceive.STREAM_CODEC, MailReceive::handle);
        registrar.playToClient(MailStatusSend.TYPE, MailStatusSend.STREAM_CODEC, MailStatusSend::handle);

        registrar.playToClient(ClientCommand.TYPE, ClientCommand.STREAM_CODEC, ClientCommand::handle);
    }

    public static void sendNewMemo(ItemStack memo) { PacketDistributor.sendToServer(new NewMemo(memo)); }

    // 文件列表
    public static void getMemoList() { PacketDistributor.sendToServer(new MemoListGet()); }
    public static void sendMemoList(ServerPlayer player, List<MemoInfo> memoList) {
        PacketDistributor.sendToPlayer(player, new MemoListSend(memoList));
    }
    public static void openInfoMenu(int index) { PacketDistributor.sendToServer(new OpenInfo(index)); }
    public static void openManagerMenu() { PacketDistributor.sendToServer(new OpenManager()); }

    // 获取物品
    public static void getMemoItem(MemoInfo memo, MemoSource source) {
        PacketDistributor.sendToServer(new MemoItemGet(memo, source));
    }
    public static void sendMemoItem(ServerPlayer player, String key, String content) {
        PacketDistributor.sendToPlayer(player, new MemoItemSend(key, content));
    }

    // 信件系统
    public static void sendMail(String content, String receiver, String massage) {
        PacketDistributor.sendToServer(new MailSend(content, receiver, massage));
    }
    public static void receiveMail(String sender) { PacketDistributor.sendToServer(new MailReceive(sender)); }
    public static void sendMailStatus(ServerPlayer player, MailStatus status) {
        PacketDistributor.sendToPlayer(player, new MailStatusSend(status));
    }

    // 命令
    public static void sendCommand(ServerPlayer player, CommandType commandType) {
        PacketDistributor.sendToPlayer(player, new ClientCommand(commandType));
    }
}
