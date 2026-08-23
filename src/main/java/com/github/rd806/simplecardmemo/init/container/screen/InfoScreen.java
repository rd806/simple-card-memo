package com.github.rd806.simplecardmemo.init.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.InfoMenu;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InfoScreen extends AbstractContainerScreen<InfoMenu> {

    private static final ResourceLocation INFO_GUI =
            ResourceLocation.tryBuild(SimpleCardMemo.MODID, "textures/container/info.png");

    // GUI 左上角的位置，使界面居中显示
    private int leftPos;
    private int topPos;
    private static final int textInputWidth = 90;
    private static final int textInputHeight = 20;
    // 输入框
    private EditBox nameInput;
    private EditBox pathInput;
    private EditBox authorInput;

    private static int index;
    private static MemoInfo memo;

    public InfoScreen(InfoMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 175;
        this.imageHeight = 197;
        index = menu.getMemoIndex();
        memo = ClientSetup.clientConfig.getMemoList().get(index);
    }

    @Override
    protected void init() {
        super.init();
        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;
        renderInputBox();
    }

    private void renderInputBox() {
        // 名称
        nameInput = new EditBox(
                this.font,
                leftPos + 63,
                topPos + 39,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        nameInput.setBordered(false);
        nameInput.setMaxLength(255);
        nameInput.setValue(memo.getMemoName());
        addRenderableWidget(nameInput);
        // 路径
        pathInput = new EditBox(
                this.font,
                nameInput.getX(),
                nameInput.getY() + 35,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        pathInput.setBordered(false);
        pathInput.setMaxLength(255);
        pathInput.setValue(memo.getMemoPath());
        addRenderableWidget(pathInput);
        // 作者
        authorInput = new EditBox(
                this.font,
                nameInput.getX(),
                pathInput.getY() + 35,
                textInputWidth,
                textInputHeight,
                Component.translatable(SimpleCardMemo.MODID + ".gui.editor_screen.input")
        );
        authorInput.setBordered(false);
        authorInput.setValue(memo.getMemoAuthor());
        addRenderableWidget(authorInput);
        // 按钮
        Button doneButton = Button.builder(Component.translatable("gui.simplecardmemo.info_screen.done"),
                        button -> save())
                .pos(nameInput.getX(), authorInput.getY() + 35)
                .size(50, 20)
                .build();
        addRenderableWidget(doneButton);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 设置渲染使用的 Shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // 设置颜色（RGBA），1 表示不改变原贴图颜色
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        // 绑定要绘制的纹理
        RenderSystem.setShaderTexture(0, Objects.requireNonNull(INFO_GUI));
        // 绘制贴图
        guiGraphics.blit(INFO_GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        renderString(graphics);
    }

    // 绘制字段
    private void renderString(GuiGraphics graphics) {
        // 名称
        graphics.drawString(
                this.font, Component.translatable("gui.simplecardmemo.info_screen.name"),
                leftPos + 15, nameInput.getY(),
                0x3F3F3F, false
        );
        // 路径
        graphics.drawString(
                this.font, Component.translatable("gui.simplecardmemo.info_screen.path"),
                leftPos + 15, pathInput.getY(),
                0x3F3F3F, false
        );
        // 作者
        graphics.drawString(
                this.font, Component.translatable("gui.simplecardmemo.info_screen.author"),
                leftPos + 15, authorInput.getY(),
                0x3F3F3F, false
        );
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        // 绘制标题
        graphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
    }

    private void save() {
        String name = nameInput.getValue();
        String path = pathInput.getValue();
        String author = authorInput.getValue();
        ClientSetup.clientConfig.getMemoList().get(index).setMemoName(name);
        ClientSetup.clientConfig.getMemoList().get(index).setMemoPath(path);
        ClientSetup.clientConfig.getMemoList().get(index).setMemoAuthor(author);
        ClientSetup.clientConfig.saveToConfig();
        // 打开管理器界面
        Channel.openManagerMenu();
    }
}
