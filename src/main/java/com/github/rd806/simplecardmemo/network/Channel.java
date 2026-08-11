package com.github.rd806.simplecardmemo.network;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.value.MailStatus;
import com.github.rd806.simplecardmemo.init.value.MemoSource;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.network.command.CommandType;
import com.github.rd806.simplecardmemo.network.command.ClientCommand;
import com.github.rd806.simplecardmemo.network.editor.NewMemo;
import com.github.rd806.simplecardmemo.network.manager.*;
import com.github.rd806.simplecardmemo.network.mail.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Channel {

    private static final String PROTOCOL_VERSION = "1";
    // 网络通道
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResourceLocation.parse(SimpleCardMemo.MODID + ":main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    // 数据包初始id
    private static int packetId = 1;

    public static void register() {
        // 物品数据包
        CHANNEL.registerMessage(
                packetId++,
                NewMemo.class, NewMemo::encode, NewMemo::decode, NewMemo::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketGet.class, MemoPacketGet::encode, MemoPacketGet::decode, MemoPacketGet::handle);
        CHANNEL.registerMessage(
                packetId++,
                MailSend.class, MailSend::encode, MailSend::decode, MailSend::handle);
        CHANNEL.registerMessage(
                packetId++,
                MailReceive.class, MailReceive::encode, MailReceive::decode, MailReceive::handle);
        // 状态数据包
        CHANNEL.registerMessage(
                packetId++,
                MailStatusSend.class, MailStatusSend::encode, MailStatusSend::decode, MailStatusSend::handle);
        // 文件列表数据包
        CHANNEL.registerMessage(
                packetId++,
                MemoListGet.class, MemoListGet::encode, MemoListGet::decode, MemoListGet::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoListReceive.class, MemoListReceive::encode, MemoListReceive::decode, MemoListReceive::handle);
        // 缓存数据包
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketSave.class, MemoPacketSave::encode, MemoPacketSave::decode, MemoPacketSave::handle);
        // 命令数据包
        CHANNEL.registerMessage(
                packetId++,
                ClientCommand.class, ClientCommand::encode, ClientCommand::decode, ClientCommand::handle);
    }

    // 获取物品
    public static void getMemoItem(MemoInfo selectedMemo, MemoSource source) {
        Channel.CHANNEL.send(PacketDistributor.SERVER.noArg(), new MemoPacketGet(selectedMemo, source));
    }

    // 发送信件状态信息
    public static void sendMailStatus(ServerPlayer player, MailStatus status) {
        Channel.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MailStatusSend(status));
    }

    // 发送到客户端缓存
    public static void sendToClientCache(ServerPlayer player, String key, String value) {
        Channel.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MemoPacketSave(key, value));
    }

    // 发送命令
    public static void sendCommand(ServerPlayer player, CommandType commandType) {
        Channel.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientCommand(commandType));
    }
}
