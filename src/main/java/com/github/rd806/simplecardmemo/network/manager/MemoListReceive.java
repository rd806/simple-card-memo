package com.github.rd806.simplecardmemo.network.manager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.screen.ManagerScreen;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MemoListReceive implements CustomPacketPayload {

    public static final Type<MemoListReceive> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "memo_list_receive"));

    private final List<MemoInfo> memoList;

    public MemoListReceive(List<MemoInfo> memoList) { this.memoList = memoList; }

    public static final StreamCodec<FriendlyByteBuf, MemoListReceive> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MemoListReceive packet) {
            buf.writeInt(packet.memoList.size());
            for (MemoInfo memo : packet.memoList) {
                buf.writeUtf(memo.getMemoName());
                buf.writeUtf(memo.getMemoPath());
                buf.writeUtf(memo.getMemoAuthor());
                buf.writeBoolean(memo.isExternal());
                buf.writeLong(memo.getLastModified());
            }
        }

        @Override
        public @NotNull MemoListReceive decode(FriendlyByteBuf buf) {
            List<MemoInfo> memoList = new ArrayList<>();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                String memoName = buf.readUtf();
                String memoPath = buf.readUtf();
                String memoAuthor = buf.readUtf();
                boolean external = buf.readBoolean();
                long modified = buf.readLong();
                MemoInfo memoInfo = new MemoInfo(memoName, memoPath, memoAuthor, external, modified);
                memoList.add(memoInfo);
            }
            return new MemoListReceive(memoList);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MemoListReceive packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Dist.CLIENT.isClient()) {
                List<MemoInfo> memoList = packet.memoList;
                ManagerScreen.setMemoList(memoList);
            }
        });
    }
}
