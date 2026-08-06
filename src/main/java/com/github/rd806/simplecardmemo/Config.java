package com.github.rd806.simplecardmemo;

import net.minecraft.client.resources.language.I18n;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SimpleCardMemo.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    // 页边距
    public enum Margin {
        WIDE,
        MEDIUM,
        NARROW,
    }
    // 日期显示风格
    public enum DateFormat {
        ISO_LOCAL_DATE,
        ISO_LOCAL_DATE_TIME,
        RFC_1123_DATE_TIME
    }

    // 读取本地文件
    public static ForgeConfigSpec.BooleanValue LOAD_LOCAL_FILES;
    // 页边距设置
    public static ForgeConfigSpec.EnumValue<Margin> PAGE_MARGIN;
    // 文本背景
    public static ForgeConfigSpec.BooleanValue TEXT_BACKGROUND;
    public static ForgeConfigSpec.ConfigValue<Integer> BACKGROUND_COLOR;
    // 缓冲区大小
    public static ForgeConfigSpec.ConfigValue<Integer> CACHE_SIZE;
    // 日期风格
    public static ForgeConfigSpec.EnumValue<DateFormat> DATE_FORMAT;

    public static ForgeConfigSpec init() {
        BUILDER.push("Text").translation(SimpleCardMemo.MODID + ".gui.config.text");
        LOAD_LOCAL_FILES = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.load_local_files")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.load_local_files.tooltip"))
                .define("LoadLocalFiles", true);
        PAGE_MARGIN = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.page_margin")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.page_margin.tooltip"))
                .defineEnum("PageMargin", Margin.MEDIUM);
        TEXT_BACKGROUND = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.text_background")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.text_background.tooltip"))
                .define("TextBackground", false);
        BACKGROUND_COLOR = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.background_color")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.background_color.tooltip"))
                .define("BackgroundColor", 0xFF020619);
        CACHE_SIZE = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.cache")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.cache.tooltip"))
                .defineInRange("CacheSize", 5, 5, 10);
        BUILDER.pop();

        BUILDER.push("Item").translation(SimpleCardMemo.MODID + ".gui.config.item");
        DATE_FORMAT = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.date_format")
                .comment(I18n.get(SimpleCardMemo.MODID + ".config.date_format.tooltip"))
                .defineEnum("DateFormat", DateFormat.ISO_LOCAL_DATE);
        BUILDER.pop();

        return BUILDER.build();
    }
}
