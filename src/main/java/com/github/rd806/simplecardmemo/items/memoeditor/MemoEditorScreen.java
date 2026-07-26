package com.github.rd806.simplecardmemo.items.memoeditor;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.init.TextLoader;
import com.github.rd806.simplecardmemo.items.memoviewer.MemoViewerItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.time.LocalDate;

public class MemoEditorScreen extends Screen {

    private final String initialContent;
    private String filePath;
    private String fileName;

    // UI 组件
    private EditBox nameInput;
    private EditBox pathInput;
    private MultiLineEditBox textInput;
    private Button saveDraftButton;
    private Button exportButton;

    // 布局常量
    private static final int PADDING = 20;
    private static final int HEADER_HEIGHT = 50;
    private static final int BOTTOM_BAR_HEIGHT = 50;
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 20;

    protected MemoEditorScreen() {
        super(Component.translatable(SimpleCardMemo.MODID + ".gui.editor.title"));
        this.initialContent = TextLoader.loadFromLocalFiles("temp.md");
        // 默认名称
        this.filePath = "memo_" + LocalDate.now() + ".md";
        this.fileName = filePath;
    }

    @Override
    protected void init() {
        super.init();

        int centerX = this.width / 2;
        int bottomY = this.height - BOTTOM_BAR_HEIGHT;

        // 创建多行文本输入框
        int textInputX = PADDING * 2;
        int textInputY = HEADER_HEIGHT + PADDING;
        int textInputWidth = this.width - PADDING * 4;
        int textInputHeight = this.height - HEADER_HEIGHT - BOTTOM_BAR_HEIGHT - PADDING * 4;
        this.textInput = new MultiLineEditBox(
                this.font,
                textInputX,
                textInputY,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input"),
                Component.literal("")
        );
        this.textInput.setValue(this.initialContent);
        this.textInput.setCharacterLimit(100000);
        this.textInput.setFocused(true);
        this.addRenderableWidget(this.textInput);


        // 创建文件名输入框
        int nameInputX = PADDING * 2 + 50;
        int nameInputY = textInputY + textInputHeight + 5;
        int nameInputWidth = BUTTON_WIDTH * 3;
        int nameInputHeight = BUTTON_HEIGHT;
        this.nameInput = new EditBox(
                this.font,
                nameInputX,
                nameInputY,
                nameInputWidth,
                nameInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        this.nameInput.setValue(fileName);
        this.nameInput.setFocused(true);
        this.nameInput.setBordered(true);
        this.addRenderableWidget(this.nameInput);

        // 创建文件路径输入框
        int pathInputX = PADDING * 2 + 50;
        int pathInputY = nameInputY + nameInputHeight + 5;
        int pathInputWidth = BUTTON_WIDTH * 3;
        int pathInputHeight = BUTTON_HEIGHT;
        this.pathInput = new EditBox(
                this.font,
                pathInputX,
                pathInputY,
                pathInputWidth,
                pathInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        this.pathInput.setValue(filePath);
        this.pathInput.setFocused(true);
        this.pathInput.setBordered(true);
        this.addRenderableWidget(this.pathInput);

        // 保存草稿按钮
        this.saveDraftButton =
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save"),
                                button -> saveDraft())
                        .pos(centerX + BUTTON_WIDTH, bottomY + 10)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build();
        this.addRenderableWidget(this.saveDraftButton);

        // 导出按钮
        this.exportButton =
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export"),
                                button -> {
                                    filePath = this.pathInput.getValue();
                                    exportFile(filePath);
                                })
                        .pos(centerX + BUTTON_WIDTH * 3 , bottomY + 10)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build();
        this.addRenderableWidget(this.exportButton);

        // 关闭按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.close"),
                                button -> this.onClose())
                        .pos(this.width - 80, 5)
                        .size(50, 20)
                        .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics);
        // 渲染标题
        graphics.drawString(
                this.font,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.title"),
                PADDING,
                8,
                0xFFFFFF
        );
        // 提示信息
        graphics.drawString(
                this.font,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint"),
                PADDING,
                8 + this.font.lineHeight + 2,
                0x888888);
        graphics.drawString(
                this.font,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.name"),
                PADDING * 2, this.nameInput.getY(),
                0xFFFFFF);
        graphics.drawString(
                this.font,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.path"),
                PADDING * 2, this.pathInput.getY(),
                0xFFFFFF);
        // 渲染输入框和其他组件
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 导出内容到草稿
    private void saveDraft() {
        String content = textInput.getValue();
        if (TextLoader.saveToLocalFiles("temp.md", content) && Minecraft.getInstance().player != null) {
            this.onClose();
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save.success"),
                    false);
        } else {
            SimpleCardMemo.LOGGER.error("Fail to save draft!");
        }
    }

    // 导出内容到文件
    private void exportFile(String path) {
        String content = textInput.getValue();
        if (content.trim().isEmpty()) {
            textInput.setValue(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.content").getString());
            return;
        }
        // 检测没有重名文件
        if (Files.exists(SimpleCardMemo.DATA_DIR.resolve(path))) {
            pathInput.setValue(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.path").getString());
            return;
        }

        if (TextLoader.saveToLocalFiles(path, content) && Minecraft.getInstance().player != null) {
            this.onClose();
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export.success"),
                    false);
        }
        fileName = nameInput.getValue();
        // 给予玩家
        if (Minecraft.getInstance().player != null) {
            ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
            viewer.setHoverName(Component.literal(fileName));
            MemoViewerItem.setDisplayName(viewer, filePath);
            MemoViewerItem.setFilePath(viewer, fileName);
            MemoViewerItem.setAuthor(viewer, Minecraft.getInstance().player.getName().getString());
            Minecraft.getInstance().player.getInventory().add(viewer);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Ctrl+S 草稿
        if (Screen.hasControlDown() && keyCode == 83) {
            saveDraft();
            return true;
        }
        // Ctrl+E 导出
        if (Screen.hasControlDown() && keyCode == 69) {
            exportFile(fileName);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
