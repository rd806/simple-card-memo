package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, SimpleCardMemo.MODID);

    // 注册本模组的创造模式标签。
    // "TODO_LIST_TAB" 为该标签的注册名，会作为内部 ID 使用。
    public static final RegistryObject<CreativeModeTab> TODO_LIST_TAB =
            CREATIVE_MODE_TABS.register("simplecardmemo",
                    () -> CreativeModeTab.builder()
                            // 设置创造标签在界面中显示的图标
                            .icon(() -> new ItemStack(Items.STONE))
                            // 设置标签的显示名称
                            .title(Component.translatable(SimpleCardMemo.MODID + ".tab.simplecardmemo"))
                            // 定义该标签中显示的物品内容
                            // output.accept(...) 用于向标签中添加物品
                            .displayItems((itemDisplayParameters, output) -> {
                                output.accept(ModItems.TEXT_VIEWER.get());
                                output.accept(ModItems.TEXT_EDITOR.get());
                            })
                            // 构建最终的 CreativeModeTab 实例
                            .build());

    public static void register(IEventBus eventBus){
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
