package com.github.rd806.simplecardmemo.items.memoviewer;

import com.github.rd806.simplecardmemo.Config;
import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.memo.MemoLoader;
import dev.dediamondpro.minemark.minecraft.MineMarkDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class MemoViewerScreen extends Screen {

    private String filePath;
    private String fileName;
    private boolean isLocalFile;
    // 待渲染文本
    private String renderedText;
    private MineMarkDrawable markdownText;

    // 页面设置
    private float header;
    private float margin;
    private float footer;

    // 滚动设置
    private float totalHeight;
    private double scrollOffset = 0;
    private double maxScroll = 0;

    public MemoViewerScreen(String path, String name, boolean source) {
        // 界面的标题
        super(Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen"));
        // 初始化数据
        reload(path, name, source);
    }

    private void reload(String path, String name, boolean source) {
        this.renderedText = MemoLoader.loadText(path, source);
        if (Config.ENABLE_MARKDOWN.get()) {
            try {
                markdownText = new MineMarkDrawable(renderedText);
            } catch (Exception e) {
                renderedText = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error").toString();
                SimpleCardMemo.LOGGER.error("Couldn't load markdown text!", e);
            }
        }
        this.filePath = path;
        this.fileName = name;
        this.isLocalFile = source;
    }

    // 设置内容左边距
    private void setContent() {
        float screenWidth = this.width;
        float screenHeight = this.height;
        switch (Config.PAGE_MARGIN.get()) {
            case WIDE -> {
                this.margin = screenWidth * 0.35f;
                this.header = screenHeight * 0.2f;
                this.footer = screenHeight * 0.2f;
            }
            case MEDIUM -> {
                this.margin = screenWidth * 0.25f;
                this.header = screenHeight * 0.15f;
                this.footer = screenHeight * 0.15f;
            }
            case NARROW -> {
                this.margin = screenWidth * 0.15f;
                this.header = screenHeight * 0.1f;
                this.footer = screenHeight * 0.1f;
            }
        }
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

        this.maxScroll = Math.max(0, this.totalHeight - this.height + this.footer + this.header);
    }

    @Override
    protected void init() {
        super.init();
        // 这里可以添加按钮等交互组件
        // 添加重载组件
        recalculate();
        this.addRenderableWidget(new Button.Builder(Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.reload"),
                button -> reload(filePath, fileName, isLocalFile))
                .pos(this.width / 2 - 50, (int) (this.height - footer + 5))
                .size(100, 20)
                .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {

        recalculate();
        // 渲染背景（灰色半透明背景）
        this.renderBackground(graphics);
        // 渲染文件名称
        graphics.drawString(this.font, fileName, 10, 5, 0xAAAAAA);
        // 渲染文本内容
        renderContent(graphics, mouseX, mouseY);
        // 渲染滚动条
        renderScrollBar(graphics);
        // 渲染其他组件（如果有）
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    // 渲染文本
    private void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        float contentX = margin;
        float contentY = header;
        float contentW = this.width - 2 * margin;
        float contentH = this.height - header - footer;
        // 使用裁剪
        graphics.enableScissor(
                (int) contentX - 5,
                (int) contentY - 5,
                (int) contentX + (int) contentW,
                (int) contentY + (int) contentH
        );
        graphics.pose().pushPose();
        float drawY = contentY - (float) scrollOffset;

        // 检查文件内容
        if (this.renderedText == null) {
            graphics.drawCenteredString(this.font, Component.translatable(SimpleCardMemo.MODID + ".gui.text.error"),
                    20, (int) contentY, 0xFFFFFF);
            return;
        }
        // 渲染文件内容
        if (Config.ENABLE_MARKDOWN.get()) {
            this.markdownText.draw(contentX, drawY, contentW, mouseX, mouseY, graphics);
        } else {
            String[] lines = renderedText.split("\n");
            int y = (int) contentY;
            for (String line : lines) {
                graphics.drawString(this.font, line, (int) contentX, y, 0xFFFFFF);
                y += this.font.lineHeight + 2;
            }
        }

        graphics.pose().popPose();
        graphics.disableScissor();
    }

    // 渲染滚动条
    private void renderScrollBar(GuiGraphics graphics) {
        if (this.maxScroll <= 0) return;

        int barX = (int) (this.width - margin + 4);
        int barY = (int) footer;
        int barW = 6;
        int barH = (int) (this.height - header - footer);

        graphics.fill(barX, barY, barX + barW, barY + barH, 0x33FFFFFF);
        // 滑块
        float progress = (float) (scrollOffset / maxScroll);
        int thumbHeight = Math.max(20, (int) (barH * Math.min(1, (barH / totalHeight))));
        int thumbY = barY + (int) ((barH - thumbHeight) * progress);
        graphics.fill(barX, thumbY, barX + barW, thumbY + thumbHeight, 0xCCFFFFFF);
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (maxScroll > 0) {
            this.scrollOffset -= amount * 20;
            this.scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean handleComponentClicked(@Nullable Style pStyle) {
        return super.handleComponentClicked(pStyle);
    }

    @Override
    public boolean isPauseScreen() {
        // 打开此界面时游戏不暂停
        return false;
    }

}
