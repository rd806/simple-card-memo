package com.github.rd806.simplecardmemo.compat;

import com.github.rd806.simplecardmemo.Config;
import com.github.rd806.simplecardmemo.SimpleCardMemo;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfigMenu {

    public static ConfigBuilder buildScreen() {
        // 新建构建器
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Component.translatable(SimpleCardMemo.MODID + ".gui.config.title"));
        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        // 文本预览
        ConfigCategory textSettings = builder.getOrCreateCategory(Component.translatable(SimpleCardMemo.MODID + ".gui.config.text"));
        buildTextSettings(entryBuilder, textSettings);
        // 物品信息
        ConfigCategory itemSettings = builder.getOrCreateCategory(Component.translatable(SimpleCardMemo.MODID + ".gui.config.item"));
        buildItemSettings(entryBuilder, itemSettings);

        return builder;
    }

    // 文本预览配置
    private static void buildTextSettings(ConfigEntryBuilder entryBuilder, ConfigCategory textSettings) {
        // 是否加载本地文件
        textSettings.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable(SimpleCardMemo.MODID + ".config.load_local_files"), Config.LOAD_LOCAL_FILES.get())
                        .setDefaultValue(true)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.load_local_files.tooltip"))
                        .setSaveConsumer(loadLocalFiles -> Config.LOAD_LOCAL_FILES.set(loadLocalFiles))
                        .build());
        // 缓冲区大小
        textSettings.addEntry(
                entryBuilder.startIntField(Component.translatable(SimpleCardMemo.MODID + ".config.cache"), Config.CACHE_SIZE.get())
                        .setDefaultValue(5)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.cache.tooltip"))
                        .setSaveConsumer(cache -> Config.CACHE_SIZE.set(cache))
                        .build());
        // 页边距
        textSettings.addEntry(
                entryBuilder.startEnumSelector(Component.translatable(SimpleCardMemo.MODID + ".config.page_margin"), Config.Margin.class, Config.Margin.MEDIUM)
                        .setDefaultValue(Config.Margin.MEDIUM)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.page_margin.tooltip"))
                        .setSaveConsumer(pageMargin -> Config.PAGE_MARGIN.set(pageMargin))
                        .build());
        // 文本背景
        textSettings.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable(SimpleCardMemo.MODID + ".config.text_background"), Config.TEXT_BACKGROUND.get())
                        .setDefaultValue(false)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.text_background.tooltip"))
                        .setSaveConsumer(textBackground -> Config.TEXT_BACKGROUND.set(textBackground))
                        .build());
        // 背景颜色
        textSettings.addEntry(
                entryBuilder.startAlphaColorField(Component.translatable(SimpleCardMemo.MODID + ".config.background_color"), Config.BACKGROUND_COLOR.get())
                        .setDefaultValue(0xFF020619)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.background_color.tooltip"))
                        .setSaveConsumer(backgroundColor -> Config.BACKGROUND_COLOR.set(backgroundColor))
                        .build());
    }

    // 物品预览配置
    private static void buildItemSettings(ConfigEntryBuilder entryBuilder, ConfigCategory itemSettings) {
        // 日期风格
        itemSettings.addEntry(
                entryBuilder.startEnumSelector(Component.translatable(SimpleCardMemo.MODID + ".config.date_format"), Config.DateFormat.class, Config.DateFormat.ISO_LOCAL_DATE)
                        .setDefaultValue(Config.DateFormat.ISO_LOCAL_DATE)
                        .setTooltip(Component.translatable(SimpleCardMemo.MODID + ".config.date_format.tooltip"))
                        .setSaveConsumer(dateFormat -> Config.DATE_FORMAT.set(dateFormat))
                        .build());
    }
}
