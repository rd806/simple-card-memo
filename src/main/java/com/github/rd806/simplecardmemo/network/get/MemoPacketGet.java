package com.github.rd806.simplecardmemo.network.get;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.network.GetExistMemo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class MemoPacketGet {

    private final MemoInfo memoInfo;

    public MemoPacketGet(MemoInfo memoInfo) {
        this.memoInfo = memoInfo;
    }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(memoInfo.getMemoName());
        buffer.writeUtf(memoInfo.getMemoPath());
        buffer.writeUtf(memoInfo.getMemoAuthor());
        buffer.writeBoolean(memoInfo.isLocalFile());
        buffer.writeLong(memoInfo.getLastModified());
    }

    // 解码：从网络缓冲区读取数据
    public static MemoPacketGet decode(FriendlyByteBuf buffer) {
        String memoName = buffer.readUtf();
        String memoPath = buffer.readUtf();
        String author = buffer.readUtf();
        boolean isLocalFile = buffer.readBoolean();
        long lastModified = buffer.readLong();
        MemoInfo memoInfo = new MemoInfo(memoName, memoPath, author, isLocalFile, lastModified);
        return new MemoPacketGet(memoInfo);
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
            // 消耗和产出物品
            input.shrink(1);
            managerMenu.getItemHandler().setStackInSlot(ManagerMenu.OUTPUT_SLOT, GetExistMemo.setMemo(memoInfo));
        });
        context.setPacketHandled(true);
    }
}
