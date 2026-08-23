package com.github.rd806.simplecardmemo.network.editor;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.InfoMenu;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class OpenManager implements CustomPacketPayload {

    public static final Type<OpenManager> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "open_manager"));


    public OpenManager() {}

    public static final StreamCodec<FriendlyByteBuf, OpenManager> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(@NotNull FriendlyByteBuf buf, @NotNull OpenManager packet) {}

        @Override
        public @NotNull OpenManager decode(@NotNull FriendlyByteBuf buf) { return new OpenManager(); }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final OpenManager ignoredPacket, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 检查是否为管理器界面
                if (!(player.containerMenu instanceof InfoMenu)) {
                    SimpleCardMemo.LOGGER.error("Not an Info Menu!");
                    return;
                }
                player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new ManagerMenu(id, inv),
                        Component.translatable("gui.simplecardmemo.manager_screen.title")
                ));
            }
        });
    }
}
