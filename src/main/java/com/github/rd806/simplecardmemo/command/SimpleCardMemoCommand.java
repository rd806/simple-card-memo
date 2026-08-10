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
import net.minecraft.ChatFormatting;
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
        // 为每个路径创建独立的节点
        // 每个命令节点应该是唯一的实例，不可在不同的命令路径中共享同一个 LiteralArgumentBuilder
        root.then(Commands.literal(CLIENT)
                .then(Commands.literal(CACHE)
                        .then(Commands.literal(INFO).executes(SimpleCardMemoCommand::showClientCache))
                        .then(Commands.literal(CLEAR).executes(SimpleCardMemoCommand::clearClientCache))
                )
        );

        root.then(Commands.literal(SERVER)
                .then(Commands.literal(CACHE)
                        .then(Commands.literal(INFO).executes(SimpleCardMemoCommand::showServerCache))
                        .then(Commands.literal(CLEAR).executes(SimpleCardMemoCommand::clearServerCache))
                )
                .then(Commands.literal(RELOAD).executes(SimpleCardMemoCommand::reload))
        );

        root.then(Commands.literal(MAIL)
                .then(Commands.literal(INFO).executes(SimpleCardMemoCommand::showMail))
                .then(Commands.literal(CLEAR).executes(SimpleCardMemoCommand::clearMail))
        );
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
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.client_cache.clear")
                            .withStyle(ChatFormatting.GRAY),
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
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.server_cache.empty"),
                        false);
            } else {
                context.getSource().sendSuccess(
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.server_cache.info")
                                .withStyle(ChatFormatting.GREEN),
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
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.server_cache.clear")
                            .withStyle(ChatFormatting.GRAY),
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
                        () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail.info")
                                .withStyle(ChatFormatting.GRAY),
                        false);
                // 显示列表
                int id = 1;
                for (MailKey set : sets) {
                    String message = id + ". " + "§7S: §r" + set.receiver()
                                    + " §7R: §r" + set.receiver()
                                    + " §7T: §r" + CommonConfig.getDateString(set.timestamp())
                                    + " §7N: §r" + set.name();
                    // 发送信息
                    context.getSource().sendSuccess(() -> Component.literal(message), false);
                    id++;
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
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.mail.clear")
                            .withStyle(ChatFormatting.GRAY),
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
                    () -> Component.translatable(SimpleCardMemo.MODID + ".command.reload")
                            .withStyle(ChatFormatting.GRAY),
                    false);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }
}
