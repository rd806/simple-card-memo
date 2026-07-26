package com.github.rd806.simplecardmemo.items.memoviewer;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import net.minecraft.ChatFormatting;
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


public class MemoViewerItem extends Item {

    // NBT键名
    private static final String FILE_PATH = "filePath";
    private static final String DISPLAY_NAME = "fileName";
    private static final String AUTHOR = "default";
    private static final String IS_LOCAL_FILE = "isLocalFile";

    public MemoViewerItem(Properties properties) {
        super(properties);
    }

    @Override
    public void onCraftedBy(@NotNull ItemStack stack, @NotNull Level level, @NotNull Player player) {
        // 第一次创建时设置NBT
        setFilePath(stack, "guide.md");
        setDisplayName(stack, "Guide");
        setTextSource(stack, true);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.simple")
                .withStyle(ChatFormatting.GRAY));
        // 获取 NBT 数据
        boolean isLocal = getTextSource(stack);
        String author = getAuthor(stack);

        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.detail"));
            // 作者
            tooltipComponents.add(Component.literal(author).withStyle(ChatFormatting.BLUE));
            // 数据来源
            Component source = isLocal ?
                    Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.local") :
                    Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.web");
            tooltipComponents.add(source);

        } else {
            // 未按 Shift 时显示提示
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.more")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            String filePath = getFilePath(stack);
            String displayName = getDisplayName(stack);
            boolean isLocalFile = getTextSource(stack);
            // 执行客户端代码
            Minecraft.getInstance().setScreen(new MemoViewerScreen(filePath, displayName, isLocalFile));
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
    // 获取文件名称
    public static String getDisplayName(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(DISPLAY_NAME)) {
            return tag.getString(DISPLAY_NAME);
        }
        return "";
    }
    // 获取文件作者
    public static String getAuthor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(AUTHOR)) {
            return tag.getString(AUTHOR);
        }
        return "";
    }

    // 设置文件路径
    public static void setFilePath(ItemStack stack, String filePath) {
        stack.getOrCreateTag().putString(FILE_PATH, filePath);
    }
    // 设置文件来源
    public static void setTextSource(ItemStack stack, boolean isLocalFile) {
        stack.getOrCreateTag().putBoolean(IS_LOCAL_FILE, isLocalFile);
    }
    // 获取文件名称
    public static void setDisplayName(ItemStack stack, String displayName) {
        stack.getOrCreateTag().putString(DISPLAY_NAME, displayName);
    }
    // 获取文件作者
    public static void setAuthor(ItemStack stack, String author) {
        stack.getOrCreateTag().putString(AUTHOR, author);
    }
}
