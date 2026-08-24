package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.InfoMenu;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS
            = DeferredRegister.create(ForgeRegistries.MENU_TYPES, SimpleCardMemo.MODID);

    // 发送界面
    public static final RegistryObject<MenuType<MailMenu>> MAIL_MENU = registerMenuType("mail_menu", MailMenu::new);
    // 管理界面
    public static final RegistryObject<MenuType<ManagerMenu>> MANAGER_MENU = registerMenuType("manager_menu", ManagerMenu::new);
    // 编辑界面
    public static final RegistryObject<MenuType<InfoMenu>> INFO_MENU = registerMenuType("info_menu", InfoMenu::new);

    // 抽象构造方法
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
