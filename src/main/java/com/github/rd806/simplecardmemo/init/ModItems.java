package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.item.MemoEditorItem;
import com.github.rd806.simplecardmemo.init.item.MemoManagerItem;
import com.github.rd806.simplecardmemo.init.item.MemoMailItem;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleCardMemo.MODID);

    public static final RegistryObject<Item> MEMO_VIEWER =
            ITEMS.register("memo_viewer", () -> new MemoViewerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MEMO_EDITOR =
            ITEMS.register("memo_editor", () -> new MemoEditorItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MEMO_MANAGER =
            ITEMS.register("memo_manager", () -> new MemoManagerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> MEMO_MAIL =
            ITEMS.register("memo_mail", () -> new MemoMailItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
