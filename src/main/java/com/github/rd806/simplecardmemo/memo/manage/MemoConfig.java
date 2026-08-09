package com.github.rd806.simplecardmemo.memo.manage;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.MemoCache;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MemoConfig {
    // 配置 JSON 文件
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String CONFIG = SimpleCardMemo.MODID + "-memo.json";
    private static final Path CONFIG_PATH = FMLPaths.CONFIGDIR.get().resolve(CONFIG);
    // 哈希映射表（用于去重和查找）
    private static final Map<String, MemoInfo> MEMO_MAP = new ConcurrentHashMap<>();
    // 文件列表
    private static List<MemoInfo> MEMO_LIST = new ArrayList<>();

    public List<MemoInfo> getMemoList() { return MEMO_LIST; }

    public MemoConfig() {
        generateConfig();
        SimpleCardMemo.LOGGER.info("Successfully loaded from config: {}", CONFIG);
    }

    // 生成配置文件
    public void generateConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            if (!Files.exists(CONFIG_PATH)) {
                createDefaultConfig();
            }
            loadFromJson();
            localFilesToJson();
            SimpleCardMemo.LOGGER.info("Loaded {} memos", MEMO_MAP.size());
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Couldn't create directory at {}", CONFIG_PATH);
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
    }

    // 创建默认配置文件
    private void createDefaultConfig() {
        try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
            String defaultConfig = """
                    {
                      "memos": [
                    
                      ]
                    }
                    """;
            writer.write(defaultConfig);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Fail to create default config!", e);
        }
    }

    // 加载 JSON 配置文件
    public void loadFromJson() {
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
            var json = GSON.fromJson(reader, JsonWrapper.class);
            if (json != null && json.memos != null) {
                MEMO_LIST = json.memos;
                MEMO_MAP.clear();
                for (MemoInfo info : json.memos) {
                    MEMO_MAP.put(info.getMemoPath(), info);
                    SimpleCardMemo.LOGGER.info("Loading Memo: {}", info.getMemoName());
                }
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to load memos from {}", CONFIG_PATH, e);
        }
    }

    // 从本地文件加载
    public void localFilesToJson() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(SimpleCardMemo.DATA_DIR)) {
            for (Path path : stream) {
                try {
                    // 跳过 temp.md 文件
                    if (path.getFileName().toString().equals("temp.md")) {
                        continue;
                    }
                    // 跳过重复文件
                    if (MEMO_MAP.containsKey(path.getFileName().toString())) {
                        continue;
                    }
                    MemoInfo info = new MemoInfo();
                    info.setMemoPath(path.getFileName().toString());
                    info.setMemoName(path.getFileName().toString());
                    info.setMemoAuthor("Default");
                    info.setExternal(true);
                    info.setLastModified(path.toFile().lastModified());
                    MEMO_LIST.add(info);
                    MEMO_MAP.put(info.getMemoPath(), info);
                } catch (Exception e) {
                    SimpleCardMemo.LOGGER.error("Failed to load memo file from: {}", path);
                }
            }
            saveToConfig();
            SimpleCardMemo.LOGGER.info("Loaded memos from {}", SimpleCardMemo.DATA_DIR);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to load local memos", e);
        }
    }

    // 预加载文件
    public void preloadFiles(MemoCache memoCache) {
        for (MemoInfo info : MEMO_LIST) {
            String content = MemoLoader.loadText(info);
            if (content != null) {
                memoCache.getCache().put(info.getMemoPath(), content);
            }
        }
    }

    // 重新加载
    public void reload() {
        MEMO_LIST.clear();
        MEMO_MAP.clear();
        loadFromJson();
        localFilesToJson();
    }

    // 保存配置到 JSON 文件
    public void saveToConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH)) {
                JsonWrapper wrapper = new JsonWrapper();
                wrapper.memos = MEMO_LIST;
                GSON.toJson(wrapper, writer);
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Couldn't save config to {}", CONFIG_PATH);
        }
    }

    private static class JsonWrapper {
        List<MemoInfo> memos;
    }
}
