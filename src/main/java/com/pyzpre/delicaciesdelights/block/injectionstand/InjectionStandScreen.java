package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class InjectionStandScreen extends AbstractContainerScreen<InjectionStandContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("delicacies_delights:textures/gui/container/injection_stand.png");


    public InjectionStandScreen(InjectionStandContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);

        if (this.menu.isCrafting()) {
            int progress = this.menu.getScaledProgress();
            int potionColor = this.menu.getPotionColor();
            int r = (potionColor >> 16) & 255;
            int g = (potionColor >> 8) & 255;
            int b = potionColor & 255;

            // Parameters for progress bars
            int[] widths = {14, 1, 1, 1, 1, 1, 1, 1, 1}; // Widths of each progress bar
            int[] heights = {3, 15, 13, 11, 9, 7, 5, 3, 1}; // Heights of each progress bar
            int[] xOffsets = {80, 94, 95, 96, 97, 98, 99, 100, 101}; // X offsets of each progress bar
            int[] yOffsets = {40, 34, 35, 36, 37, 38, 39, 40, 41}; // Y offsets of each progress bar

            int currentProgress = progress;
            for (int i = 0; i < widths.length; i++) {
                if (currentProgress <= 0) {
                    break;
                }
                int barWidth = Math.min(currentProgress, widths[i]);
                // Draw shadow bar
                drawProgressBar(guiGraphics, x + xOffsets[i], y + yOffsets[i] + heights[i], barWidth, 1, 104, 104, 104);
                drawProgressBar(guiGraphics, x + xOffsets[i], y + yOffsets[i], barWidth, heights[i], r, g, b);
                currentProgress -= widths[i]; // Reduce progress
            }
        }
    }


    private void drawProgressBar(GuiGraphics guiGraphics, int x, int y, int width, int height, int r, int g, int b) {
        RenderSystem.setShaderColor(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
        guiGraphics.fill(x, y, x + width, y + height, (r << 16) | (g << 8) | b | 0xFF000000);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset the shader color to default white
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int titleX = (this.imageWidth - this.font.width(this.title)) / 2;
        guiGraphics.drawString(this.font, this.title, titleX, 6, 0x3f3f3f, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            guiGraphics.renderTooltip(this.font, this.hoveredSlot.getItem(), mouseX, mouseY);
        }
        super.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
