package com.github.rd806.simplecardmemo.network;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.network.command.MemoCacheClear;
import com.github.rd806.simplecardmemo.network.command.MemoCacheInfo;
import com.github.rd806.simplecardmemo.network.get.MemoPacketGet;
import com.github.rd806.simplecardmemo.network.get.MemoPacketNew;
import com.github.rd806.simplecardmemo.network.send.MemoPacketReceive;
import com.github.rd806.simplecardmemo.network.send.MemoPacketSave;
import com.github.rd806.simplecardmemo.network.send.MemoPacketSend;
import com.github.rd806.simplecardmemo.network.send.MailStatusSend;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
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
                MemoPacketNew.class, MemoPacketNew::encode, MemoPacketNew::decode, MemoPacketNew::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketGet.class, MemoPacketGet::encode, MemoPacketGet::decode, MemoPacketGet::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketSend.class, MemoPacketSend::encode, MemoPacketSend::decode, MemoPacketSend::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketReceive.class, MemoPacketReceive::encode, MemoPacketReceive::decode, MemoPacketReceive::handle);
        // 状态数据包
        CHANNEL.registerMessage(
                packetId++,
                MailStatusSend.class, MailStatusSend::encode, MailStatusSend::decode, MailStatusSend::handle);
        // 缓存数据包
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketSave.class, MemoPacketSave::encode, MemoPacketSave::decode, MemoPacketSave::handle);
        // 命令数据包
        CHANNEL.registerMessage(
                packetId++,
                MemoCacheInfo.class, MemoCacheInfo::encode, MemoCacheInfo::decode, MemoCacheInfo::handle);
        CHANNEL.registerMessage(
                packetId++,
                MemoCacheClear.class, MemoCacheClear::encode, MemoCacheClear::decode, MemoCacheClear::handle);
    }
}
