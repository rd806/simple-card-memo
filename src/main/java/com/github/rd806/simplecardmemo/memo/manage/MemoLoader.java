package com.github.rd806.simplecardmemo.memo.manage;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.setup.ClientSetup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class MemoLoader {
    // 从外部文件中获取
    public static String loadText(MemoInfo memoInfo) {
        String filePath = memoInfo.getMemoPath();
        if (filePath == null) {
            SimpleCardMemo.LOGGER.error("The Memo path is null!");
            return null;
        }
        if (isUrl(filePath)) {
            try {
                // 再尝试网络加载
                URI uri = new URI(filePath);
                URL url = uri.toURL();
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(30000);
                connection.setRequestProperty("User-Agent", "SimpleCardMemo");

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
                SimpleCardMemo.LOGGER.error("Failed to load file from url: {}", filePath, e);
                return null;
            }
        } else {
            try {
                Path path = SimpleCardMemo.DATA_DIR.resolve(filePath);
                if (Files.exists(path)) {
                    return Files.readString(path);
                }
            } catch (IOException e) {
                SimpleCardMemo.LOGGER.error("Failed to load file from local: {}", filePath, e);
                return null;
            }
        }
        return null;
    }
    
    // 保存到本地文件
    public static boolean saveToLocal(String text, MemoInfo memoInfo) {
        String filepath = memoInfo.getMemoPath();
        String safeName = sanitizeFileName(filepath);
        Path path = SimpleCardMemo.DATA_DIR.resolve(safeName);
        try {
            Files.writeString(path, text);
            SimpleCardMemo.LOGGER.info("Successfully saved text to local file: {}", safeName);
            return true;
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Failed to save file", e);
            return false;
        }
    }

    // 移除不安全字符
    private static String sanitizeFileName(String name) {
        // 暂时仅支持英文、数字、下划线
        return name.replaceAll("[^a-zA-Z0-9\\-_.]", "_");
    }

    // 删除文件
    public static boolean deleteLocalFiles(MemoInfo memoInfo) {
        try {
            // 内部资源文件无法删除
            if (!memoInfo.isExternal()) { return false; }
            String filePath = memoInfo.getMemoPath();
            if (Files.deleteIfExists(SimpleCardMemo.DATA_DIR.resolve(filePath))) {
                ClientSetup.clientConfig.getMemoList().remove(memoInfo);
                ClientSetup.clientConfig.saveToConfig();
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
            SimpleCardMemo.LOGGER.error("Failed to create temp.md", e);
        }
    }

    // 检查是否为 URL
    private static boolean isUrl(String path) {
        if (path == null) return false;
        // 更完整的 URL 检测
        String lowerPath = path.toLowerCase();
        return lowerPath.startsWith("http://") ||
                lowerPath.startsWith("https://") ||
                lowerPath.startsWith("ftp://");
    }
}
