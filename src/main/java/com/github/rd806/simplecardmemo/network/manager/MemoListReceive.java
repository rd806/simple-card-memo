package com.github.rd806.simplecardmemo.network.manager;

import com.github.rd806.simplecardmemo.init.container.screen.ManagerScreen;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class MemoListReceive {

    private final List<MemoInfo> memoList;

    public MemoListReceive(List<MemoInfo> memoList) {
        this.memoList = memoList;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(memoList.size());
        for (MemoInfo memoInfo : memoList) {
            buf.writeUtf(memoInfo.getMemoName());
            buf.writeUtf(memoInfo.getMemoPath());
            buf.writeUtf(memoInfo.getMemoAuthor());
            buf.writeBoolean(memoInfo.isExternal());
            buf.writeLong(memoInfo.getLastModified());
        }
    }

    public static MemoListReceive decode(FriendlyByteBuf buf) {
        List<MemoInfo> memoList = new ArrayList<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String memoName = buf.readUtf();
            String memoPath = buf.readUtf();
            String memoAuthor = buf.readUtf();
            boolean external = buf.readBoolean();
            long lastModified = buf.readLong();
            MemoInfo memoInfo = new MemoInfo(memoName, memoPath, memoAuthor, external, lastModified);
            memoList.add(memoInfo);
        }
        return new MemoListReceive(memoList);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ManagerScreen.setMemoList(memoList)));
        context.setPacketHandled(true);
    }
}
