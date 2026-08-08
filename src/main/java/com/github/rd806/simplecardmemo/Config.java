package com.github.rd806.simplecardmemo;

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
                .comment("Load local memos when start games")
                .define("LoadLocalFiles", true);
        PAGE_MARGIN = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.page_margin")
                .comment("Page Margin: WIDE/MIDDLE/NARROW")
                .defineEnum("PageMargin", Margin.MEDIUM);
        TEXT_BACKGROUND = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.text_background")
                .comment("Enable text background when viewing")
                .define("TextBackground", false);
        BACKGROUND_COLOR = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.background_color")
                .comment("Color for the text background")
                .define("BackgroundColor", 0xFF020619);
        CACHE_SIZE = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.cache")
                .comment("Define how many memos' content will be cached during the game")
                .defineInRange("CacheSize", 5, 5, 10);
        BUILDER.pop();

        BUILDER.push("Item").translation(SimpleCardMemo.MODID + ".gui.config.item");
        DATE_FORMAT = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.date_format")
                .comment("Date format for the memos")
                .defineEnum("DateFormat", DateFormat.ISO_LOCAL_DATE);
        BUILDER.pop();

        return BUILDER.build();
    }
}
