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
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Channel {

    // 通道 ID 保持与版本一致
    private static final String PROTOCOL_VERSION = "1.2.1";
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
                MemoItemGet.class, MemoItemGet::encode, MemoItemGet::decode, MemoItemGet::handle);
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
                MemoListSend.class, MemoListSend::encode, MemoListSend::decode, MemoListSend::handle);
        // 缓存数据包
        CHANNEL.registerMessage(
                packetId++,
                MemoItemSend.class, MemoItemSend::encode, MemoItemSend::decode, MemoItemSend::handle);
        // 命令数据包
        CHANNEL.registerMessage(
                packetId++,
                ClientCommand.class, ClientCommand::encode, ClientCommand::decode, ClientCommand::handle);
    }

    // 获取服务端列表
    public static void getMemoList() { CHANNEL.send(PacketDistributor.SERVER.noArg(), new MemoListGet()); }
    // 发送服务端列表
    public static void sendMemoList(ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MemoListSend(ServerSetup.serverConfig.getMemoList()));
    }

    // 获取物品
    public static void getMemoItem(MemoInfo selectedMemo, MemoSource source) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new MemoItemGet(selectedMemo, source));
    }
    // 发送到客户端缓存
    public static void sendMemoItem(ServerPlayer player, String key, String value) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MemoItemSend(key, value));
    }

    // 发送信件
    public static void sendMail(String content, String target, String message) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new MailSend(content, target, message));
    }
    // 接收信件
    public static void receiveMail(String target) {
        CHANNEL.send(PacketDistributor.SERVER.noArg(), new MailReceive(target));
    }
    // 发送信件状态信息
    public static void sendMailStatus(ServerPlayer player, MailStatus status) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new MailStatusSend(status));
    }

    // 发送命令
    public static void sendCommand(ServerPlayer player, CommandType commandType) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new ClientCommand(commandType));
    }
}
