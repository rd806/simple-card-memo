package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.ManagerMenu;
import com.github.rd806.simplecardmemo.memo.MemoConfig;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.MemoLoader;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.get.MemoPacketGet;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ManagerScreen extends AbstractContainerScreen<ManagerMenu> {

    // 背景GUI图片
    private static final ResourceLocation MANAGER_GUI =
            ResourceLocation.parse(SimpleCardMemo.MODID + ":textures/container/manager.png");

    private List<MemoInfo> memoList;
    private MemoInfo selectedMemo;
    private int selectIndex;
    // 滚动常量
    private int memoListScroll = 0;
    private int memoListMaxScroll = 0;
    // 布局常量
    // GUI 左上角的位置，使界面居中显示
    private int leftPos;
    private int topPos;
    private static int PADDING;
    private static int HEADER;
    private static int FOOTER;
    private static int FILE_LIST_WIDTH;
    private static int FILE_LIST_HEIGHT;
    private static int ENTRY_HEIGHT;
    // 输入框
    private EditBox nameInput;
    // 按键常量
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 20;

    public ManagerScreen(ManagerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        memoList = MemoConfig.MEMO_LIST;
        selectedMemo = memoList.get(0);
        this.imageWidth = 175;
        this.imageHeight = 238;
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
        this.inventoryLabelY = 145;
        // 布局
        PADDING = leftPos + 16;
        HEADER = topPos + 24;
        FOOTER = topPos + 110;
        FILE_LIST_WIDTH = this.width - PADDING * 2;
        FILE_LIST_HEIGHT = this.height - HEADER - FOOTER;
        ENTRY_HEIGHT = this.font.lineHeight * 2;

        // 创建文件名输入框
        nameInput = new EditBox(
                this.font,
                leftPos - BUTTON_WIDTH * 2 - 5,
                topPos + 10,
                BUTTON_WIDTH * 2,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.input")
        );
        nameInput.setBordered(true);
        nameInput.setValue(selectedMemo.getMemoName());
        nameInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.hint.name"));

        // 编辑按钮
        Button editButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.edit"),
                        button -> {
                            MemoConfig.MEMO_LIST.get(selectIndex).setMemoName(nameInput.getValue());
                            MemoConfig.saveToConfig();
                            refreshMemoList();
                        })
                .pos(leftPos - BUTTON_WIDTH - 5, nameInput.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        // 删除按钮
        Button deleteButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.delete"),
                        button -> {
                            if (MemoLoader.deleteLocalFiles(selectedMemo.getMemoPath())) {
                                MemoConfig.MEMO_LIST.remove(selectedMemo);
                                MemoConfig.saveToConfig();
                                refreshMemoList();
                            }
                            refreshMemoList();
                            MemoConfig.saveToConfig();
                        })
                .pos(leftPos - BUTTON_WIDTH - 5, editButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        // 刷新按钮
        Button reloadButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.reload"),
                        button -> refreshMemoList())
                .pos(leftPos - BUTTON_WIDTH - 5, deleteButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        // 导出按钮
        Button exportButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.manager_screen.export"),
                        button -> getItem())
                .pos(leftPos - BUTTON_WIDTH - 5, reloadButton.getY() + BUTTON_HEIGHT + 5)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        // 绘制按钮
        addRenderableWidget(nameInput);
        addRenderableWidget(exportButton);
        addRenderableWidget(deleteButton);
        addRenderableWidget(editButton);
        addRenderableWidget(reloadButton);
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
        renderTooltip(graphics, mouseX, mouseY);
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
        // 绘制文件条目
        for (int i = memoListScroll; i < Math.min(totalEntries, memoListScroll + visibleEntries + 1); i++) {
            MemoInfo info = memoList.get(i);
            int y = HEADER + (i - memoListScroll) * ENTRY_HEIGHT;
            // 高亮选中的文件
            if (selectedMemo != null && info.getMemoPath().equals(selectedMemo.getMemoPath())) {
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
        MemoConfig.reload();
        memoList = MemoConfig.MEMO_LIST;
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
                    selectIndex = index;
                    nameInput.setValue(selectedMemo.getMemoName());
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
        // 发送网络包
        Channel.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new MemoPacketGet(selectedMemo)
        );
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        memoListScroll -= (int) (amount * 2);
        memoListScroll = Math.max(0, Math.min(memoListScroll, memoListMaxScroll));
        return super.mouseScrolled(mouseX, mouseY, amount);
    }
}
