package com.pyzpre.delicaciesdelights.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.OverlaySyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Bus.FORGE, value = Dist.CLIENT)
public class OverlayRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayRenderer.class);
    private static final Map<ResourceLocation, Float> currentAlphas = new HashMap<>();
    private static OverlayManager.OverlayMetadata currentOverlay = null;
    private static boolean shouldRenderOverlays = false;
    private static boolean isFadingOut = false;
    private static long startTime = System.currentTimeMillis();
    private static boolean resetElapsedTime = false;

    public static void addOverlaysToRender(List<OverlayManager.OverlayMetadata> overlays) {
        if (!overlays.isEmpty()) {
            OverlayManager.OverlayMetadata newOverlay = overlays.get(0); // Limit to one overlay at a time
            if (currentOverlay == null || !currentOverlay.location.equals(newOverlay.location)) {
                currentOverlay = newOverlay;
                currentAlphas.putIfAbsent(currentOverlay.location, 0.0f);
                shouldRenderOverlays = true;
                isFadingOut = false;
            }
        }
    }

    public static void startFadingOut() {
        isFadingOut = true;
        shouldRenderOverlays = false;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (currentOverlay != null && (shouldRenderOverlays || currentAlphas.values().stream().anyMatch(alpha -> alpha > 0.0f))) {

            if (shouldRenderOverlays) {
                handleEffects();
            }

            if (isFadingOut) {
                fadeOutEffects(player);
            }

            if (player != null && mc.level != null) {
                float alpha = currentAlphas.get(currentOverlay.location);
                renderOverlay(event.getGuiGraphics(), alpha, currentOverlay.location);

                if (isFadingOut && alpha == 0.0f) {
                    currentOverlay = null;
                    currentAlphas.clear();
                    isFadingOut = false;
                    resetElapsedTime = false;
                }
            }
        }
    }

    private static void handleEffects() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = (currentTime - startTime) / 1000.0f;

        if (currentOverlay != null) {
            float alpha;

            if (currentOverlay.pulsate) {
                if (!resetElapsedTime) {
                    startTime = System.currentTimeMillis();
                    elapsedTime = 0;
                    resetElapsedTime = true;
                }
                alpha = 0.05f * (1 - (float) Math.cos((elapsedTime / currentOverlay.pulsateDuration) * 2 * Math.PI));
            } else {
                alpha = currentAlphas.getOrDefault(currentOverlay.location, 0.0f);
                if (alpha < 0.1f) {
                    alpha += currentOverlay.alphaIncrement;
                    if (alpha > 0.1f) {
                        alpha = 0.1f;
                    }
                }
                resetElapsedTime = false;
            }

            currentAlphas.put(currentOverlay.location, alpha);
        }
    }

    private static void fadeOutEffects(Player player) {
        if (currentOverlay != null) {
            float alpha = currentAlphas.get(currentOverlay.location);
            if (alpha > 0.0f) {
                alpha -= currentOverlay.alphaIncrement;
                if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                currentAlphas.put(currentOverlay.location, alpha);
            }

            if (alpha == 0.0f) {
                String tag = getOverlayTag(currentOverlay);
                if (tag != null) {
                    OverlayManager.updateOverlayTag(player, tag, false, false);
                    LOGGER.info("Removing overlay tag in OverlayRenderer: {}", tag);
                }
                currentOverlay = null;  // Clear the current overlay when fully faded out
            }
        }
    }

    private static String getOverlayTag(OverlayManager.OverlayMetadata overlay) {
        for (Map.Entry<String, List<OverlayManager.OverlayMetadata>> entry : OverlayManager.getOverlayMap().entrySet()) {
            if (entry.getValue().contains(overlay)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void renderOverlay(GuiGraphics guiGraphics, float alpha, ResourceLocation overlay) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, overlay);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        guiGraphics.blit(overlay, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    public static void handleSyncPacket(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.getOverlays().isEmpty()) {
                startFadingOut();
            } else {
                addOverlaysToRender(OverlayManager.getOverlaysByLocations(packet.getOverlays()));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
