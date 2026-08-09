package com.github.rd806.simplecardmemo.init.container.screen;

import com.github.rd806.simplecardmemo.SimpleCardMemo;
import com.github.rd806.simplecardmemo.init.container.menu.MailMenu;
import com.github.rd806.simplecardmemo.memo.cache.CacheSystem;
import com.github.rd806.simplecardmemo.network.mail.MailStatus;
import com.github.rd806.simplecardmemo.init.item.MemoViewerItem;
import com.github.rd806.simplecardmemo.memo.MemoInfo;
import com.github.rd806.simplecardmemo.network.Channel;
import com.github.rd806.simplecardmemo.network.mail.MemoPacketReceive;
import com.github.rd806.simplecardmemo.network.mail.MemoPacketSend;
import com.github.rd806.simplecardmemo.setup.ClientSetup;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

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
    private static MailStatus status;
    private static String content;
    private static String message;

    public MailScreen(MailMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        // GUI 的宽度与高度（像素）
        // 这些值通常需要与背景贴图尺寸保持一致
        this.mailMenu = menu;
        this.imageWidth = 175;
        this.imageHeight = 210;
        status = MailStatus.DEFAULT;
    }

    @Override
    protected void init() {
        super.init();
        // 计算 GUI 左上角在屏幕上的位置
        leftPos = (this.width - this.imageWidth) / 2;
        topPos = (this.height - this.imageHeight) / 2;
        this.inventoryLabelY = 115;
        // 绘制按钮
        renderButton();
    }

    private void renderButton() {
        // 输入框
        nameInput = new EditBox(
                this.font,
                leftPos + 43,
                topPos + 21,
                90,
                18,
                Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.input")
        );
        nameInput.setMaxLength(256);
        nameInput.setBordered(false);
        nameInput.setHint(Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.input.hint")
                .withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
        nameInput.setTextColor(0xF3EFE0);
        addRenderableWidget(nameInput);
        // 发送按钮
        Button sendButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.send"),
                        button -> sendMemo())
                .pos(leftPos + 97, topPos + 46)
                .size(50, 18)
                .build();
        addRenderableWidget(sendButton);
        // 接收按钮
        Button receiveButton = Button.builder(Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.receive"),
                        button -> receiveMemo())
                .pos(leftPos + 97, topPos + 75)
                .size(50, 18)
                .build();
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
        renderHint(graphics, status);
        // 渲染物品提示
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    // 发送信件
    private void sendMemo() {
        status = MailStatus.DEFAULT;
        target = nameInput.getValue();
        if (target.isEmpty()) {
            status = MailStatus.NO_TARGET;
            return;
        }
        // 获取发送的文件
        ItemStack stack = mailMenu.getItemStackHandler().getStackInSlot(MailMenu.INPUT_SLOT);
        if (stack.equals(ItemStack.EMPTY)) {
            SimpleCardMemo.LOGGER.warn("The memo sent is empty!");
            status = MailStatus.EMPTY_SEND;
            return;
        }
        // 构造发送信息
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            message = player.getName().getString() + " " + I18n.get(SimpleCardMemo.MODID + ".memo_mail.send");
        }
        // 获取发送的内容
        MemoInfo memoInfo = MemoViewerItem.getMemoInfo(stack);
        // 异步加载
        CompletableFuture.runAsync(() -> content = CacheSystem.getMemoContentWithCache(memoInfo, ClientSetup.clientCache))
                .thenAccept(data -> Minecraft.getInstance().execute(() ->
                        Channel.CHANNEL.send(
                                PacketDistributor.SERVER.noArg(),
                                new MemoPacketSend(content, target, message)
                        )))
                .exceptionally(e -> {
                            SimpleCardMemo.LOGGER.error("Error on sending mail", e);
                            return null;
                        });
    }

    // 接收信件
    private void receiveMemo() {
        status = MailStatus.DEFAULT;
        this.target = nameInput.getValue();
        if (target.isEmpty()) {
            status = MailStatus.NO_TARGET;
            return;
        }
        Channel.CHANNEL.send(
                PacketDistributor.SERVER.noArg(),
                new MemoPacketReceive(target)
        );
    }

    // 渲染屏幕提示文字
    private void renderHint(GuiGraphics graphics, MailStatus cases) {
        switch (cases) {
            case SUCCESS_SEND -> graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.message.success_send"),
                    leftPos + 43, topPos + 100,
                    0x008000, false
            );
            case SUCCESS_RECEIVE -> graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.message.success_receive"),
                    leftPos + 43, topPos + 100,
                    0x008000, false
            );
            case EMPTY_SEND -> graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.message.empty_send"),
                    leftPos + 43, topPos + 100,
                    0xFF5555, false
            );
            case EMPTY_RECEIVE -> graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.message.empty_receive"),
                    leftPos + 43, topPos + 100,
                    0xFF5555, false
            );
            case NO_TARGET -> graphics.drawString(
                    this.font,
                    Component.translatable(SimpleCardMemo.MODID + ".gui.mail_screen.message.no_target"),
                    leftPos + 43, topPos + 100,
                    0xFF5555, false
            );
            case DEFAULT -> {}
        }
    }

    public static void setStatus(MailStatus status1) {
        status = status1;
    }
}
