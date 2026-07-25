package com.github.rd806.todolist.init;

import com.github.rd806.todolist.Todolist;
import com.github.rd806.todolist.items.textviewer.TextViewerItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Todolist.MODID);

    public static final RegistryObject<Item> TEXT_VIEWER =
            ITEMS.register("text_viewer", () -> new TextViewerItem(new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> TEXT_EDITOR =
            ITEMS.register("text_editor", () -> new TextViewerItem(new Item.Properties().stacksTo(1)));
}
