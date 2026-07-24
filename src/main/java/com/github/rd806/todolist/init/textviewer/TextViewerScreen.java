package com.github.rd806.todolist.init.textviewer;

import com.github.rd806.todolist.Config;
import com.github.rd806.todolist.Todolist;
import dev.dediamondpro.minemark.minecraft.MineMarkDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextViewerScreen extends Screen {

    private final String filePath;
    private final boolean isLocalFile;
    // 待渲染文本
    private String renderedText;
    private MineMarkDrawable markdownText;

    // 页面设置
    private float footer;

    /**
     * #contentX################ contentW ##################
     * contentY
     * #
     * #
     * contentH
     * #
     * #
     * #
     * #####################################################
     **/

    private float contentX;
    private float contentY;
    private float contentW;
    private float contentH;

    private float totalHeight;
    // 滚动设置
    private double scrollOffset = 0;
    private double maxScroll = 0;

    protected TextViewerScreen(String path, boolean source) {
        // 界面的标题
        super(Component.translatable(Todolist.MODID + ".gui.viewer_screen"));
        // 初始化数据
        this.renderedText = TextLoader.loadText(path, source);
        if (Config.ENABLE_MARKDOWN.get()) {
            try {
                markdownText = new MineMarkDrawable(renderedText);
            } catch (Exception e) {
                renderedText = Component.translatable(Todolist.MODID + ".gui.text.error").toString();
            }
        }
        this.filePath = path;
        this.isLocalFile = source;
    }

    // 设置内容左边距
    private void setContent() {
        float screenWidth = this.width;
        switch (Config.PAGE_MARGIN.get()) {
            case WIDE -> this.contentX = screenWidth * ((float) 1 / 3);
            case MEDIUM -> this.contentX =  screenWidth * ((float) 1 / 4);
            case NARROW -> this.contentX =  screenWidth * ((float) 1 / 5);
        }
        this.contentY = 30;
        this.footer = 30;
        switch (Config.PAGE_MARGIN.get()) {
            case WIDE -> this.contentW = screenWidth * ((float) 1 / 3);
            case MEDIUM -> this.contentW = screenWidth * ((float) 2 / 4);
            case NARROW -> this.contentW = screenWidth * ((float) 3 / 5);
        }
        this.contentH = this.width - this.footer - this.contentY;
    }

    // 设置内容高度
    private void setContentHeight() {
        if (markdownText != null) {
            this.totalHeight = markdownText.getHeight();
        } else {
            this.totalHeight = renderedText.lines().count() * 20;
        }
    }

    // 重新计算
    private void recalculate() {
        setContent();
        setContentHeight();

        this.maxScroll = Math.max(0, this.totalHeight - this.contentH);
    }

    @Override
    protected void init() {
        super.init();
        // 这里可以添加按钮等交互组件
        // 添加重载组件
        recalculate();
        this.addRenderableWidget(new Button.Builder(
                Component.translatable(Todolist.MODID + ".gui.button.reload"),
                button -> this.renderedText = TextLoader.loadText(filePath, isLocalFile))
                .pos(this.width / 2 - 50, (int) (this.contentY + this.contentH + 5))
                .size(100, 20)
                .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        recalculate();

        // 渲染背景（灰色半透明背景）
        this.renderBackground(graphics);
        // 渲染文件路径
        graphics.drawString(this.font, filePath, 10, 5, 0xAAAAAA);
        // 渲染文本内容
        renderContent(graphics, mouseX, mouseY);
        // 渲染滚动条
        renderScrollBar(graphics);
        // 渲染其他组件（如果有）
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 渲染文本
    private void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        // 使用裁剪
        graphics.enableScissor(
                (int) contentX,
                (int) contentY,
                (int) contentX + (int) contentW,
                (int) contentY + (int) contentH
        );

        graphics.pose().pushPose();
        graphics.pose().translate(0, - this.scrollOffset, 0);

        // 检查文件内容
        if (this.renderedText == null) {
            graphics.drawCenteredString(this.font, Component.translatable(Todolist.MODID + ".gui.text.error"),
                    20, (int) this.contentY, 0xFFFFFF);
            return;
        }
        // 渲染文件内容
        if (Config.ENABLE_MARKDOWN.get()) {
            this.markdownText.draw(this.contentX, this.contentY, this.contentW, mouseX, mouseY, graphics);
        } else {
            String[] lines = renderedText.split("\n");
            int y = (int) this.contentY;
            for (String line : lines) {
                graphics.drawString(this.font, line, (int) this.contentX, y, 0xFFFFFF);
                y += this.font.lineHeight + 2;
            }
        }

        graphics.pose().popPose();
        graphics.disableScissor();
    }

    // 渲染滚动条
    private void renderScrollBar(GuiGraphics graphics) {
        if (this.maxScroll <= 0) return;

        int barX = (int) (contentX + contentW + 4);
        int barY = (int) contentY;
        int barW = 6;
        int barH = (int) contentH;

        graphics.fill(barX, barY, barX + barW, barY + barH, 0x33FFFFFF);

        // 滑块
        float progress = (float) (scrollOffset / maxScroll);
        int thumbHeight = Math.max(20, (int) (barH * Math.min(1, (contentH / totalHeight))));
        int thumbY = barY + (int) ((barH - thumbHeight) * progress);

        graphics.fill(barX, thumbY, barX + barW, thumbY + thumbHeight, 0xCCFFFFFF);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (this.maxScroll > 0) {
            this.scrollOffset -= amount * 20;
            this.scrollOffset = Math.max(0, Math.min(this.scrollOffset, this.maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean isPauseScreen() {
        // 打开此界面时游戏不暂停
        return false;
    }

}
