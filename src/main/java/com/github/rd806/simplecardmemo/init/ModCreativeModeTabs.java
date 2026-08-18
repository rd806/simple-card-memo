package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.BuiltInList;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
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
                                // 加载内置列表
                                for (MemoInfo memoInfo : BuiltInList.BUILT_IN_MEMOS) {
                                    ItemStack item = GetExistMemo.setMemo(memoInfo);
                                    output.accept(item);
                                }
                            })
                            // 构建最终的 CreativeModeTab 实例
                            .build());


    public static void register(IEventBus eventBus){ CREATIVE_MODE_TABS.register(eventBus); }

    // 新的备忘录
    public static ItemStack newMemo() { return new ItemStack(ModItems.MEMO_VIEWER.value()); }
}
