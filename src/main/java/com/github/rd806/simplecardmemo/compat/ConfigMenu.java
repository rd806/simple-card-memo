package com.github.rd806.simplecardmemo.compat;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.config.ClientConfig;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ConfigMenu {

    public static ConfigBuilder buildScreen() {
        // 新建构建器
        ConfigBuilder builder = ConfigBuilder.create().setTitle(Component.translatable("cloth.simplecardmemo.config.title"));
        builder.setGlobalized(true);
        builder.setGlobalizedExpanded(false);
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        // 文本预览
        ConfigCategory textSettings = builder.getOrCreateCategory(Component.translatable("cloth.simplecardmemo.config.text"));
        buildTextSettings(entryBuilder, textSettings);
        // 通用设置
        ConfigCategory itemSettings = builder.getOrCreateCategory(Component.translatable("cloth.simplecardmemo.config.common"));
        buildCommonSettings(entryBuilder, itemSettings);

        return builder;
    }

    // 文本预览配置
    private static void buildTextSettings(ConfigEntryBuilder entryBuilder, ConfigCategory textSettings) {
        // 页边距
        textSettings.addEntry(
                entryBuilder.startEnumSelector(Component.translatable("config.simplecardmemo.page_margin"), ClientConfig.Margin.class, ClientConfig.PAGE_MARGIN.get())
                        .setDefaultValue(ClientConfig.Margin.MEDIUM)
                        .setTooltip(Component.translatable("config.simplecardmemo.page_margin.tooltip"))
                        .setSaveConsumer(pageMargin -> ClientConfig.PAGE_MARGIN.set(pageMargin))
                        .build());
        // 文本背景
        textSettings.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("config.simplecardmemo.text_background"), ClientConfig.TEXT_BACKGROUND.get())
                        .setDefaultValue(false)
                        .setTooltip(Component.translatable("config.simplecardmemo.text_background.tooltip"))
                        .setSaveConsumer(textBackground -> ClientConfig.TEXT_BACKGROUND.set(textBackground))
                        .build());
        // 背景颜色
        textSettings.addEntry(
                entryBuilder.startAlphaColorField(Component.translatable("config.simplecardmemo.background_color"), ClientConfig.BACKGROUND_COLOR.get())
                        .setDefaultValue(0xFF020619)
                        .setTooltip(Component.translatable("config.simplecardmemo.background_color.tooltip"))
                        .setSaveConsumer(backgroundColor -> ClientConfig.BACKGROUND_COLOR.set(backgroundColor))
                        .build());
    }

    // 物品预览配置
    private static void buildCommonSettings(ConfigEntryBuilder entryBuilder, ConfigCategory commonSettings) {
        // 日期风格
        commonSettings.addEntry(
                entryBuilder.startEnumSelector(Component.translatable("config.simplecardmemo.date_format"), CommonConfig.DateFormat.class, CommonConfig.DATE_FORMAT.get())
                        .setDefaultValue(CommonConfig.DateFormat.ISO_LOCAL_DATE)
                        .setTooltip(Component.translatable("config.simplecardmemo.date_format.tooltip"))
                        .setSaveConsumer(dateFormat -> CommonConfig.DATE_FORMAT.set(dateFormat))
                        .build());
        // 是否预加载本地文件
        commonSettings.addEntry(
                entryBuilder.startBooleanToggle(Component.translatable("config.simplecardmemo.load_local_files"), CommonConfig.PRELOAD_FILES.get())
                        .setDefaultValue(true)
                        .setTooltip(Component.translatable("config.simplecardmemo.load_local_files.tooltip"))
                        .setSaveConsumer(loadLocalFiles -> CommonConfig.PRELOAD_FILES.set(loadLocalFiles))
                        .build());
        // 缓冲区大小
        commonSettings.addEntry(
                entryBuilder.startIntField(Component.translatable("config.simplecardmemo.cache"), CommonConfig.CACHE_SIZE.get())
                        .setDefaultValue(20)
                        .setTooltip(Component.translatable("config.simplecardmemo.cache.tooltip"))
                        .setSaveConsumer(cache -> CommonConfig.CACHE_SIZE.set(cache))
                        .build());
    }
}
