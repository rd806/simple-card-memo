package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerSetup {

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        SimpleCardMemo.LOGGER.info("Start SimpleCardMemo on the server!");
    }
}
