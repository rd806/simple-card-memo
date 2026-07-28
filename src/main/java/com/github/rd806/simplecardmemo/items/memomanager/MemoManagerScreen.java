package com.github.rd806.simplecardmemo.items.memomanager;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.MemoLoader;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.items.memoviewer.MemoViewerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MemoManagerScreen extends Screen {

    private List<MemoInfo> memoList;
    private MemoInfo selectedMemo;

    // 滚动常量
    private int memoListScroll = 0;
    private int memoListMaxScroll = 0;
    // 布局常量
    private static int PADDING;
    private static int HEADER;
    private static int FOOTER;
    private static int FILE_LIST_WIDTH;
    private static int FILE_LIST_HEIGHT;
    private static int ENTRY_HEIGHT;
    // 按键常量
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 20;

    public MemoManagerScreen() {
        super(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.title"));
        memoList = MemoLoader.listAllMemos();
        selectedMemo = new MemoInfo();
    }

    @Override
    protected void init() {
        super.init();

        PADDING = (int) (this.width * 0.1);
        HEADER = (int) (this.height * 0.2);
        FOOTER = (int) (this.height * 0.2);
        FILE_LIST_WIDTH = this.width - PADDING * 2;
        FILE_LIST_HEIGHT = this.height - HEADER - FOOTER;
        ENTRY_HEIGHT = this.font.lineHeight * 2;

        // 关闭按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.close"),
                                button -> this.onClose())
                        .pos(this.width - PADDING - BUTTON_WIDTH, 5)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 删除按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.delete"),
                                button -> {
                                    if (!MemoLoader.deleteLocalFiles(selectedMemo.getMemoPath())) { return; }
                                    refreshMemoList();
                                })
                        .pos(PADDING, this.height - FOOTER + 10)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 刷新按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.reload"),
                                button -> refreshMemoList())
                        .pos(this.width - PADDING - BUTTON_WIDTH * 2 - 5, this.height - FOOTER + 10)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 导出按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export"),
                                button -> {
                                    getItem();
                                    this.onClose();
                                })
                        .pos(this.width - PADDING - BUTTON_WIDTH, this.height - FOOTER + 10)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        // 渲染标题
        graphics.drawString(
                this.font,
                Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.title"),
                PADDING,
                8,
                0xFFFFFF
        );
        // 渲染文件列表
        renderFileList(graphics);

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 文件列表
    private void renderFileList(GuiGraphics graphics) {
        if (memoList == null) {
            graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.memo_list"),
                    PADDING,
                    HEADER,
                    0x888888
            );
            return;
        }

        // 计算最大滚动
        int totalEntries = memoList.size();
        int visibleEntries = FILE_LIST_HEIGHT / ENTRY_HEIGHT;
        memoListMaxScroll = Math.max(0, totalEntries - visibleEntries);
        if (memoListScroll > memoListMaxScroll) {
            memoListScroll = memoListMaxScroll;
        }

        // 绘制列表背景
        graphics.fill(
                PADDING - 4,
                HEADER - 4,
                PADDING + FILE_LIST_WIDTH + 4,
                HEADER + FILE_LIST_HEIGHT + 4,
                0xCC000000
        );
        graphics.fill(PADDING, HEADER, PADDING + FILE_LIST_WIDTH, HEADER + FILE_LIST_HEIGHT, 0xCC222222);

        // 绘制文件条目
        for (int i = memoListScroll; i < Math.min(totalEntries, memoListScroll + visibleEntries + 1); i++) {
            MemoInfo info = memoList.get(i);
            int y = HEADER + (i - memoListScroll) * ENTRY_HEIGHT;
            // 高亮选中的文件
            if (selectedMemo != null && info.getMemoName().equals(selectedMemo.getMemoName())) {
                graphics.fill(PADDING, y, PADDING + FILE_LIST_WIDTH, y + ENTRY_HEIGHT, 0x4466CC66);
            }
            // 文件图标和名称
            graphics.drawString(
                    this.font,
                    Component.literal("📄 " + info.getMemoName()),
                    PADDING + 4,
                    y + ENTRY_HEIGHT / 4,
                    0xFFFFFF
            );
        }
    }

    // 刷新文件列表
    private void refreshMemoList() {
        memoList = MemoLoader.listAllMemos();
        memoListScroll = 0;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击文件列表选择文件
        if (memoList != null && !memoList.isEmpty()) {
            // 点击选择
            if (mouseX >= PADDING && mouseX <= PADDING + FILE_LIST_WIDTH && mouseY >= HEADER &&
                    mouseY <= HEADER + FILE_LIST_HEIGHT) {
                int index = (int) ((mouseY - HEADER) / ENTRY_HEIGHT) + memoListScroll;
                if (index >= 0 && index < memoList.size()) {
                    selectedMemo = memoList.get(index);
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 获取对应的物品
    private void getItem() {
        if (selectedMemo == null) {
            return;
        }
        if (Minecraft.getInstance().player != null) {
            ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
            viewer.setHoverName(Component.literal(selectedMemo.getMemoName()));
            MemoViewerItem.setDisplayName(viewer, selectedMemo.getMemoName());
            MemoViewerItem.setFilePath(viewer, selectedMemo.getMemoName());
            MemoViewerItem.setAuthor(viewer, Minecraft.getInstance().player.getName().getString());
            MemoViewerItem.setLastModified(viewer, selectedMemo.getLastModified());
            // 发送物品
            Minecraft.getInstance().player.getInventory().add(viewer);
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export.success"),
                    false);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        memoListScroll -= (int) (amount * 2);
        memoListScroll = Math.max(0, Math.min(memoListScroll, memoListMaxScroll));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
