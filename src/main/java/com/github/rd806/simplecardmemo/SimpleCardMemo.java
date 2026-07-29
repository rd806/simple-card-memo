package com.github.rd806.simplecardmemo;

import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.network.Channel;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SimpleCardMemo.MODID)
public class SimpleCardMemo {

    public static final String MODID = "simplecardmemo";
    public static final Logger LOGGER = LogUtils.getLogger();
    // 数据文件目录
    public static final Path DATA_DIR = FMLPaths.GAMEDIR.get().resolve("data/simple_card_memo");

    public SimpleCardMemo(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        // 创建物品和物品栏
        ModItems.ITEMS.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        Channel.registerGetMemo();
        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        context.registerConfig(ModConfig.Type.COMMON, Config.init());
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // 创建文件目录
        try {
            Files.createDirectories(DATA_DIR);
        } catch (IOException e) {
            LOGGER.error("Failed to create directory {}", DATA_DIR);
        }
    }
}
