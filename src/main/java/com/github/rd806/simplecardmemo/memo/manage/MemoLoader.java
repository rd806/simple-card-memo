package com.github.rd806.simplecardmemo.memo.manage;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class MemoLoader {

    public static String loadText(MemoInfo memoInfo) {
        if (memoInfo.isLocalFile()) {
            return loadFromLocalFiles(memoInfo);
        } else {
            return loadFromUrl(memoInfo);
        }
    }

    // 从网络文件中获取
    private static String loadFromUrl(MemoInfo memoInfo) {
        String urlStr = memoInfo.getMemoPath();
        if (urlStr == null) {
            SimpleCardMemo.LOGGER.error("The Memo URL is null!");
            return null;
        }
        try {
            URI uri = new URI(urlStr);
            URL url = uri.toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(30000);
            connection.setRequestProperty("User-Agent", "Todolist");

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                SimpleCardMemo.LOGGER.error("HTTP error: {} - {}", responseCode, connection.getResponseMessage());
                return null;
            }
            // 获取内容类型和编码
            String contentType = connection.getContentType();
            String charset = "UTF-8"; // 默认编码
            if (contentType != null) {
                String[] parts = contentType.split(";");
                for (String part : parts) {
                    part = part.trim();
                    if (part.startsWith("charset=")) {
                        charset = part.substring(8);
                        break;
                    }
                }
            }
            // 读取内容
            StringBuilder content = new StringBuilder();
            try (InputStream inputStream = connection.getInputStream();
                 BufferedReader reader = new BufferedReader(
                         new InputStreamReader(inputStream, charset))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }
            return content.toString();
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to load file from url: {}", urlStr);
            return null;
        }
    }

    // 从本地文件中获取
    public static String loadFromLocalFiles(MemoInfo memoInfo) {
        String filepath = memoInfo.getMemoPath();
        if (filepath == null) {
            SimpleCardMemo.LOGGER.error("The Memo Path is null!");
            return null;
        }
        try {
           // 从资源包中加载
           ResourceLocation location = ResourceLocation.parse(filepath);
           ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
           Resource resource = resourceManager.getResource(location).orElse(null);
           // 检查来源
           if (resource != null) {
               StringBuilder content = new StringBuilder();
               try (BufferedReader reader = new BufferedReader(
                       new InputStreamReader(resource.open(), StandardCharsets.UTF_8))) {
                   String line;
                   while ((line = reader.readLine()) != null) {
                       content.append(line).append("\n");
                   }
               }
               return content.toString();
           } else {
               Path path = SimpleCardMemo.DATA_DIR.resolve(filepath);
               if (Files.exists(path)) {
                   return Files.readString(path);
               }
           }
        } catch (Exception e) {
           SimpleCardMemo.LOGGER.error("Failed to load file from local: {}", filepath);
        }
        return null;
    }
    
    // 保存到本地文件
    public static boolean saveToLocalFiles(String text, MemoInfo memoInfo) {
        String filepath = memoInfo.getMemoPath();
        String safeName = sanitizeFileName(filepath);
        Path path = SimpleCardMemo.DATA_DIR.resolve(safeName);
        try {
            Files.writeString(path, text);
            SimpleCardMemo.LOGGER.info("Successfully saved text to local file: {}", safeName);
            return true;
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to save file");
            return false;
        }
    }

    // 移除不安全字符
    private static String sanitizeFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9\\-_.]", "_");
    }

    // 删除文件
    public static boolean deleteLocalFiles(MemoInfo memoInfo) {
        try {
            String filePath = memoInfo.getMemoPath();
            if (Files.deleteIfExists(SimpleCardMemo.DATA_DIR.resolve(filePath))) {
                MemoConfig.MEMO_LIST.remove(memoInfo);
                MemoConfig.saveToConfig();
                SimpleCardMemo.LOGGER.info("Successfully delete local file: {}", filePath);
                return true;
            } else {
                SimpleCardMemo.LOGGER.error("Failed to delete local file: {}", filePath);
                return false;
            }
        } catch (IOException e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
            return false;
        }
    }

    // 创建临时文件
    public static void createTempFile() {
        try {
            Path path = SimpleCardMemo.DATA_DIR.resolve("temp.md");
            if (Files.notExists(path)) {
                Files.writeString(path, "This is the temp file.");
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to create temp.md");
        }
    }
}
