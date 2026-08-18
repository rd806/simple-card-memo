package com.github.rd806.simplecardmemo.setup;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.compat.ConfigMenu;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.init.ModMenus;
import com.github.rd806.simplecardmemo.init.container.screen.MailScreen;
import com.github.rd806.simplecardmemo.init.container.screen.ManagerScreen;
import com.github.rd806.simplecardmemo.memo.BuiltInList;
import com.github.rd806.simplecardmemo.memo.cache.MemoContentCache;
import com.github.rd806.simplecardmemo.memo.cache.MemoScreenCache;
import com.github.rd806.simplecardmemo.memo.manage.MemoConfig;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(value = SimpleCardMemo.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = SimpleCardMemo.MODID, value = Dist.CLIENT)
public class ClientSetup {

    public static MemoConfig clientConfig;
    public static MemoContentCache clientContentCache;
    public static MemoScreenCache clientScreenCache;

    public ClientSetup(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, (container1, parent) ->
                ConfigMenu.buildScreen().setParentScreen(parent).build());
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        // 创建临时文件
        MemoLoader.createTempFile();
        // 内置列表
        BuiltInList.loadBuiltInMemos(Minecraft.getInstance().getResourceManager());
        // 客户端配置文件
        clientConfig = new MemoConfig();
        clientContentCache = new MemoContentCache();
        clientScreenCache = new MemoScreenCache();
        // 预加载文件
        if (CommonConfig.PRELOAD_FILES.get()) {
            clientConfig.preloadFiles(clientContentCache);
            SimpleCardMemo.LOGGER.info("Preload Files on the client!");
        }
    }

    // 注册GUI
    @SubscribeEvent
    private static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenus.MANAGER_MENU.get(), ManagerScreen::new);
        event.register(ModMenus.MAIL_MENU.get(), MailScreen::new);
    }
}
