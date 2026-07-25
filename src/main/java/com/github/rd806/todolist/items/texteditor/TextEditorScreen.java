package com.github.rd806.todolist.items.texteditor;

import com.github.rd806.todolist.Todolist;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class TextEditorScreen extends Screen {

    protected TextEditorScreen() {
        super(Component.translatable(Todolist.MODID + ".gui.editor_screen"));
    }



    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 渲染背景（灰色半透明背景）
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTick);
    }

}
