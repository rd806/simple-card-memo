package com.github.rd806.simplecardmemo.command;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.memo.cache.ServerMemoCache;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.command.MemoCacheClear;
import com.github.rd806.simplecardmemo.network.command.MemoCacheInfo;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

import java.util.Set;

public class SimpleCardMemoCommand {

    private static final String ROOT = "simplecardmemo";
    private static final String CACHE = "cache";
    private static final String MAIL = "mail";
    private static final String INFO = "info";
    private static final String CONTENT = "content";
    private static final String CLEAR = "clear";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT)
                .requires((source) -> source.hasPermission(2));
        LiteralArgumentBuilder<CommandSourceStack> cache = Commands.literal(CACHE);
        LiteralArgumentBuilder<CommandSourceStack> mail = Commands.literal(MAIL);
        LiteralArgumentBuilder<CommandSourceStack> info = Commands.literal(INFO);
        LiteralArgumentBuilder<CommandSourceStack> content = Commands.literal(CONTENT);
        LiteralArgumentBuilder<CommandSourceStack> clear = Commands.literal(CLEAR);

        root.then(cache.then(info.executes(SimpleCardMemoCommand::showCache)));
        root.then(cache.then(clear.executes(SimpleCardMemoCommand::clearCache)));
        root.then(mail.then(info.executes(SimpleCardMemoCommand::showMail)));
        root.then(mail.then(content.executes(SimpleCardMemoCommand::showServerContent)));
        root.then(mail.then(clear.executes(SimpleCardMemoCommand::clearMail)));
        return root;
    }

    // 查看缓存
    private static int showCache(CommandContext<CommandSourceStack> context) {
        try {
            // 发送网络包
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                Channel.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new MemoCacheInfo()
                );
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 清理缓存
    private static int clearCache(CommandContext<CommandSourceStack> context) {
        try {
            // 发送网络包
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                Channel.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new MemoCacheClear()
                );
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 显示信件内容
    private static int showMail(CommandContext<CommandSourceStack> context) {
        try {
            Set<MailKey> sets = MailSystem.getAllMails();
            // 显示列表
            if (sets.isEmpty()) {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail.empty"),
                        false);
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail.info"),
                        false);
                // 显示列表
                for (MailKey set : sets) {
                    String message = "§7S: §r" + set.receiver()
                                    + " §7R: §r" + set.receiver()
                                    + " §7T: §r" + CommonConfig.getDateString(set.timestamp())
                                    + " §7N: §r" + set.name();
                    // 发送信息
                    context.getSource().sendSuccess(() -> Component.literal(message), false);
                }
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 展示服务端缓存
    private static int showServerContent(CommandContext<CommandSourceStack> context) {
        try {
            Set<String> sets = ServerMemoCache.getAll();
            // 显示列表
            if (sets.isEmpty()) {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail_content.empty"),
                        false);
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail_content.info"),
                        false);
                // 显示列表
                for (String set : sets) {
                    // 发送信息
                    context.getSource().sendSuccess(() -> Component.literal(set), false);
                }
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 清除信件缓存
    private static int clearMail(CommandContext<CommandSourceStack> context) {
        try {
            context.getSource().sendSuccess(
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail.clear"),
                    false);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }
}
