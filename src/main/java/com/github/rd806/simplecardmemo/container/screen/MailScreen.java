package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MailScreen extends AbstractContainerScreen<MailMenu> {

    private static final ResourceLocation MAIL_GUI =
            ResourceLocation.parse(SimpleCardMemo.MODID + ":textures/container/mail.png");

    public MailScreen(MailMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        // GUI 的宽度与高度（像素）
        // 这些值通常需要与背景贴图尺寸保持一致
        this.imageWidth = 175;
        this.imageHeight = 165;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 设置渲染使用的 Shader
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        // 设置颜色（RGBA），1 表示不改变原贴图颜色
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        // 绑定要绘制的纹理
        RenderSystem.setShaderTexture(0, MAIL_GUI);
        // 计算 GUI 左上角的位置，使界面居中显示
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        // 绘制贴图
        guiGraphics.blit(MAIL_GUI, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制界面背景
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }
}
