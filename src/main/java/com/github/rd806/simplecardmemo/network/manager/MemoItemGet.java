package com.github.rd806.simplecardmemo.network.manager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.init.value.MemoSource;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.setup.ServerSetup;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

public class MemoItemGet implements CustomPacketPayload {

    public static final Type<MemoItemGet> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(SimpleCardMemo.MODID, "memo_packet_get"));

    private final MemoInfo memoInfo;
    private final MemoSource memoSource;

    public MemoItemGet(MemoInfo memoInfo, MemoSource memoSource) {
        this.memoInfo = memoInfo;
        this.memoSource = memoSource;
    }

    public static final StreamCodec<FriendlyByteBuf, MemoItemGet> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public void encode(FriendlyByteBuf buf, MemoItemGet packet) {
            buf.writeUtf(packet.memoInfo.getMemoName());
            buf.writeUtf(packet.memoInfo.getMemoPath());
            buf.writeUtf(packet.memoInfo.getMemoAuthor());
            buf.writeBoolean(packet.memoInfo.isExternal());
            buf.writeLong(packet.memoInfo.getLastModified());
            buf.writeEnum(packet.memoSource);
        }

        @Override
        public @NotNull MemoItemGet decode(FriendlyByteBuf buf) {
            String memoName = buf.readUtf();
            String memoPath = buf.readUtf();
            String author = buf.readUtf();
            boolean external = buf.readBoolean();
            long lastModified = buf.readLong();
            MemoInfo memoInfo = new MemoInfo(memoName, memoPath, author, external, lastModified);
            MemoSource memoSource = buf.readEnum(MemoSource.class);
            return new MemoItemGet(memoInfo, memoSource);
        }
    };

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() { return TYPE; }

    // 处理数据包
    public static void handle(final MemoItemGet packet, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                // 检查是否为管理器界面
                if (!(player.containerMenu instanceof ManagerMenu managerMenu)) {
                    SimpleCardMemo.LOGGER.error("Not a Manager Menu!");
                    return;
                }
                // 检查输入槽
                ItemStack input = managerMenu.getItemStackHandler().getStackInSlot(ManagerMenu.INPUT_SLOT);
                if (input.isEmpty()) {
                    return;
                }
                // 检查输出槽
                ItemStack output = managerMenu.getItemStackHandler().getStackInSlot(ManagerMenu.OUTPUT_SLOT);
                if (!output.isEmpty()) {
                    return;
                }
                // 如果是服务端文件还需要传递内容
                if (packet.memoSource.equals(MemoSource.SERVER)) {
                    String filePath = packet.memoInfo.getMemoPath();
                    String content = ServerSetup.serverCache.get(filePath);
                    if (content == null) {
                        content = MemoLoader.loadFromExternal(packet.memoInfo);
                    }
                    Channel.sendMemoItem(player, filePath, content);
                }
                // 消耗和产出物品
                input.shrink(1);
                managerMenu.getItemStackHandler().setStackInSlot(ManagerMenu.OUTPUT_SLOT, GetExistMemo.setMemo(packet.memoInfo));
            }
        });
    }
}
