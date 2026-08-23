package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.MemoContentCache;
import com.github.rd806.simplecardmemo.memo.manage.MemoConfig;
import com.github.rd806.simplecardmemo.network.Channel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Map;

@Mod(value = SimpleCardMemo.MODID)
@EventBusSubscriber(modid = SimpleCardMemo.MODID)
public class ServerSetup {

    public static MemoConfig serverConfig;
    public static MemoContentCache serverCache;

    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        SimpleCardMemo.LOGGER.info("Start SimpleCardMemo on the server!");
        serverConfig = new MemoConfig();
        serverCache = new MemoContentCache();
        serverConfig.preloadFiles(serverCache);
        SimpleCardMemo.LOGGER.info("Preload Files on the server!");
    }

    // 玩家进入服务器发送缓存
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            for (Map.Entry<String, String> entry :serverCache.getCache().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                Channel.sendMemoItem(serverPlayer, key, value);
            }
            serverPlayer.displayClientMessage(
                    Component.translatable("message.simplecardmemo.player.login"),
                    false
            );
        }
    }

}
