package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.Config;
import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.memo.cache.ClientMemoCache;
import dev.dediamondpro.minemark.minecraft.MineMarkDrawable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class MemoViewerScreen extends Screen {

    private final MemoInfo memoInfo;
    // 待渲染文本
    private String renderedText;
    private MineMarkDrawable markdownText;

    // 页面设置
    private float header;
    private float margin;
    private float footer;
    // 滚动设置
    private float totalHeight = 0;
    private double scrollOffset = 0;
    private double maxScroll = 0;
    // 拖动设置
    private boolean isDragging = false;
    private int dragStartY = 0;
    private int dragStartOffset = 0;

    public MemoViewerScreen(String content, MemoInfo memoInfo) {
        // 界面的标题
        super(Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen"));
        // 初始化数据
        this.renderedText = content;
        this.memoInfo = memoInfo;
        try {
            this.markdownText = new MineMarkDrawable(renderedText);
        } catch (Exception e) {
            renderedText = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error").toString();
            SimpleCardMemo.LOGGER.error("Couldn't load markdown text!", e);
        }
    }

    private void reload(MemoInfo memoInfo) {
        this.renderedText = ClientMemoCache.getMemoContent(memoInfo);
        try {
            this.markdownText = new MineMarkDrawable(renderedText);
        } catch (Exception e) {
            renderedText = Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error").toString();
            SimpleCardMemo.LOGGER.error("Couldn't load markdown text!", e);
        }
    }

    // 设置页面边距
    private void resetContent() {
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

    private void calculateScrollOffset() {
        // 计算高度
        if (markdownText != null) {
            totalHeight = markdownText.getHeight();
        } else if (renderedText != null) {
            totalHeight = renderedText.lines().count() * 20;
        } else {
            totalHeight = height - header - footer;
        }
        // 计算滚动
        this.maxScroll = Math.max(0, totalHeight - height + footer + header);
    }

    @Override
    protected void init() {
        super.init();
        // 重新计算
        resetContent();
        calculateScrollOffset();
        this.addRenderableWidget(new Button.Builder(Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.reload"),
                button -> reload(memoInfo))
                .pos(this.width / 2 - 50, (int) (this.height - footer + 5))
                .size(100, 20)
                .build()
        );
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景（灰色半透明背景）
        this.renderBackground(graphics);
        // 渲染其他组件（如果有）
        super.render(graphics, mouseX, mouseY, partialTick);
        // 计算滚动
        calculateScrollOffset();
        // 渲染文件名称
        graphics.drawString(this.font, memoInfo.getMemoName(), 10, 5, 0xAAAAAA);
        // 渲染文本内容
        renderContent(graphics, mouseX, mouseY);
        // 渲染滚动条
        renderScrollBar(graphics);
    }

    // 渲染文本
    private void renderContent(GuiGraphics graphics, int mouseX, int mouseY) {
        int contentX = (int) margin;
        int contentY = (int) header;
        int contentW = (int) (this.width - 2 * margin);
        int contentH = (int) (this.height - header - footer);
        // 使用裁剪
        graphics.enableScissor(
                contentX - 5, contentY - 5,
                contentX + contentW, contentY + contentH
        );
        graphics.pose().pushPose();
        float drawY = contentY - (float) scrollOffset;
        // 检查文件内容
        if (this.renderedText == null) {
            graphics.drawCenteredString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.viewer_screen.error")
                            .append(memoInfo.getMemoPath()),
                    20, contentY, 0xFFFFFF);
            return;
        }
        // 渲染文件内容
        try {
            this.markdownText.draw(contentX, drawY, contentW, mouseX, mouseY, graphics);
        } catch (Exception e) {
            SimpleCardMemo.LOGGER.error("Couldn't render markdown text!", e);
            String[] lines = renderedText.split("\n");
            int y = contentY;
            for (String line : lines) {
                graphics.drawString(this.font, line, contentX, y, 0xFFFFFF);
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
        int barY = (int) header;
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // 检查是否点击在滚动条滑块上（开始拖动）
        if (button == 0 && maxScroll > 0) {
            int barX = (int) (this.width - margin + 4);
            int barY = (int) footer;
            int barW = 6;
            int barH = (int) (this.height - header - footer);

            float visibleRatio = totalHeight / barH;
            int thumbHeight = Math.max(20, (int)(barH * visibleRatio));
            float progress = (float) (scrollOffset / maxScroll);
            int thumbY = barY + (int)((barH - thumbHeight) * progress);

            // 判断是否点击在滑块上
            if (mouseX >= barX && mouseX <= barX + barW && mouseY >= thumbY && mouseY <= thumbY + thumbHeight) {
                isDragging = true;
                dragStartY = (int) mouseY;
                dragStartOffset = (int) scrollOffset;
                return true;
            }
            // 点击滚动条轨道，跳转到对应位置
            if (mouseX >= barX && mouseX <= barX + barW && mouseY >= barY && mouseY <= barY + barH) {
                float clickProgress = (float) (mouseY - barY) / barH;
                scrollOffset = (int)(clickProgress * maxScroll);
                scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (isDragging && button == 0) {
            int deltaY = (int) (mouseY - dragStartY);
            int barHeight = (int) (this.height - header - footer);
            float progress = (float) deltaY / barHeight;
            scrollOffset = dragStartOffset + (int) (progress * maxScroll);
            scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        isDragging = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean isPauseScreen() {
        // 打开此界面时游戏不暂停
        return false;
    }
}
