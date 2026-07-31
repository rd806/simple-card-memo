package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MailScreen extends AbstractContainerScreen<MailMenu> {

    // 背景GUI图片
    private static final ResourceLocation MAIL_GUI =
            ResourceLocation.parse(SimpleCardMemo.MODID + ":textures/container/mail.png");
    // 输入框
    private EditBox receiverInput;
    // GUI 左上角的位置，使界面居中显示
    private int leftPos;
    private int topPos;

    public MailScreen(MailMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        // GUI 的宽度与高度（像素）
        // 这些值通常需要与背景贴图尺寸保持一致
        this.imageWidth = 175;
        this.imageHeight = 165;
    }

    @Override
    protected void init() {
        super.init();
        // 计算 GUI 左上角在屏幕上的位置
        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;
        // 创建输入框
        this.receiverInput = new EditBox(
                this.font,
                leftPos + 59,
                topPos + 21,
                60,
                20,
                Component.translatable(SimpleCardMemo.MODID + ".mail.gui.message") // 提示文本
        );
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 设置渲染使用的 Shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // 设置颜色（RGBA），1 表示不改变原贴图颜色
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        // 绑定要绘制的纹理
        RenderSystem.setShaderTexture(0, MAIL_GUI);
        // 绘制贴图
        guiGraphics.blit(MAIL_GUI, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        // 绘制输入框
        this.receiverInput.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制界面背景
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        // 渲染物品提示
        this.renderTooltip(graphics, mouseX, mouseY);
    }
}
