package com.github.rd806.todolist.setup;

import com.github.rd806.todolist.Todolist;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerSetup {
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        Todolist.LOGGER.info("HELLO from server starting");
    }
}
