package com.github.rd806.simplecardmemo.init.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.value.MemoSource;
import com.github.rd806.simplecardmemo.init.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.BuiltInList;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.memo.GetExistMemo;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {

    // 背景GUI图片
    private static final ResourceLocation MANAGER_GUI =
            ResourceLocation.tryBuild(SimpleCardMemo.MODID, "textures/container/manager.png");

    // 文件信息
    private static List<MemoInfo> memoList = ClientSetup.clientConfig.getMemoList();
    private static MemoSource memoSource = MemoSource.CLIENT;
    private MemoInfo selectedMemo = new MemoInfo();
    private int selectIndex;
    // 滚动常量
    private int memoListScroll = 0;
    private int memoListMaxScroll = 0;
    // 布局常量
    private int leftPos;
    private int topPos;
    private static int PADDING;
    private static int HEADER;
    // 文件列表
    private static final int FILE_LIST_WIDTH = 123;
    private static final int FILE_LIST_HEIGHT = 64;
    private static final int ENTRY_HEIGHT = 16;
    private static int TOTAL_HEIGHT;
    // 输入框
    private Button editButton;
    private Button deleteButton;
    private EditBox nameInput;
    // 按键常量
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 20;

    public ManagerScreen(ManagerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 175;
        this.imageHeight = 235;
    }

    @Override
    protected void init() {
        super.init();
        // 计算 GUI 左上角的位置，使界面居中显示
        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;
        // 物品栏标题的 X 位置
        this.inventoryLabelX = 8;
        // 物品栏标题的 Y 位置
        this.inventoryLabelY = 140;
        // 布局
        PADDING = leftPos + 26;
        HEADER = topPos + 47;
        TOTAL_HEIGHT = memoList.size() * ENTRY_HEIGHT;

        renderEditBox();
        renderButton();
    }

    // 绘制输入框
    private void renderEditBox() {
        // 创建文件名输入框
        nameInput = new EditBox(
                this.font,
                leftPos + 43, topPos + 28,
                90, 18,
                Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.input")
        );
        nameInput.setMaxLength(256);
        nameInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.input.hint")
                .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        nameInput.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.input.tooltip")));
        nameInput.setTextColor(0xF3EFE0);
        nameInput.setBordered(false);
        addRenderableWidget(nameInput);
    }

    // 绘制按钮
    private void renderButton() {
        // 切换来源
        CycleButton<MemoSource> sourceChange = CycleButton.<MemoSource>builder(
                (value) -> {
                    String name = "";
                    switch (value) {
                        case BUILTIN -> name = I18n.get(SimpleCardMemo.MODID + ".gui.manager_screen.source.built_in");
                        case CLIENT -> name = I18n.get(SimpleCardMemo.MODID + ".gui.manager_screen.source.client");
                        case SERVER -> name = I18n.get(SimpleCardMemo.MODID + ".gui.manager_screen.source.server");
                    }
                    return Component.literal(name);
                })
                .withValues(MemoSource.values())
                .create(
                        leftPos - BUTTON_WIDTH - 5,  topPos + 28,
                        BUTTON_WIDTH, BUTTON_HEIGHT,
                        Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.source"),
                        (button, value) -> {
                            memoSource = value;
                            refreshMemoList();
                        }
                );
        sourceChange.setValue(memoSource);
        sourceChange.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.source.tooltip")));
        addRenderableWidget(sourceChange);

        // 编辑按钮
        editButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.edit"),
                        button -> editMemoName())
                .pos(leftPos - BUTTON_WIDTH - 5, sourceChange.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        editButton.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.edit.tooltip")));
        if (!memoSource.equals(MemoSource.CLIENT)) { editButton.active = false; }
        addRenderableWidget(editButton);

        // 导出按钮
        Button exportButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export"),
                        button -> exportItem())
                .pos(editButton.getX(), editButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        exportButton.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export.tooltip")));
        addRenderableWidget(exportButton);

        // 刷新按钮
        Button reloadButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.reload"),
                        button -> refreshMemoList())
                .pos(editButton.getX(), exportButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        reloadButton.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.reload.tooltip")));
        addRenderableWidget(reloadButton);

        // 删除按钮
        deleteButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.delete"),
                        button -> {
                            if (!memoSource.equals(MemoSource.CLIENT)) { return; }
                            if (MemoLoader.deleteLocalFiles(selectedMemo)) { refreshMemoList(); }
                        })
                .pos(editButton.getX(), reloadButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        deleteButton.setTooltip(Tooltip.create(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.delete.tooltip")));
        if (!memoSource.equals(MemoSource.CLIENT)) { deleteButton.active = false; }
        addRenderableWidget(deleteButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 设置渲染使用的 Shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // 设置颜色（RGBA），1 表示不改变原贴图颜色
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        // 绑定要绘制的纹理
        RenderSystem.setShaderTexture(0, MANAGER_GUI);
        // 绘制贴图
        guiGraphics.blit(MANAGER_GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        // 渲染文件列表
        renderFileList(graphics);
        // 绘制物品提示
        this.renderTooltip(graphics, mouseX, mouseY);
        renderFileTooltip(graphics, mouseX, mouseY);
        renderScrollBar(graphics);
    }

    // 绘制文件列表
    private void renderFileList(GuiGraphics graphics) {
        if (memoList.isEmpty()) {
            graphics.drawString(
                    this.font, Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.memo_list"),
                    PADDING, HEADER,
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

        // 使用裁剪
        graphics.enableScissor(
                PADDING, HEADER,
                PADDING + FILE_LIST_WIDTH, HEADER + FILE_LIST_HEIGHT
        );
        graphics.pose().pushPose();

        // 绘制时应用滚动偏移
        int scrollOffset = memoListScroll * ENTRY_HEIGHT;
        // 绘制文件条目
        for (int i = 0; i < totalEntries; i++) {
            MemoInfo info = memoList.get(i);
            // 这里减去偏移量，实现向上滚动
            int y = HEADER + i * ENTRY_HEIGHT - scrollOffset;
            // 不在可视区域的条目跳过不绘制
            if (y + ENTRY_HEIGHT < HEADER || y > HEADER + FILE_LIST_HEIGHT) {
                continue;
            }
            // 高亮选中的文件
            if (selectedMemo != null && info.getMemoPath().equals(selectedMemo.getMemoPath())) {
                graphics.fill(PADDING, y, PADDING + FILE_LIST_WIDTH, y + ENTRY_HEIGHT, 0x4466CC66);
            }
            // 文件图标和名称
            graphics.drawString(
                    this.font, Component.literal("📄 " + info.getMemoName()),
                    PADDING + 4, y + ENTRY_HEIGHT / 4,
                    0x3F3F3F, false
            );
        }

        graphics.pose().popPose();
        graphics.disableScissor();
    }

    // 悬浮提示信息
    private void renderFileTooltip(GuiGraphics graphics, double mouseX, double mouseY) {
        // 检测鼠标是否在某个矩形区域内
        if (isMouseOver(mouseX, mouseY)) {
            int index = (int) ((mouseY - HEADER) / ENTRY_HEIGHT) + memoListScroll;
            if (index >= 0 && index < memoList.size()) {
                // 获取对应的物品
                ItemStack stack = GetExistMemo.setMemo(memoList.get(index));
                // 显示单行文本
                graphics.renderTooltip(this.font, stack, (int) mouseX, (int) mouseY);
            }
        }
    }

    // 渲染滚动条
    private void renderScrollBar(GuiGraphics graphics) {
        if (memoListMaxScroll <= 0) return;
        // 滑块轨道
        int barX = PADDING + FILE_LIST_WIDTH - 3;
        int barY = HEADER;
        int barW = 3;
        int barH = FILE_LIST_HEIGHT;
        graphics.fill(barX, barY, barX + barW, barY + barH, 0x33FFFFFF);
        // 滑块
        float progress = (float) (memoListScroll / memoListMaxScroll);
        int thumbHeight = Math.max(20, barH * Math.min(1, (barH / TOTAL_HEIGHT)));
        int thumbY = barY + (int) ((barH - thumbHeight) * progress);
        graphics.fill(barX, thumbY, barX + barW, thumbY + thumbHeight, 0xCCFFFFFF);
    }

    // 检查鼠标位置
    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= PADDING && mouseX <= PADDING + FILE_LIST_WIDTH
                && mouseY >= HEADER && mouseY <= HEADER + FILE_LIST_HEIGHT;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 点击文件列表选择文件
        if (memoList != null && !memoList.isEmpty()) {
            if (isMouseOver(mouseX, mouseY)) {
                int index = (int) ((mouseY - HEADER) / ENTRY_HEIGHT) + memoListScroll;
                if (index >= 0 && index < memoList.size()) {
                    selectedMemo = memoList.get(index);
                    selectIndex = index;
                    nameInput.setValue(selectedMemo.getMemoName());
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        memoListScroll -= (int) (amount * 2);
        memoListScroll = Math.max(0, Math.min(memoListScroll, memoListMaxScroll));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    // 获取文件列表
    public static void setMemoList(List<MemoInfo> list) { memoList = list; }

    // 编辑文件名称
    private void editMemoName() {
        String name = nameInput.getValue();
        if (!memoSource.equals(MemoSource.CLIENT) || name.isEmpty()) { return; }
        ClientSetup.clientConfig.getMemoList().get(selectIndex).setMemoName(name);
        ClientSetup.clientConfig.saveToConfig();
        refreshMemoList();
    }

    // 刷新文件列表
    private void refreshMemoList() {
        switch (memoSource) {
            case BUILTIN -> {
                memoList = BuiltInList.BUILT_IN_MEMOS;
                editButton.active = false;
                deleteButton.active = false;
            }
            case CLIENT -> {
                ClientSetup.clientConfig.reload();
                memoList = ClientSetup.clientConfig.getMemoList();
                editButton.active = true;
                deleteButton.active = true;
            }
            case SERVER -> {
                Channel.getMemoList();
                editButton.active = false;
                deleteButton.active = false;
            }
        }
        memoListScroll = 0;
    }

    // 获取对应的物品
    private void exportItem() {
        if (selectedMemo == null) {
            SimpleCardMemo.LOGGER.warn("No memo is selected!");
            return;
        }
        // 发送网络包
        Channel.getMemoItem(selectedMemo, memoSource);
    }
}
