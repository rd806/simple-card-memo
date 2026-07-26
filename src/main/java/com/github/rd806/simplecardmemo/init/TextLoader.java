package com.github.rd806.simplecardmemo.init;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TextLoader {

    public static String loadText(String string, boolean isLocalFile) {
        if (isLocalFile) {
            // 先尝试从资源包中获取
            String text = loadFromResources(string);
            if (text == null) {
                text = loadFromLocalFiles(string);
            }
            return text;
        } else {
            return loadFromUrl(string);
        }
    }

    // 从网络文件中获取
    public static String loadFromUrl(String urlStr) {
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

    // 从资源包中加载文件
    public static String loadFromResources(String filepath) {
        try {
            // 资源路径格式：assets/你的modid/ + filePath
            ResourceLocation location = ResourceLocation.parse(SimpleCardMemo.MODID + ":sample/" + filepath);

            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            Resource resource = resourceManager.getResource(location).orElse(null);

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
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to load file from resources: {}", filepath);
        }
        return null;
    }

    // 从本地文件中获取
    public static String loadFromLocalFiles(String filepath) {
       try {
           Path path = SimpleCardMemo.DATA_DIR.resolve(filepath);
           if (Files.exists(path)) {
               return Files.readString(path);
           }
       } catch (Exception e) {
           SimpleCardMemo.LOGGER.error("Failed to load file from local: {}", filepath);
       }
       return "Could not load text file from: " + filepath;
    }

    // 保存到本地文件
    public static boolean saveToLocalFiles(String filePath, String text) {
        String safeName = sanitizeFileName(filePath);
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
}
