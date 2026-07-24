package com.github.rd806.todolist.init.textviewer;

import com.github.rd806.todolist.Todolist;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;


public class TextViewerItem extends Item {

    // NBT键名
    private static final String FILE_PATH = "filePath";
    private static final String IS_LOCAL_FILE = "isLocalFile";

    public TextViewerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        // 第一次创建时设置NBT
        setFilePath(stack, "default.md");
        setTextSource(stack, true);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(Todolist.MODID + ".item.text_viewer.tooltip.simple"));
        // 获取 NBT 数据
        String path = getFilePath(stack);
        boolean isLocal = getTextSource(stack);

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.empty());
            tooltipComponents.add(Component.literal("§7§o" + path));
            // 数据来源
            Component source = isLocal ?
                    Component.translatable(Todolist.MODID + ".item.text_viewer.tooltip.local") :
                    Component.translatable(Todolist.MODID + ".item.text_viewer.tooltip.web");
            tooltipComponents.add(source);

        } else {
            // 未按 Shift 时显示提示
            tooltipComponents.add(Component.translatable(Todolist.MODID + ".item.text_viewer.tooltip.more"));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            // 为空则创建
            if (stack.getTag() == null) {
                setFilePath(stack, "default.md");
                setTextSource(stack, true);
            }
            String filePath = getFilePath(stack);
            boolean isLocalFile = getTextSource(stack);
            // 执行客户端代码
            Minecraft.getInstance().setScreen(new TextViewerScreen(filePath, isLocalFile));
        }
        // 返回成功，表示物品被使用了，但避免消耗（比如不减少耐久度）
        return InteractionResultHolder.success(stack);
    }

    // 获取文件路径
    public static String getFilePath(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(FILE_PATH)) {
            return tag.getString(FILE_PATH);
        }
        return "";
    }
    // 获取文件来源
    public static boolean getTextSource(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(IS_LOCAL_FILE)) {
            return tag.getBoolean(IS_LOCAL_FILE);
        }
        return true;
    }

    // 设置文件路径
    public static void setFilePath(ItemStack stack, String filePath) {
        stack.getOrCreateTag().putString(FILE_PATH, filePath);
    }
    // 设置文件来源
    public static void setTextSource(ItemStack stack, boolean isLocalFile) {
        stack.getOrCreateTag().putBoolean(IS_LOCAL_FILE, isLocalFile);
    }
}
