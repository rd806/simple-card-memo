package com.github.rd806.simplecardmemo.items.memoviewer;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.memo.MemoLoader;
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

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MemoViewerItem extends Item {

    // NBT键名
    private static final String FILE_PATH = "filePath";
    private static final String DISPLAY_NAME = "displayName";
    private static final String AUTHOR = "author";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String IS_LOCAL_FILE = "isLocalFile";

    private static String content;

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
        // 使用方法
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.simple")
                .withStyle(ChatFormatting.GRAY));
        // 作者
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.author")
                .append(Component.literal(getAuthor(stack))).withStyle(ChatFormatting.BLUE));
        // 更多提示信息
        if (Screen.hasShiftDown()) {
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.detail"));
            // 数据来源
            Component source = getTextSource(stack) ?
                    Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.local") :
                    Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.web");
            tooltipComponents.add(source);
            // 修改日期
            if (getLastModified(stack) > 0) {
                String lastModified = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(getLastModified(stack)));
                Component time = Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.time")
                        .append(Component.literal(lastModified)).withStyle(ChatFormatting.BLUE);
                tooltipComponents.add(time);
            }
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
            // 异步加载
            CompletableFuture.runAsync(() -> content = MemoLoader.loadText(filePath, isLocalFile))
                    .thenAccept(data -> Minecraft.getInstance().execute(
                            () ->
                            Minecraft.getInstance().setScreen(
                                    new MemoViewerScreen(content, filePath, displayName, isLocalFile))
                            )
                    )
                    .exceptionally(
                            e -> {
                                SimpleCardMemo.LOGGER.error("Error loading network data", e);
                                return null;
            });
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
    // 获取修改日期
    public static long getLastModified(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(LAST_MODIFIED)) {
            return tag.getLong(LAST_MODIFIED);
        }
        return 0;
    }

    // 设置文件路径
    public static void setFilePath(ItemStack stack, String filePath) {
        stack.getOrCreateTag().putString(FILE_PATH, filePath);
    }
    // 设置文件来源
    public static void setTextSource(ItemStack stack, boolean isLocalFile) {
        stack.getOrCreateTag().putBoolean(IS_LOCAL_FILE, isLocalFile);
    }
    // 设置文件名称
    public static void setDisplayName(ItemStack stack, String displayName) {
        stack.getOrCreateTag().putString(DISPLAY_NAME, displayName);
    }
    // 设置文件作者
    public static void setAuthor(ItemStack stack, String author) {
        stack.getOrCreateTag().putString(AUTHOR, author);
    }
    // 设置修改日期
    public static void setLastModified(ItemStack stack, long lastModified) {
        stack.getOrCreateTag().putLong(LAST_MODIFIED, lastModified);
    }
}
