package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.MemoCache;
import com.github.rd806.simplecardmemo.memo.manage.MemoConfig;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerSetup {

    public static MemoConfig serverConfig;
    public static MemoCache serverCache;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        SimpleCardMemo.LOGGER.info("Start SimpleCardMemo on the server!");
        serverConfig = new MemoConfig();
        serverCache = new MemoCache();
        serverConfig.preloadFiles(serverCache);
        SimpleCardMemo.LOGGER.info("Preload Files on the server!");
    }
}
