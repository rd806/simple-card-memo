package com.github.rd806.simplecardmemo.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.items.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.send.MemoPacketReceive;
import com.github.rd806.simplecardmemo.network.send.MemoPacketSend;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class MailScreen extends AbstractContainerScreen<MailMenu> {

    // 背景GUI图片
    private static final ResourceLocation MAIL_GUI =
            ResourceLocation.parse(SimpleCardMemo.MODID + ":textures/container/mail.png");

    private final MailMenu mailMenu;

    // GUI 左上角的位置，使界面居中显示
    private int leftPos;
    private int topPos;

    private EditBox nameInput;
    private String target;

    public MailScreen(MailMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        // GUI 的宽度与高度（像素）
        // 这些值通常需要与背景贴图尺寸保持一致
        this.mailMenu = menu;
        this.imageWidth = 175;
        this.imageHeight = 165;
    }

    @Override
    protected void init() {
        super.init();
        // 计算 GUI 左上角在屏幕上的位置
        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;
        // 输入框
        nameInput = new EditBox(
                this.font,
                leftPos + 60,
                topPos + 6,
                60,
                20,
                Component.translatable(SimpleCardMemo.MODID + ".mail.gui.message")
        );
        // 发送按钮
        Button sendButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.send"),
                        button -> sendMemo())
                .pos(leftPos + 115, topPos + 32)
                .size(40, 16)
                .build();
        // 接收按钮
        Button receiveButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.receive"),
                        button -> receiveMemo())
                .pos(leftPos + 30, topPos + 54)
                .size(40, 16)
                .build();

        addRenderableWidget(nameInput);
        addRenderableWidget(sendButton);
        addRenderableWidget(receiveButton);
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
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 绘制界面背景
        renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
        // 渲染物品提示
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    // 发送信件
    private void sendMemo() {
        this.target = nameInput.getValue();
        // 获取发送的文件
        ItemStack stack = mailMenu.getItemStackHandler().getStackInSlot(MailMenu.INPUT_SLOT);
        if (stack.isEmpty()) {
            SimpleCardMemo.LOGGER.warn("Memo not found!");
            return;
        }
        // 获取发送的内容
        String content = CacheSystem.getMemoContentWithCache(MemoViewerItem.getMemoInfo(stack));
        Channel.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new MemoPacketSend(stack, content, target)
        );
    }

    // 接收信件
    private void receiveMemo() {
        this.target = nameInput.getValue();
        Channel.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new MemoPacketReceive(target)
        );
    }
}
