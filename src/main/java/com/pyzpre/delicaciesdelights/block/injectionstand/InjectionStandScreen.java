package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class InjectionStandScreen extends AbstractContainerScreen<InjectionStandContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation("delicacies_delights:textures/gui/container/injection_stand.png");
    private static final Logger LOGGER = LogManager.getLogger(); // Logger declaration

    // Data structure to store pixel information
    private static class Pixel {
        int x;
        int y;
        int color;

        Pixel(int x, int y, int color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }

    // List to hold all pixel data
    private final List<List<Pixel>> bubbleData = new ArrayList<>();
    private final InjectionStandEntity blockEntity;

    public InjectionStandScreen(InjectionStandContainer container, Inventory inv, Component title) {
        super(container, inv, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.blockEntity = container.getBlockEntity(); // Access the block entity from the container
        // Define pixel data with colors and positions
        //bubble 1
        List<Pixel> bubble1 = new ArrayList<>();
        bubble1.add(new Pixel(85, 17, 0xfff87e));
        bubble1.add(new Pixel(84, 18, 0xfff87e));
        bubble1.add(new Pixel(85, 18, 0xfff87e));
        bubble1.add(new Pixel(86, 18, 0xfff32d));
        bubble1.add(new Pixel(85, 19, 0xfff32d));
        bubbleData.add(bubble1);
        //bubble 2
        List<Pixel> bubble2 = new ArrayList<>();
        bubble2.add(new Pixel(90, 21, 0xffc100));
        bubble2.add(new Pixel(89, 22, 0xffc100));
        bubble2.add(new Pixel(90, 22, 0xecb813));
        bubble2.add(new Pixel(91, 22, 0xd8ae26));
        bubble2.add(new Pixel(90, 23, 0xd8ae26));
        bubbleData.add(bubble2);
        //bubble 3
        List<Pixel> bubble3 = new ArrayList<>();
        bubble3.add(new Pixel(86, 25, 0xd8ae26));
        bubble3.add(new Pixel(85, 26, 0xd8ae26));
        bubble3.add(new Pixel(86, 26, 0xb9931c));
        bubble3.add(new Pixel(87, 26, 0xb36b19));
        bubble3.add(new Pixel(86, 27, 0xb36b19));
        bubbleData.add(bubble3);
        //bubble 4
        List<Pixel> bubble4 = new ArrayList<>();
        bubble4.add(new Pixel(90, 29, 0xa86d1b));
        bubble4.add(new Pixel(91, 29, 0xa86d1b));
        bubble4.add(new Pixel(89, 30, 0xa86d1b));
        bubble4.add(new Pixel(90, 30, 0xa86d1b));
        bubble4.add(new Pixel(91, 30, 0xb36b19));
        bubble4.add(new Pixel(92, 30, 0xb15b10));
        bubble4.add(new Pixel(89, 31, 0xb36b19));
        bubble4.add(new Pixel(90, 31, 0xb15b10));
        bubble4.add(new Pixel(91, 31, 0xb15b10));
        bubble4.add(new Pixel(92, 31, 0xbf5a00));
        bubble4.add(new Pixel(90, 32, 0xbf5a00));
        bubble4.add(new Pixel(91, 32, 0xbf5a00));
        bubbleData.add(bubble4);
        //bubble 5
        List<Pixel> bubble5 = new ArrayList<>();
        bubble5.add(new Pixel(85, 30, 0xb36b19));
        bubble5.add(new Pixel(84, 31, 0xb36b19));
        bubble5.add(new Pixel(85, 31, 0xb36b19));
        bubble5.add(new Pixel(86, 31, 0xa86d1b));
        bubble5.add(new Pixel(85, 32, 0xb15b10));
        bubbleData.add(bubble5);
        //bubble 6
        List<Pixel> bubble6 = new ArrayList<>();
        bubble6.add(new Pixel(84, 34, 0xbf5a00));
        bubble6.add(new Pixel(85, 34, 0xbf5a00));
        bubble6.add(new Pixel(83, 35, 0xbf5a00));
        bubble6.add(new Pixel(84, 35, 0xbf5a00));
        bubble6.add(new Pixel(85, 35, 0xb15b10));
        bubble6.add(new Pixel(86, 35, 0xae3c00));
        bubble6.add(new Pixel(83, 36, 0xb15b10));
        bubble6.add(new Pixel(84, 36, 0xae3c00));
        bubble6.add(new Pixel(85, 36, 0xae3c00));
        bubble6.add(new Pixel(86, 36, 0x953300));
        bubble6.add(new Pixel(84, 37, 0x953300));
        bubble6.add(new Pixel(85, 37, 0x953300));
        bubbleData.add(bubble6);
        //bubble 7
        List<Pixel> bubble7 = new ArrayList<>();
        bubble7.add(new Pixel(91, 35, 0xbf5a00));
        bubble7.add(new Pixel(90, 36, 0xbf5a00));
        bubble7.add(new Pixel(91, 36, 0xae3c00));
        bubble7.add(new Pixel(92, 36, 0x953300));
        bubble7.add(new Pixel(91, 37, 0x953300));
        bubbleData.add(bubble7);
        //bubble 8
        List<Pixel> bubble8 = new ArrayList<>();
        bubble8.add(new Pixel(87, 39, 0xbf5a00));
        bubble8.add(new Pixel(88, 39, 0xbf5a00));
        bubble8.add(new Pixel(89, 39, 0xb15b10));
        bubble8.add(new Pixel(86, 40, 0xbf5a00));
        bubble8.add(new Pixel(87, 40, 0xbf5a00));
        bubble8.add(new Pixel(88, 40, 0xb15b10));
        bubble8.add(new Pixel(89, 40, 0xae3c00));
        bubble8.add(new Pixel(90, 40, 0x953300));
        bubble8.add(new Pixel(86, 41, 0xb15b10));
        bubble8.add(new Pixel(87, 41, 0xae3c00));
        bubble8.add(new Pixel(88, 41, 0xae3c00));
        bubble8.add(new Pixel(89, 41, 0x953300));
        bubble8.add(new Pixel(90, 41, 0x953300));
        bubble8.add(new Pixel(86, 42, 0xae3c00));
        bubble8.add(new Pixel(87, 42, 0xae3c00));
        bubble8.add(new Pixel(88, 42, 0x953300));
        bubble8.add(new Pixel(89, 42, 0x953300));
        bubble8.add(new Pixel(90, 42, 0x953300));
        bubble8.add(new Pixel(87, 43, 0x953300));
        bubble8.add(new Pixel(88, 43, 0x953300));
        bubble8.add(new Pixel(89, 43, 0x953300));
        bubbleData.add(bubble8);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {

        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (this.width - this.imageWidth) / 2;
        int y = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, this.imageWidth, this.imageHeight);


        // Render individual pixels only if blaze powder has been added
        if (isBlazePowderConsumed()) {
            drawPixels(guiGraphics, x, y);
        }

        if (this.menu.isCrafting()) {
            int progress = this.menu.getScaledProgress();
            int potionColor = this.menu.getPotionColor();
            int r = (potionColor >> 16) & 255;
            int g = (potionColor >> 8) & 255;
            int b = potionColor & 255;
            // Custom rendering for crafting progress
        }
    }

    private void drawPixels(GuiGraphics guiGraphics, int offsetX, int offsetY) {
        int totalBubbles = bubbleData.size();

        if (this.menu.isCrafting()) {
            // Get current crafting progress
            int craftingProgress = this.menu.getCraftingProgress();


            // Remove 1 bubble every 5 ticks
            int bubblesToRemove = (int) (craftingProgress / 5);


            // Loop over totalBubbles + 1 to ensure all bubbles can be removed
            bubblesToRemove = bubblesToRemove % (totalBubbles + 1);


            // Render the visible bubbles, skipping the first 'bubblesToRemove' bubbles
            for (int i = bubblesToRemove; i < totalBubbles; i++) {
                List<Pixel> bubble = bubbleData.get(i);

                for (Pixel pixel : bubble) {
                    int r = (pixel.color >> 16) & 255;
                    int g = (pixel.color >> 8) & 255;
                    int b = pixel.color & 255;
                    drawPixel(guiGraphics, offsetX + pixel.x, offsetY + pixel.y, r, g, b);
                }
            }
        } else {
            // Not crafting, render bubbles only if blaze powder is added
            if (isBlazePowderConsumed() && isPowered()) {
                for (int bubbleIndex = 0; bubbleIndex < bubbleData.size(); bubbleIndex++) {
                    List<Pixel> bubble = bubbleData.get(bubbleIndex);

                    for (Pixel pixel : bubble) {
                        int r = (pixel.color >> 16) & 255;
                        int g = (pixel.color >> 8) & 255;
                        int b = pixel.color & 255;
                        drawPixel(guiGraphics, offsetX + pixel.x, offsetY + pixel.y, r, g, b);
                    }
                }
            }
        }
    }



    private void drawPixel(GuiGraphics guiGraphics, int x, int y, int r, int g, int b) {

        RenderSystem.setShaderColor(r / 255.0F, g / 255.0F, b / 255.0F, 1.0F);
        guiGraphics.fill(x, y, x + 1, y + 1, (r << 16) | (g << 8) | b | 0xFF000000);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); // Reset the shader color to default white
    }

    private boolean isBlazePowderConsumed() {
        return this.menu.getBlazePowderConsumed();
    }

    private boolean isPowered() {
        return this.menu.isPowered();
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