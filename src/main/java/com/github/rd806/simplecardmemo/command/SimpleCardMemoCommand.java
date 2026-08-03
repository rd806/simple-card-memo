package com.github.rd806.simplecardmemo.command;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.ServerMemoCache;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.command.MemoCacheClear;
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
    private static final String CLEAR = "clear";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT)
                .requires((source) -> source.hasPermission(2));
        LiteralArgumentBuilder<CommandSourceStack> cache = Commands.literal(CACHE);
        LiteralArgumentBuilder<CommandSourceStack> mail = Commands.literal(MAIL);
        LiteralArgumentBuilder<CommandSourceStack> info = Commands.literal(INFO);
        LiteralArgumentBuilder<CommandSourceStack> clear = Commands.literal(CLEAR);

        root.then(cache.then(clear.executes(SimpleCardMemoCommand::clearCache)));
        root.then(mail.then(info.executes(SimpleCardMemoCommand::showMail)));
        return root;
    }

    // 清理缓存
    private static int clearCache(CommandContext<CommandSourceStack> context) {
        try {
            // 发送网络包
            ServerPlayer player = context.getSource().getPlayer();
            Channel.CHANNEL.send(
                    PacketDistributor.PLAYER.with(() -> player),
                    new MemoCacheClear()
            );
            // 提示信息
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable(SimpleCardMemo.MODID + ".command.cache.clear"),
                        false
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
            ServerPlayer player = context.getSource().getPlayer();
            Set<String> set = ServerMemoCache.getInstance().getMemoKeys();
            // 显示列表
            if (set.isEmpty()) {
                SimpleCardMemo.LOGGER.info("No Mail is on the server!");
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable(SimpleCardMemo.MODID + ".command.mail.empty"),
                            false);
                }
            } else {
                SimpleCardMemo.LOGGER.info("The following mails are available:");
                if (player != null) {
                    player.displayClientMessage(
                            Component.translatable(SimpleCardMemo.MODID + ".command.mail.info"),
                            false);
                }
                for (String key : set) {
                    SimpleCardMemo.LOGGER.info(key);
                    if (player != null) {
                        player.displayClientMessage(Component.literal(key), false);
                    }
                }
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

}
