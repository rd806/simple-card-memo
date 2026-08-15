package com.github.rd806.simplecardmemo.init.item;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.init.container.screen.MemoViewerScreen;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.CacheSystem;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
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

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class MemoViewerItem extends Item {

    // NBT键名
    private static final String FILE_PATH = "filePath";
    private static final String DISPLAY_NAME = "displayName";
    private static final String AUTHOR = "author";
    private static final String LAST_MODIFIED = "lastModified";
    private static final String IS_EXTERNAL = "isExternal";
    // 默认内容
    private static MemoViewerScreen memoViewerScreen = null;
    private final String prefix = "§a▍ §7";

    public MemoViewerItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        // 为空展示默认信息
        if (stack.equals(ModCreativeModeTabs.newMemo(), false)) {
            // 使用方法
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.general.tooltip")
                    .withStyle(ChatFormatting.GRAY));
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
                String time = prefix + I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.time")
                                + "§r" + CommonConfig.getDateString(timestamp);

                tooltipComponents.add(Component.literal(time));
            }
        } else {
            // 使用方法
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.general.tooltip")
                    .withStyle(ChatFormatting.GRAY));
            // 显示提示
            tooltipComponents.add(Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.more")
                    .withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            // 若为空物品，转换为最后一次打开的备忘录
            if (getFilePath(stack).isEmpty()) {
                if (memoViewerScreen == null) {
                    player.displayClientMessage(
                            Component.translatable(SimpleCardMemo.MODID + ".item.memo_viewer.no_screen"),
                            false);
                    return InteractionResultHolder.success(stack);
                }
                Minecraft.getInstance().execute(() -> Minecraft.getInstance().setScreen(memoViewerScreen));
                return InteractionResultHolder.success(stack);
            }
            // 异步加载
            CompletableFuture.runAsync(() -> {
                memoViewerScreen = ClientSetup.clientScreenCache.get(stack);
                if (memoViewerScreen == null) {
                    // 构造 MemoInfo
                    MemoInfo memoInfo = getMemoInfo(stack);
                    String content = CacheSystem.getMemoContentWithCache(memoInfo, ClientSetup.clientContentCache);
                    memoViewerScreen = new MemoViewerScreen(content, memoInfo);
                    // 放入缓存
                    ClientSetup.clientScreenCache.put(stack, memoViewerScreen);
                }
            })
            .thenAccept(data -> Minecraft.getInstance().execute(() ->
                    Minecraft.getInstance().setScreen(memoViewerScreen))
            )
            .exceptionally(
                    e -> {
                        SimpleCardMemo.LOGGER.error("Error loading content data", e);
                        return null;
                    });
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
        if (tag != null && tag.contains(IS_EXTERNAL)) {
            return tag.getBoolean(IS_EXTERNAL);
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
    public static void setTextSource(ItemStack stack, boolean isExternal) {
        stack.getOrCreateTag().putBoolean(IS_EXTERNAL, isExternal);
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
    @OnlyIn(Dist.CLIENT)
    private String getAuthorString(ItemStack stack) {
        String author = I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.author") + "§r" + getAuthor(stack);
        return prefix + author;
    }
    // 获取文件来源信息
    @OnlyIn(Dist.CLIENT)
    private String getSourceString(ItemStack stack) {
        // 数据来源
        String source = getTextSource(stack) ?
                I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.external") :
                I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.resource");
        return prefix + I18n.get(SimpleCardMemo.MODID + ".item.memo_viewer.tooltip.source") + "§r" + source;
    }
}
