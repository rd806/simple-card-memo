package com.github.rd806.simplecardmemo.memo;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class BuiltInList {

    public static List<MemoInfo> BUILT_IN_MEMOS = new ArrayList<>();

    private static final Gson GSON = new Gson();

    // 加载内置列表
    public static void loadBuiltInMemos(ResourceManager manager) {
        InputStream stream;
        ResourceLocation res = ResourceLocation.tryBuild(SimpleCardMemo.MODID, "memos.json");
        Resource optional;
        if (res == null) {
            SimpleCardMemo.LOGGER.warn("memos.json not found!");
            return;
        }

        optional = manager.getResource(res).orElse(null);
        if (optional == null) {
            SimpleCardMemo.LOGGER.warn("Failed to load built-in memos.");
            return;
        }

        try {
            stream = optional.open();
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.warn("Failed to open resource {}", res, e);
            return;
        }

        BUILT_IN_MEMOS = GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8),
                new TypeToken<List<MemoInfo>>(){}.getType());
        SimpleCardMemo.LOGGER.info("Load {} memos from {}", BUILT_IN_MEMOS.size(), res);
    }
}
