package com.github.rd806.simplecardmemo.command;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.cache.ClientMemoCache;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class SimpleCardMemoCommand {

    private static final String ROOT = "simplecardmemo";
    private static final String CLEAR_CACHE = "clear_cache";

    public static LiteralArgumentBuilder<CommandSourceStack> get() {
        LiteralArgumentBuilder<CommandSourceStack> root = Commands.literal(ROOT)
                .requires((source) -> source.hasPermission(2));

        LiteralArgumentBuilder<CommandSourceStack> clearCache = Commands.literal(CLEAR_CACHE);

        root.then(clearCache.executes(SimpleCardMemoCommand::clearCache));
        return root;
    }

    // 清理缓存
    private static int clearCache(CommandContext<CommandSourceStack> context) {
        try {
            ClientMemoCache.clear();
            ServerPlayer player = context.getSource().getPlayer();
            if (player != null) {
                player.displayClientMessage(
                        Component.translatable(SimpleCardMemo.MODID + ".command.clear_cache"),
                        false
                );
            }
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error(e.getMessage());
        }
        return Command.SINGLE_SUCCESS;
    }

}
