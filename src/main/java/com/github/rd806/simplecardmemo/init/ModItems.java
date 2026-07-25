package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.items.texteditor.TextEditorItem;
import com.github.rd806.simplecardmemo.items.textviewer.TextViewerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SimpleCardMemo.MODID);

    public static final RegistryObject<Item> TEXT_VIEWER =
            ITEMS.register("text_viewer", () -> new TextViewerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TEXT_EDITOR =
            ITEMS.register("text_editor", () -> new TextEditorItem(new Item.Properties().stacksTo(1)));
}
