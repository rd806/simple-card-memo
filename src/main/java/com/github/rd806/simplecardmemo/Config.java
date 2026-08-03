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

    // 页边距设置
    public static ForgeConfigSpec.EnumValue<Margin> PAGE_MARGIN;
    // 缓冲区大小
    public static ForgeConfigSpec.IntValue CACHE_SIZE;
    // 日期风格
    public static ForgeConfigSpec.EnumValue<DateFormat> DATE_FORMAT;

    public static ForgeConfigSpec init() {
        PAGE_MARGIN = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.page_margin")
                .defineEnum("PageMargin", Margin.MEDIUM);

        CACHE_SIZE = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.cache")
                .defineInRange("CacheSize", 5, 5, 10);

        DATE_FORMAT = BUILDER
                .translation(SimpleCardMemo.MODID + ".config.date_format")
                .defineEnum("DateFormat", DateFormat.ISO_LOCAL_DATE);

        return BUILDER.build();
    }
}
