package com.github.rd806.simplecardmemo.network.editor;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.InfoMenu;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenInfo {

    private final int index;

    public OpenInfo(int index) { this.index = index; }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) { buffer.writeInt(this.index); }

    // 解码：从网络缓冲区读取数据
    public static OpenInfo decode(FriendlyByteBuf buffer) {
        int index = buffer.readInt();
        return new OpenInfo(index);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            // 检查是否为管理器界面
            if (!(player.containerMenu instanceof ManagerMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Manager Menu!");
                return;
            }

            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new InfoMenu(id, inv, index),
                    Component.translatable("gui.simplecardmemo.info_screen.title")
            ));
        });
        context.setPacketHandled(true);
    }
}
