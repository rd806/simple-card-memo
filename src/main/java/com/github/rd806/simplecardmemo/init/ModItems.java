package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.item.MemoEditorItem;
import com.github.rd806.simplecardmemo.init.item.MemoMailItem;
import com.github.rd806.simplecardmemo.init.item.MemoManagerItem;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, SimpleCardMemo.MODID);

    public static final Holder<Item> MEMO_VIEWER =
            ITEMS.register("memo_viewer", () -> new MemoViewerItem(new Item.Properties().stacksTo(1)));
    public static final Holder<Item> MEMO_EDITOR =
            ITEMS.register("memo_editor", () -> new MemoEditorItem(new Item.Properties().stacksTo(1)));
    public static final Holder<Item> MEMO_MANAGER =
            ITEMS.register("memo_manager", () -> new MemoManagerItem(new Item.Properties().stacksTo(1)));
    public static final Holder<Item> MEMO_MAIL =
            ITEMS.register("memo_mail", () -> new MemoMailItem(new Item.Properties().stacksTo(1)));

    public static void register(IEventBus eventBus) { ITEMS.register(eventBus); }
}
