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

public class OpenInfo implements CustomPacketPayload {

    public static final Type<OpenInfo> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "open_info"));

    private final int index;

    public OpenInfo(int index) {
        this.index = index;
    }

    public static final StreamCodec<FriendlyByteBuf, OpenInfo> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, OpenInfo packet) {
            buf.writeInt(packet.index);
        }

        @Override
        public @NotNull OpenInfo decode(FriendlyByteBuf buf) {
            int index = buf.readInt();
            return new OpenInfo(index);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final OpenInfo packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 检查是否为管理器界面
                if (!(player.containerMenu instanceof ManagerMenu)) {
                    SimpleCardMemo.LOGGER.error("Not a Mail Menu!");
                    return;
                }

                player.openMenu(new SimpleMenuProvider(
                        (id, inv, p) -> new InfoMenu(id, inv, packet.index),
                        Component.translatable("gui.simplecardmemo.info_screen.title")
                ));
            }
        });
    }
}
