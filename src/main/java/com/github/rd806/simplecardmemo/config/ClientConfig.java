package com.github.rd806.simplecardmemo.config;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class ClientConfig {

    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    // 页边距
    public enum Margin {
        WIDE,
        MEDIUM,
        NARROW,
    }

    // 页边距设置
    public static ModConfigSpec.EnumValue<Margin> PAGE_MARGIN;
    // 文本背景
    public static ModConfigSpec.BooleanValue TEXT_BACKGROUND;
    public static ModConfigSpec.ConfigValue<Integer> BACKGROUND_COLOR;


    public static ModConfigSpec init() {
        BUILDER.push("Text").translation(SimpleCardMemo.MODID + ".gui.config.text");
        PAGE_MARGIN = BUILDER
                .translation("config.simplecardmemo.page_margin")
                .comment("Page Margin: WIDE/MIDDLE/NARROW")
                .defineEnum("PageMargin", Margin.MEDIUM);
        TEXT_BACKGROUND = BUILDER
                .translation("config.simplecardmemo.text_background")
                .comment("Enable text background when viewing")
                .define("TextBackground", false);
        BACKGROUND_COLOR = BUILDER
                .translation("config.simplecardmemo.background_color")
                .comment("Color for the text background")
                .define("BackgroundColor", 0xFF020619);
        BUILDER.pop();

        return BUILDER.build();
    }
}
