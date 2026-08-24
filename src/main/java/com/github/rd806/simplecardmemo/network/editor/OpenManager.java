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

public class OpenManager {

    public OpenManager() {}

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf ignoredBuffer) {}

    // 解码：从网络缓冲区读取数据
    public static OpenManager decode(FriendlyByteBuf ignoredBuffer) { return new OpenManager(); }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            // 检查是否为管理器界面
            if (!(player.containerMenu instanceof InfoMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Info Menu!");
                return;
            }
            player.openMenu(new SimpleMenuProvider(
                    (id, inv, p) -> new ManagerMenu(id, inv),
                    Component.translatable("gui.simplecardmemo.manager_screen.title")
            ));
        });
        context.setPacketHandled(true);
    }
}
