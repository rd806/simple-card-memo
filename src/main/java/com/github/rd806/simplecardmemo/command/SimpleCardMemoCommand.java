package com.github.rd806.simplecardmemo.command;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.memo.mail.MailKey;
import com.github.rd806.simplecardmemo.memo.mail.MailSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.command.CommandType;
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Set;

public class SimpleCardMemoCommand {

    private static final String ROOT = "simplecardmemo";
    private static final String CLIENT = "client";
    private static final String SERVER = "server";
    private static final String MAIL = "mail";
    private static final String CACHE = "cache";
    private static final String INFO = "info";
    private static final String CLEAR = "clear";
    private static final String RELOAD = "reload";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT)
                .requires((source) -> source.hasPermission(2));
        LiteralArgumentBuilder<CommandSourceStack> client = Commands.literal(CLIENT);
        LiteralArgumentBuilder<CommandSourceStack> server = Commands.literal(SERVER);
        LiteralArgumentBuilder<CommandSourceStack> cache = Commands.literal(CACHE);
        LiteralArgumentBuilder<CommandSourceStack> mail = Commands.literal(MAIL);
        LiteralArgumentBuilder<CommandSourceStack> info = Commands.literal(INFO);
        LiteralArgumentBuilder<CommandSourceStack> clear = Commands.literal(CLEAR);
        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal(RELOAD);

        root.then(client.then(cache.then(info.executes(SimpleCardMemoCommand::showClientCache))));
        root.then(client.then(cache.then(clear.executes(SimpleCardMemoCommand::clearClientCache))));
        root.then(server.then(cache.then(info.executes(SimpleCardMemoCommand::showServerCache))));
        root.then(server.then(cache.then(clear.executes(SimpleCardMemoCommand::clearServerCache))));
        root.then(server.then(reload.executes(SimpleCardMemoCommand::reload)));
        root.then(mail.then(info.executes(SimpleCardMemoCommand::showMail)));
        root.then(mail.then(clear.executes(SimpleCardMemoCommand::clearMail)));
        return root;
    }

    // 查看客户端缓存
    private static int showClientCache(CommandContext<CommandSourceStack> context) {
        try {
            // 发送网络包
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                Channel.sendCommand(player, CommandType.CACHE_INFO);
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 清理客户端缓存
    private static int clearClientCache(CommandContext<CommandSourceStack> context) {
        try {
            // 发送网络包
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                Channel.sendCommand(player, CommandType.CACHE_CLEAR);
            }
            context.getSource().sendSuccess(
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.cache.clear"),
                    false
            );
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 展示服务端缓存
    private static int showServerCache(CommandContext<CommandSourceStack> context) {
        try {
            Set<String> sets = ServerSetup.serverCache.getCache().keySet();
            // 显示列表
            if (sets.isEmpty()) {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.cache.empty"),
                        false);
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.cache.info"),
                        false);
                // 显示列表
                for (String set : sets) {
                    // 发送信息
                    context.getSource().sendSuccess(() -> Component.literal("§a▍ §7" + set), false);
                }
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

    // 清除服务器缓存
    private static int clearServerCache(CommandContext<CommandSourceStack> context) {
        try {
            ServerSetup.serverCache.clear();
            context.getSource().sendSuccess(
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.cache.clear"),
                    false
            );
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

    // 重新加载
    private static int reload(CommandContext<CommandSourceStack> context) {
        try {
            ServerSetup.serverConfig.reload();
            ServerSetup.serverConfig.preloadFiles(ServerSetup.serverCache);
            context.getSource().sendSuccess(
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.reload"),
                    false);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }
}
