package com.github.rd806.simplecardmemo.network.editor;

import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NewMemo {

    private final ItemStack stack;

    public NewMemo(ItemStack stack) { this.stack = stack; }

    // 编码：将数据写入网络缓冲区
    public void encode(FriendlyByteBuf buffer) { buffer.writeItem(this.stack); }

    // 解码：从网络缓冲区读取数据
    public static NewMemo decode(FriendlyByteBuf buffer) {
        ItemStack stack = buffer.readItem();
        return new NewMemo(stack);
    }

    // 处理方法
    public void handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) { return; }
            // 消耗和产出物品
            if (GetExistMemo.consumeItem(player.getInventory(), new ItemStack(ModItems.MEMO_EDITOR.get()), 1)) {
                player.getInventory().add(stack);
            }
        });
        context.setPacketHandled(true);
    }
}
