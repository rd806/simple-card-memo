package com.github.rd806.simplecardmemo.memo.mail;

import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MailSystem {

    private static final Map<MailKey, ItemStack> serverMails = new ConcurrentHashMap<>();
    private static final Map<String, String> mailCache = new ConcurrentHashMap<>();

    // 处理信件
    public static void putMail(MailKey key, ItemStack stack) { serverMails.put(key, stack); }
    public static ItemStack getMail(MailKey key) { return serverMails.get(key); }
    public static void removeMail(MailKey key) { serverMails.remove(key); }

    // 处理服务端缓存
    public static void putContent(String key, String content) { mailCache.put(key, content); }
    public static String getContent(String key) { return mailCache.get(key); }

    // 获取所有信件
    public static Set<MailKey> getAllMails() { return serverMails.keySet(); }
    // 获取内容缓存
    public static Set<String> getAllContents() { return mailCache.keySet(); }
}
