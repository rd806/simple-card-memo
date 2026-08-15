package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleCardMemo.MODID);

    // 注册本模组的创造模式标签
    // "SIMPLE_CARD_MEMO_TAB" 为该标签的注册名，会作为内部 ID 使用
    public static final Holder<CreativeModeTab> SIMPLE_CARD_MEMO_TAB =
            CREATIVE_MODE_TABS.register("simplecardmemo",
                    () -> CreativeModeTab.builder()
                            // 设置创造标签在界面中显示的图标
                            .icon(() -> new ItemStack(ModItems.MEMO_VIEWER.value()))
                            // 设置标签的显示名称
                            .title(Component.translatable(SimpleCardMemo.MODID + ".tab.simplecardmemo"))
                            // 定义该标签中显示的物品内容
                            .displayItems((itemDisplayParameters, output) -> {
                                output.accept(ModItems.MEMO_EDITOR.value());
                                output.accept(ModItems.MEMO_MANAGER.value());
                                output.accept(ModItems.MEMO_MAIL.value());
                                output.accept(newMemo());
                                output.accept(memoGuide());
                                output.accept(hostServer());
                            })
                            // 构建最终的 CreativeModeTab 实例
                            .build());


    public static void register(IEventBus eventBus){ CREATIVE_MODE_TABS.register(eventBus); }

    // 新的备忘录
    public static ItemStack newMemo() { return new ItemStack(ModItems.MEMO_VIEWER.value()); }

    // 教程文件
    public static ItemStack memoGuide() {
        ItemStack item = new ItemStack(ModItems.MEMO_VIEWER.value());
        // 设置物品信息
        MemoViewerItem.setItemName(item, Component.translatable(SimpleCardMemo.MODID + ".item.memo_guide").getString());
        MemoViewerItem.setFilePath(item, SimpleCardMemo.MODID + ":sample/guide.md");
        MemoViewerItem.setDisplayName(item, "Guide");
        MemoViewerItem.setTextSource(item, false);
        MemoViewerItem.setAuthor(item, "RunicDolphin806");
        MemoViewerItem.setLastModified(item, 0);
        return item;
    }

    // Minecraft Java 开服教程
    private static ItemStack hostServer() {
        ItemStack item = new ItemStack(ModItems.MEMO_VIEWER.value());
        // 设置物品信息
        MemoViewerItem.setItemName(item, Component.translatable(SimpleCardMemo.MODID + ".item.minecraft_server").getString());
        MemoViewerItem.setFilePath(item, SimpleCardMemo.MODID + ":sample/minecraft_server.md");
        MemoViewerItem.setDisplayName(item, "Minecraft Server");
        MemoViewerItem.setTextSource(item, false);
        MemoViewerItem.setAuthor(item, "From Internet");
        MemoViewerItem.setLastModified(item, 0);
        return item;
    }

    // 添加到内置列表
    public static void builtInMemo() {
        ClientSetup.builtInMemos.add(MemoViewerItem.getMemoInfo(memoGuide()));
        ClientSetup.builtInMemos.add(MemoViewerItem.getMemoInfo(hostServer()));
    }
}
