package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.compat.ConfigMenu;
import com.github.rd806.simplecardmemo.init.container.screen.MailScreen;
import com.github.rd806.simplecardmemo.init.container.screen.ManagerScreen;
import com.github.rd806.simplecardmemo.init.ModMenus;
import com.github.rd806.simplecardmemo.memo.manage.MemoConfig;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = SimpleCardMemo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
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
