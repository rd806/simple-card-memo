package com.github.rd806.simplecardmemo.items;

import com.github.rd806.simplecardmemo.Config;
import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.screen.MemoViewerScreen;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.ClientMemoCache;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;


public class MemoViewerItem extends Item {

    // NBT键名
    private static final String FILE_PATH = "filePath";
    private static final String DISPLAY_NAME = "displayName";
    private static final String AUTHOR = "author";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String IS_LOCAL_FILE = "isLocalFile";
    // 默认内容
    private static String content = "Default Text";
    private final String prefix = "§a▍ §r";

    public MemoViewerItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        // 使用方法
        tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.general.tooltip")
                .withStyle(ChatFormatting.GRAY));
        // 为空展示默认信息
        if (stack.equals(ModCreativeModeTabs.newMemo(), false)) {
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.default")
                    .withStyle(ChatFormatting.GRAY));
            return;
        }
        // 更多提示信息
        if (Screen.hasShiftDown()) {
            // 作者名称
            tooltipComponents.add(Component.literal(getAuthorString(stack)));
            // 数据来源
            tooltipComponents.add(Component.literal(getSourceString(stack)));
            // 修改日期
            long timestamp = getLastModified(stack);
            if (timestamp > 0) {
                tooltipComponents.add(Component.literal(getDateString(timestamp)));
            }
        } else {
            // 未按 Shift 时显示提示
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.more")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack newStack = stack.copy();
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            // 若为空物品，转换为最后一次打开的备忘录
            if (stack.getTag() == null) {
                newStack = ClientMemoCache.getLastMemo();
            }
            // 构造 MemoInfo
            MemoInfo memoInfo = getMemoInfo(newStack);
            // 异步加载
            CompletableFuture.runAsync(() -> {
                        // 使用 LRU 缓存机制
                        content = ClientMemoCache.getMemoContentWithCache(memoInfo);
                    })
                    .thenAccept(data -> Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().setScreen(new MemoViewerScreen(content, memoInfo)))
                    )
                    .exceptionally(
                            e -> {
                                SimpleCardMemo.LOGGER.error("Error loading content data", e);
                                return null;
            });
            ClientMemoCache.setLastMemo(newStack);
        }
        // 返回成功，表示物品被使用了，但避免消耗
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
    // 获取完整的MemoInfo
    public static MemoInfo getMemoInfo(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) {
            SimpleCardMemo.LOGGER.warn("The Memo Info may be null!");
            return new MemoInfo();
        }
        String displayName = getDisplayName(stack);
        String filePath = getFilePath(stack);
        String author = getAuthor(stack);
        boolean isLocalFile = getTextSource(stack);
        long modified = getLastModified(stack);
        return new MemoInfo(displayName, filePath, author, isLocalFile, modified);
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

    // 获取文件作者信息
    private String getAuthorString(ItemStack stack) {
        String author = I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.author") + getAuthor(stack);
        return prefix + author;
    }
    // 获取文件来源信息
    private String getSourceString(ItemStack stack) {
        // 数据来源
        String source = getTextSource(stack) ?
                I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.local") :
                I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.web");
        return prefix + I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.source") + source;
    }
    // 获取格式化的时间字符串
    @OnlyIn(Dist.CLIENT)
    private String getDateString(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        String lastModified = "";
        switch (Config.DATE_FORMAT.get()) {
            case ISO_LOCAL_DATE -> lastModified = DateTimeFormatter.ISO_LOCAL_DATE.
                    format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
            case ISO_LOCAL_DATE_TIME -> lastModified = DateTimeFormatter.ISO_LOCAL_DATE_TIME.
                    format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).withNano(0));
            case RFC_1123_DATE_TIME -> lastModified = DateTimeFormatter.RFC_1123_DATE_TIME.withLocale(Locale.US).
                    format(ZonedDateTime.ofInstant(instant, ZoneId.systemDefault()));
        }
        return prefix + I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.time") + lastModified;
    }
}
