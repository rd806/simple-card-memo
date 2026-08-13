package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

// 1.21.1 及以上使用组件系统，不使用 NBT
public class MemoDataComponents {
    // 使用标准的 create 方法
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS =
            DeferredRegister.create(BuiltInRegistries.DATA_COMPONENT_TYPE, SimpleCardMemo.MODID);

    // 文件路径 - String
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> FILE_PATH =
            DATA_COMPONENTS.register("file_path",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build()
            );

    // 显示名称 - String
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> DISPLAY_NAME =
            DATA_COMPONENTS.register("display_name",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build()
            );

    // 作者 - String
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> AUTHOR =
            DATA_COMPONENTS.register("author",
                    () -> DataComponentType.<String>builder()
                            .persistent(Codec.STRING)
                            .networkSynchronized(ByteBufCodecs.STRING_UTF8)
                            .build()
            );

    // 最后修改时间 - Long
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Long>> LAST_MODIFIED =
            DATA_COMPONENTS.register("last_modified",
                    () -> DataComponentType.<Long>builder()
                            .persistent(Codec.LONG)
                            .networkSynchronized(ByteBufCodecs.VAR_LONG)
                            .build()
            );

    // 是否外部文件 - Boolean
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Boolean>> IS_EXTERNAL =
            DATA_COMPONENTS.register("is_external",
                    () -> DataComponentType.<Boolean>builder()
                            .persistent(Codec.BOOL)
                            .networkSynchronized(ByteBufCodecs.BOOL)
                            .build()
            );
}
