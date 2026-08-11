package com.github.rd806.simplecardmemo.memo.mail;

import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class MailSystem {

    private static final Map<MailKey, ItemStack> serverMails = new ConcurrentHashMap<>();
    private static final Map<MailKey, String> mailContents = new ConcurrentHashMap<>();

    // 处理信件
    public static void putMail(MailKey key, ItemStack stack) { serverMails.put(key, stack); }
    public static ItemStack getMail(MailKey key) { return serverMails.get(key); }

    public static void putContent(MailKey key, String content) { mailContents.put(key, content); }
    public static String getContent(MailKey key) { return mailContents.get(key); }

    public static void removeMail(MailKey key) { serverMails.remove(key); }
    public static void removeContent(MailKey key) { mailContents.remove(key); }

    // 获取所有信件
    public static Set<MailKey> getAllMails() { return serverMails.keySet(); }
}
