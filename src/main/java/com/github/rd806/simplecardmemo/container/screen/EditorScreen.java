package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.memo.MemoConfig;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.MemoLoader;
import com.github.rd806.simplecardmemo.items.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.get.MemoPacketNew;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Files;
import java.time.LocalDate;
import java.util.Objects;

public class EditorScreen extends Screen {

    private final String initialContent;
    private String filePath;
    private String displayName;
    private String author;
    private boolean isLocalFile;

    // UI 组件
    private EditBox nameInput;
    private EditBox pathInput;
    private EditBox authorInput;
    private Checkbox sourceInput;
    private MultiLineEditBox textInput;
    // 布局常量
    private static int PADDING;
    private static int HEADER;
    private static int FOOTER;
    // 按钮常量
    private static final int BUTTON_WIDTH = 50;
    private static final int BUTTON_HEIGHT = 20;

    public EditorScreen() {
        super(Component.translatable(SimpleCardMemo.MODID + ".gui.editor.title"));
        this.initialContent = MemoLoader.loadFromLocalFiles(CacheSystem.getTempMemo());
        setDefaultValues();
        MemoLoader.createTempFile();
    }

    // 设置默认名称
    private void setDefaultValues() {
        this.filePath = "memo_" + LocalDate.now() + ".md";
        this.displayName = filePath;
        this.author = Objects.requireNonNull(Minecraft.getInstance().player).getName().getString();
        this.isLocalFile = true;
    }

    @Override
    protected void init() {
        super.init();

        PADDING = (int) (this.width * 0.15);
        HEADER = (int) (this.height * 0.2);
        FOOTER = (int) (this.height * 0.2);
        int EDIT_BOX_WIDTH = (this.width - 2 * PADDING) / 4;

        // 创建文件名输入框
        this.nameInput = new EditBox(
                this.font,
                PADDING,
                HEADER,
                EDIT_BOX_WIDTH,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        this.nameInput.setBordered(true);
        this.nameInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.name"));
        this.addRenderableWidget(this.nameInput);

        // 创建文件路径输入框
        this.pathInput = new EditBox(
                this.font,
                nameInput.getX() + EDIT_BOX_WIDTH + 5,
                HEADER,
                EDIT_BOX_WIDTH,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        this.pathInput.setBordered(true);
        this.pathInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.path"));
        this.addRenderableWidget(this.pathInput);

        // 创建作者输入框
        authorInput = new EditBox(
                this.font,
                pathInput.getX() + EDIT_BOX_WIDTH + 5,
                HEADER,
                EDIT_BOX_WIDTH,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        authorInput.setBordered(true);
        authorInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.author"));
        addRenderableWidget(this.authorInput);

        // 复选框
        sourceInput = new Checkbox(
                PADDING,
                this.height - FOOTER + 20,
                20,
                20,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.islocal"),
                isLocalFile
        );
        this.addRenderableWidget(this.sourceInput);

        // 创建多行文本输入框
        int textInputWidth = this.width - PADDING * 2;
        int textInputHeight = this.height - HEADER - FOOTER - BUTTON_HEIGHT;
        textInput = new MultiLineEditBox(
                this.font,
                PADDING,
                HEADER + BUTTON_HEIGHT + 5,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input"),
                Component.literal("")
        );
        textInput.setValue(this.initialContent);
        textInput.setCharacterLimit(100000);
        textInput.setFocused(true);
        addRenderableWidget(this.textInput);

        // 使用默认值填充
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.default"),
                                button -> {
                                    setDefaultValues();
                                    this.nameInput.setValue(this.displayName);
                                    this.pathInput.setValue(this.filePath);
                                    this.authorInput.setValue(this.author);
                                })
                        .pos(authorInput.getX() + EDIT_BOX_WIDTH + 5, HEADER)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 保存草稿按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save"),
                        button -> saveDraft())
                        .pos(this.width - PADDING - BUTTON_WIDTH * 2 - 5, this.height - FOOTER + 20)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 导出按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export"),
                        button -> {
                            filePath = this.pathInput.getValue();
                            displayName = this.nameInput.getValue();
                            author = this.authorInput.getValue();
                            isLocalFile = this.sourceInput.selected();
                            exportItem(filePath);
                            MemoConfig.saveToConfig();
                        })
                        .pos(this.width - PADDING - BUTTON_WIDTH, this.height - FOOTER + 20)
                        .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                        .build()
        );
        // 关闭按钮
        this.addRenderableWidget(
                Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.close"),
                                button -> this.onClose())
                        .pos(this.width - PADDING - BUTTON_WIDTH, 5)
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
        // 渲染输入框和其他组件
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 导出内容到草稿
    private void saveDraft() {
        String content = textInput.getValue();
        if (MemoLoader.saveToLocalFiles(content, CacheSystem.getTempMemo()) && Minecraft.getInstance().player != null) {
            this.onClose();
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save.success"),
                    false);
        } else {
            SimpleCardMemo.LOGGER.error("Fail to save draft!");
        }
    }

    // 导出内容到文件
    private void exportItem(String path) {
        String content = textInput.getValue();
        if (content.trim().isEmpty()) {
            textInput.setValue(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.content").getString());
            return;
        }
        // 给予玩家
        if (Minecraft.getInstance().player != null) {
            ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
            MemoInfo memoInfo = new MemoInfo(displayName, filePath, author, isLocalFile, System.currentTimeMillis());
            if (isLocalFile) {
                // 检测重名文件
                if (Files.exists(SimpleCardMemo.DATA_DIR.resolve(path))) {
                    pathInput.setValue("");
                    pathInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.path"));
                    return;
                }
                MemoLoader.saveToLocalFiles(content, memoInfo);
                MemoViewerItem.setFilePath(viewer, filePath);
            } else {
                filePath = content;
                memoInfo.setMemoPath(filePath);
                MemoViewerItem.setFilePath(viewer, content);
            }
            // 添加到列表
            MemoConfig.MEMO_LIST.add(memoInfo);
            // 设置物品
            viewer.setHoverName(Component.literal(displayName));
            // 发送网络包
            Channel.CHANNEL.send(
                    PacketDistributor.SERVER.noArg(),
                    new MemoPacketNew(memoInfo)
            );
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export.success"),
                    false);
            this.onClose();
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
            filePath = this.pathInput.getValue();
            displayName = this.nameInput.getValue();
            author = this.authorInput.getValue();
            exportItem(filePath);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
