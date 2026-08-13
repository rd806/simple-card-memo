package com.github.rd806.simplecardmemo.init.item;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.config.CommonConfig;
import com.github.rd806.simplecardmemo.init.MemoDataComponents;
import com.github.rd806.simplecardmemo.init.ModCreativeModeTabs;
import com.github.rd806.simplecardmemo.init.container.screen.MemoViewerScreen;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MemoViewerItem extends Item {
    // 默认内容
    private static String content = "Default Text";
    private final String prefix = "§a▍ §7";

    public MemoViewerItem(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltipComponents, @NotNull TooltipFlag flag) {
        // 为空展示默认信息
        if (stack.equals(ModCreativeModeTabs.newMemo())) {
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
        ItemStack newStack = stack.copy();
        // 只在客户端执行打开界面的逻辑
        if (level.isClientSide) {
            // 若为空物品，转换为最后一次打开的备忘录
            if (getFilePath(stack).isEmpty()) {
                newStack = ClientSetup.clientCache.getLastMemo();
            }
            // 构造 MemoInfo
            MemoInfo memoInfo = getMemoInfo(newStack);
            // 异步加载
            CompletableFuture.runAsync(() -> {
                        // 使用 LRU 缓存机制
                        content = CacheSystem.getMemoContentWithCache(memoInfo, ClientSetup.clientCache);
                    })
                    .thenAccept(data -> Minecraft.getInstance().execute(() ->
                            Minecraft.getInstance().setScreen(new MemoViewerScreen(content, memoInfo)))
                    )
                    .exceptionally(
                            e -> {
                                SimpleCardMemo.LOGGER.error("Error loading content data", e);
                                return null;
                            });
            ClientSetup.clientCache.setLastMemo(newStack);
        }
        // 返回成功，表示物品被使用了，但避免消耗
        return InteractionResultHolder.success(stack);
    }

    // 获取文件路径
    public static String getFilePath(ItemStack stack) {
        String filePath = stack.get(MemoDataComponents.FILE_PATH);
        if (filePath == null) {
            return "";
        }
        return filePath;
    }
    // 获取文件来源
    public static boolean getTextSource(ItemStack stack) {
        return Boolean.TRUE.equals(stack.get(MemoDataComponents.IS_EXTERNAL));
    }
    // 获取文件名称
    public static String getDisplayName(ItemStack stack) {
        String displayName = stack.get(MemoDataComponents.DISPLAY_NAME);
        if (displayName == null) {
            return "";
        }
        return displayName;
    }
    // 获取文件作者
    public static String getAuthor(ItemStack stack) {
        String author = stack.get(MemoDataComponents.AUTHOR);
        if (author == null) {
            return "";
        }
        return author;
    }
    // 获取修改日期
    public static long getLastModified(ItemStack stack) {
        Long lastModified = stack.get(MemoDataComponents.LAST_MODIFIED);
        if (lastModified == null) {
            return 0;
        }
        return lastModified;
    }
    // 获取完整的MemoInfo
    public static MemoInfo getMemoInfo(ItemStack stack) {
        String displayName = getDisplayName(stack);
        String filePath = getFilePath(stack);
        String author = getAuthor(stack);
        boolean isLocalFile = getTextSource(stack);
        long modified = getLastModified(stack);
        return new MemoInfo(displayName, filePath, author, isLocalFile, modified);
    }

    // 设置文件路径
    public static void setFilePath(ItemStack stack, String filePath) {
        stack.set(MemoDataComponents.FILE_PATH, filePath);
    }
    // 设置文件来源
    public static void setTextSource(ItemStack stack, boolean isExternal) {
        stack.set(MemoDataComponents.IS_EXTERNAL, isExternal);
    }
    // 设置文件名称
    public static void setDisplayName(ItemStack stack, String displayName) {
        stack.set(MemoDataComponents.DISPLAY_NAME, displayName);
    }
    // 设置文件作者
    public static void setAuthor(ItemStack stack, String author) {
        stack.set(MemoDataComponents.AUTHOR, author);
    }
    // 设置修改日期
    public static void setLastModified(ItemStack stack, long lastModified) {
        stack.set(MemoDataComponents.LAST_MODIFIED, lastModified);
    }
    // 设置名称
    // 名称有固定组件 CUSTOM_NAME
    public static void setItemName(ItemStack stack, String itemName) {
        stack.set(DataComponents.CUSTOM_NAME, Component.literal(itemName));
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
