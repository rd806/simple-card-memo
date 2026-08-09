package com.github.rd806.simplecardmemo.network.get;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.mail.MemoPacketSave;
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class MemoPacketGet {

    private final MemoInfo memoInfo;
    private final boolean fromServer;

    public MemoPacketGet(MemoInfo memoInfo, boolean fromServer) {
        this.memoInfo = memoInfo;
        this.fromServer = fromServer;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(memoInfo.getMemoName());
        buffer.writeUtf(memoInfo.getMemoPath());
        buffer.writeUtf(memoInfo.getMemoAuthor());
        buffer.writeBoolean(memoInfo.isExternal());
        buffer.writeLong(memoInfo.getLastModified());
        buffer.writeBoolean(fromServer);
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacketGet decode(FriendlyByteBuf buffer) {
        String memoName = buffer.readUtf();
        String memoPath = buffer.readUtf();
        String author = buffer.readUtf();
        boolean external = buffer.readBoolean();
        long lastModified = buffer.readLong();
        MemoInfo memoInfo = new MemoInfo(memoName, memoPath, author, external, lastModified);
        boolean fromServer = buffer.readBoolean();
        return new MemoPacketGet(memoInfo, fromServer);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            // 检查是否为管理器界面
            if (!(player.containerMenu instanceof ManagerMenu managerMenu)) {
                SimpleCardMemo.LOGGER.error("Not a Manager Menu!");
                return;
            }
            // 检查输入槽
            ItemStack input = managerMenu.getItemHandler().getStackInSlot(ManagerMenu.INPUT_SLOT);
            if (input.isEmpty()) { return; }
            // 检查输出槽
            ItemStack output = managerMenu.getItemHandler().getStackInSlot(ManagerMenu.OUTPUT_SLOT);
            if (!output.isEmpty()) { return; }
            // 如果是服务端文件还需要传递内容
            if (fromServer) {
                String filePath = memoInfo.getMemoPath();
                String content = ServerSetup.serverCache.get(filePath);
                if (content == null) {
                    content = MemoLoader.loadText(memoInfo);
                }
                Channel.CHANNEL.send(
                        PacketDistributor.PLAYER.with(() -> player),
                        new MemoPacketSave(filePath, content)
                );
            }
            // 消耗和产出物品
            input.shrink(1);
            managerMenu.getItemHandler().setStackInSlot(ManagerMenu.OUTPUT_SLOT, GetExistMemo.setMemo(memoInfo));
        });
        context.setPacketHandled(true);
    }
}
