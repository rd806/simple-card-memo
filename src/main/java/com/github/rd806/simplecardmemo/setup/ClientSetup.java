package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.compat.ConfigMenu;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.init.container.screen.MailScreen;
import com.github.rd806.simplecardmemo.init.container.screen.ManagerScreen;
import com.github.rd806.simplecardmemo.init.ModMenus;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.MemoContentCache;
import com.github.rd806.simplecardmemo.memo.cache.MemoScreenCache;
import com.github.rd806.simplecardmemo.memo.manage.MemoConfig;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SimpleCardMemo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class  ClientSetup {

    public static MemoConfig clientConfig;
    public static MemoContentCache clientContentCache;
    public static MemoScreenCache clientScreenCache;
    // 内置文件列表
    public static List<MemoInfo> builtInMemos = new ArrayList<>();

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 创建临时文件
        MemoLoader.createTempFile();
        // 注册GUI
        registerScreens();
        // 配置界面
        if (ModList.get().isLoaded("cloth_config")) {
            SimpleCardMemo.fmlContext.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) ->
                            ConfigMenu.buildScreen().setParentScreen(parent).build()));
        }
        // 客户端配置文件
        clientConfig = new MemoConfig();
        clientContentCache = new MemoContentCache();
        clientScreenCache = new MemoScreenCache();
        // 预加载文件
        if (CommonConfig.PRELOAD_FILES.get()) {
            clientConfig.preloadFiles(clientContentCache);
            SimpleCardMemo.LOGGER.info("Preload Files on the client!");
        }
        ModCreativeModeTabs.builtInMemo();
    }

    // 注册GUI
    private static void registerScreens() {
        MenuScreens.register(
                ModMenus.MAIL_MENU.get(),
                MailScreen::new
        );
        MenuScreens.register(
                ModMenus.MANAGER_MENU.get(),
                ManagerScreen::new
        );
    }
}
