package com.github.rd806.simplecardmemo.config;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraftforge.common.ForgeConfigSpec;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class CommonConfig {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    // 日期显示风格
    public enum DateFormat {
        ISO_LOCAL_DATE,
        ISO_LOCAL_DATE_TIME,
        RFC_1123_DATE_TIME
    }

    // 日期风格
    public static ForgeConfigSpec.EnumValue<DateFormat> DATE_FORMAT;
    // 读取本地文件
    public static ForgeConfigSpec.BooleanValue PRELOAD_FILES;
    // 缓冲区大小
    public static ForgeConfigSpec.ConfigValue<Integer> CACHE_SIZE;

    public static ForgeConfigSpec init() {
        BUILDER.push("Common").translation(SimpleCardMemo.MODID + ".gui.config.common");
        DATE_FORMAT = BUILDER
                .translation("config.simplecardmemo.date_format")
                .comment("Date format for the memos")
                .defineEnum("DateFormat", DateFormat.ISO_LOCAL_DATE);
        PRELOAD_FILES = BUILDER
                .translation("config.simplecardmemo.load_local_files")
                .comment("Preload local memos when start games")
                .define("PreloadFiles", true);
        CACHE_SIZE = BUILDER
                .translation("config.simplecardmemo.cache")
                .comment("Define how many memos' content will be cached during the game")
                .defineInRange("CacheSize", 20, 5, 100);
        BUILDER.pop();
        return BUILDER.build();
    }

    // 获取格式化的时间字符串
    public static String getDateString(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        String lastModified = "";
        switch (DATE_FORMAT.get()) {
            case ISO_LOCAL_DATE -> lastModified = DateTimeFormatter.ISO_LOCAL_DATE
                    .format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            case ISO_LOCAL_DATE_TIME -> lastModified = DateTimeFormatter.ISO_LOCAL_DATE_TIME
                    .format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).withNano(0));
            case RFC_1123_DATE_TIME -> lastModified = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.US)
                    .format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }
        return lastModified;
    }
}
