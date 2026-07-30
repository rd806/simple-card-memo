package com.github.rd806.simplecardmemo.network;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.network.get.MemoPacketGet;
import com.github.rd806.simplecardmemo.network.send.MemoPacketReceive;
import com.github.rd806.simplecardmemo.network.send.MemoPacketSend;
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

    public static void registerGetMemo() {
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketGet.class,
                MemoPacketGet::encode,
                MemoPacketGet::decode,
                MemoPacketGet::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketSend.class,
                MemoPacketSend::encode,
                MemoPacketSend::decode,
                MemoPacketSend::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                MemoPacketReceive.class,
                MemoPacketReceive::encode,
                MemoPacketReceive::decode,
                MemoPacketReceive::handle
        );
    }
}
