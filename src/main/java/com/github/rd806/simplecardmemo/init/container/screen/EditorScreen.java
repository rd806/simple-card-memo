package com.github.rd806.simplecardmemo.init.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.ModItems;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.manage.MemoLoader;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.get.MemoPacketNew;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
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

    private static final MemoInfo tempMemo = new MemoInfo(
            "temp", "temp.md", "Default", true, 0);
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
        this.initialContent = MemoLoader.loadText(tempMemo);
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
        // 初始化布局
        PADDING = (int) (this.width * 0.1);
        HEADER = (int) (this.height * 0.2);
        FOOTER = (int) (this.height * 0.2);
        renderEditBox();
    }

    private void renderEditBox() {
        int textInputWidth = this.width - PADDING * 2 - BUTTON_WIDTH * 2 - 10;

        // 创建文件路径输入框
        pathInput = new EditBox(
                this.font,
                PADDING,
                HEADER,
                textInputWidth,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        pathInput.setMaxLength(1000);
        pathInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.path"));
        addRenderableWidget(this.pathInput);

        // 创建文本内容输入框
        int textInputHeight = this.height - HEADER - FOOTER;
        textInput = new MultiLineEditBox(
                this.font,
                PADDING,
                pathInput.getY() + BUTTON_HEIGHT + 5,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input"),
                Component.literal("")
        );
        textInput.setValue(this.initialContent);
        textInput.setCharacterLimit(100000);
        textInput.setFocused(true);
        addRenderableWidget(this.textInput);

        // 创建文件名输入框
        nameInput = new EditBox(
                this.font,
                textInput.getX() + textInput.getWidth() + 10,
                HEADER,
                BUTTON_WIDTH * 2,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        nameInput.setBordered(true);
        nameInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.name"));
        addRenderableWidget(this.nameInput);

        // 创建作者输入框
        authorInput = new EditBox(
                this.font,
                nameInput.getX(),
                nameInput.getY() + BUTTON_HEIGHT + 5,
                BUTTON_WIDTH * 2,
                BUTTON_HEIGHT,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        authorInput.setBordered(true);
        authorInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.hint.author"));
        addRenderableWidget(this.authorInput);

        // 复选框
        sourceInput = new Checkbox(
                nameInput.getX(), authorInput.getY() + BUTTON_HEIGHT + 5,
                20, 20,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.islocal"),
                isLocalFile
        );
        this.addRenderableWidget(this.sourceInput);

        // 使用默认值填充
        Button defaultButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.default"),
                        button -> {
                            setDefaultValues();
                            this.nameInput.setValue(this.displayName);
                            this.pathInput.setValue(this.filePath);
                            this.authorInput.setValue(this.author);
                        })
                .pos(nameInput.getX(), sourceInput.getY() + 25)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(defaultButton);

        // 保存草稿按钮
        Button saveButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save"),
                        button -> saveDraft())
                .pos(nameInput.getX(), defaultButton.getY() + 25)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(saveButton);

        // 导出按钮
        Button exportButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export"),
                        button -> {
                            filePath = this.pathInput.getValue();
                            displayName = this.nameInput.getValue();
                            author = this.authorInput.getValue();
                            isLocalFile = this.sourceInput.selected();
                            exportItem(filePath);
                            ClientSetup.clientConfig.saveToConfig();
                        })
                .pos(nameInput.getX(), saveButton.getY() + 25)
                .size(BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
        this.addRenderableWidget(exportButton);
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
        isLocalFile = sourceInput.selected();
        textInput.visible = isLocalFile;
        // 渲染输入框和其他组件
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 导出内容到草稿
    private void saveDraft() {
        String content = textInput.getValue();
        if (MemoLoader.saveToLocal(content, tempMemo) && Minecraft.getInstance().player != null) {
            this.onClose();
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.save.success"),
                    false);
        } else {
            SimpleCardMemo.LOGGER.error("Fail to save draft!");
        }
    }

    // 导出内容到物品
    private void exportItem(String path) {
        // 给予玩家
        ItemStack viewer = new ItemStack(ModItems.MEMO_VIEWER.get());
        MemoInfo memoInfo = new MemoInfo(displayName, filePath, author, true, System.currentTimeMillis());
        if (isLocalFile) {
            // 检测重名文件
            if (Files.exists(SimpleCardMemo.DATA_DIR.resolve(path))) {
                pathInput.setValue("");
                pathInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.path"));
                return;
            }
            String content = textInput.getValue();
            if (content.trim().isEmpty()) {
                textInput.setValue(Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input.error.content")
                        .getString());
                return;
            }
            MemoLoader.saveToLocal(content, memoInfo);
        }
        // 添加到列表
        ClientSetup.clientConfig.getMemoList().add(memoInfo);
        // 设置物品
        viewer.setHoverName(Component.literal(displayName));
        // 发送网络包
        Channel.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new MemoPacketNew(viewer));
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.displayClientMessage(
                    Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.export.success"),
                    false);
        }
        this.onClose();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        return super.mouseScrolled(mouseX, mouseY, amount);
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
