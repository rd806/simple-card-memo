package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.nio.file.Files;
import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = SimpleCardMemo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        SimpleCardMemo.LOGGER.info("HELLO FROM CLIENT SETUP");
        SimpleCardMemo.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        // 创建临时文件
        try {
            Path path = SimpleCardMemo.DATA_DIR.resolve("temp.md");
            if (Files.notExists(path)) {
                Files.writeString(path, "This is the temp file.");
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to create temp.md");
        }
    }
}
