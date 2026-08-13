package com.github.rd806.simplecardmemo;

import com.github.rd806.simplecardmemo.config.ClientConfig;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.init.MemoDataComponents;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.init.ModMenus;
import com.github.rd806.simplecardmemo.network.Channel;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(SimpleCardMemo.MODID)
public class SimpleCardMemo {

    public static final String MODID = "simplecardmemo";
    public static final Logger LOGGER = LogUtils.getLogger();
    // 数据文件目录
    public static final Path DATA_DIR = FMLPaths.GAMEDIR.get().resolve("data/simple_card_memo");

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public SimpleCardMemo(IEventBus modEventBus, ModContainer modContainer) {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(Channel::register);
        // 注册物品
        MemoDataComponents.DATA_COMPONENTS.register(modEventBus);
        ModItems.register(modEventBus);
        ModMenus.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        // 配置文件
        modContainer.registerConfig(ModConfig.Type.CLIENT, ClientConfig.init());
        modContainer.registerConfig(ModConfig.Type.COMMON, CommonConfig.init());
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
