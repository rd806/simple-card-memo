package com.github.rd806.todolist;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Todolist.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    // 页边距
    public enum Margin {
        WIDE,
        MEDIUM,
        NARROW,
    }

    // 是否启用 Markdown 渲染
    public static ForgeConfigSpec.BooleanValue ENABLE_MARKDOWN;
    // 页边距设置
    public static ForgeConfigSpec.EnumValue<Margin> PAGE_MARGIN;

    public static ForgeConfigSpec init() {
        ENABLE_MARKDOWN = BUILDER
                .comment("Whether to enable markdown rendering.")
                .define("EnableMarkdown", true);

        PAGE_MARGIN = BUILDER
                .defineEnum("PageMargin", Margin.MEDIUM);

        return BUILDER.build();
    }
}
