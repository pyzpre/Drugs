package com.pyzpre.delicaciesdelights.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.OverlaySyncPacket;
import com.pyzpre.delicaciesdelights.network.RequestOverlayResourcesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class OverlayRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayRenderer.class);
    private static final Map<ResourceLocation, Float> currentAlphas = new HashMap<>();
    private static OverlayMetadata currentOverlay = null;
    private static boolean shouldRenderOverlays = false;
    private static boolean isFadingOut = false;
    private static long startTime = System.currentTimeMillis();
    private static boolean resetElapsedTime = false;
    private static int currentFrame = 0;

    public static synchronized void addOverlaysToRender(List<OverlayMetadata> overlays) {
        if (!overlays.isEmpty()) {
            OverlayMetadata newOverlay = overlays.get(0); // Limit to one overlay at a time
            if (currentOverlay == null || !currentOverlay.getLocation().equals(newOverlay.getLocation())) {
                currentOverlay = newOverlay;
                currentAlphas.putIfAbsent(currentOverlay.getLocation(), 0.0f);
                shouldRenderOverlays = true;
                isFadingOut = false;
                currentFrame = 0;
                startTime = System.currentTimeMillis();
            }
        }
    }

    public static synchronized void startFadingOut() {
        isFadingOut = true;
        shouldRenderOverlays = false;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        synchronized (OverlayRenderer.class) {
            if (currentOverlay != null && (shouldRenderOverlays || currentAlphas.values().stream().anyMatch(alpha -> alpha > 0.0f))) {

                if (shouldRenderOverlays) {
                    handleEffects();
                }

                if (isFadingOut) {
                    fadeOutEffects(player);
                }

                if (player != null && mc.level != null) {
                    if (currentOverlay == null) {
                        LOGGER.warn("Current overlay is null. Skipping rendering.");
                        return;
                    }

                    Float alpha = currentAlphas.get(currentOverlay.getLocation());

                    // Handle case where the location is not found
                    if (alpha == null) {
                        LOGGER.warn("Alpha value for current overlay location {} not found.", currentOverlay.getLocation());
                        clearCurrentOverlay();
                        return;
                    }

                    renderOverlay(event.getGuiGraphics(), alpha);

                    if (isFadingOut && alpha == 0.0f) {
                        clearCurrentOverlay();
                    }
                }
            }
        }
    }

    private static synchronized void handleEffects() {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = (currentTime - startTime) / 1000.0f;

        if (currentOverlay != null) {
            float alpha;

            if (currentOverlay.isPulsate()) {
                if (!resetElapsedTime) {
                    startTime = System.currentTimeMillis();
                    elapsedTime = 0;
                    resetElapsedTime = true;
                }
                alpha = 0.05f * (1 - (float) Math.cos((elapsedTime / currentOverlay.getPulsateDuration()) * 2 * Math.PI));
            } else {
                alpha = currentAlphas.getOrDefault(currentOverlay.getLocation(), 0.0f);
                if (alpha < 0.1f) {
                    alpha += currentOverlay.getAlphaIncrement();
                    if (alpha > 0.1f) {
                        alpha = 0.1f;
                    }
                }
                resetElapsedTime = false;
            }

            currentAlphas.put(currentOverlay.getLocation(), alpha);
            updateCurrentFrame(elapsedTime);
        }
    }

    private static synchronized void fadeOutEffects(Player player) {
        if (currentOverlay != null) {
            Float alpha = currentAlphas.get(currentOverlay.getLocation());
            if (alpha != null && alpha > 0.0f) {
                alpha -= currentOverlay.getAlphaIncrement();
                if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                currentAlphas.put(currentOverlay.getLocation(), alpha);
            }

            if (alpha != null && alpha == 0.0f) {
                String tag = getOverlayTag(currentOverlay);
                if (tag != null) {
                    OverlayManager.updateOverlayTag(player, tag, false, false);
                    LOGGER.info("Removing overlay tag in OverlayRenderer: {}", tag);
                }
                clearCurrentOverlay();
            }
        }
    }

    private static synchronized void clearCurrentOverlay() {
        currentOverlay = null;
        currentAlphas.clear();
        isFadingOut = false;
        resetElapsedTime = false;
    }

    private static String getOverlayTag(OverlayMetadata overlay) {
        for (Map.Entry<String, List<OverlayMetadata>> entry : OverlayManager.getOverlayMap().entrySet()) {
            if (entry.getValue().contains(overlay)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static void renderOverlay(GuiGraphics guiGraphics, float alpha) {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        int screenHeight = mc.getWindow().getGuiScaledHeight();

        ResourceLocation frameLocation = currentOverlay.getFrames().get(currentFrame);

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, frameLocation);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        guiGraphics.blit(frameLocation, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static void updateCurrentFrame(float elapsedTime) {
        int frameDurationMs = currentOverlay.getFrameDuration();
        int frameCount = currentOverlay.getFrames().size();

        currentFrame = (int) ((elapsedTime * 1000) / frameDurationMs) % frameCount;
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

    public static synchronized void syncOverlays(List<ResourceLocation> overlays) {
        if (overlays.isEmpty()) {
            startFadingOut();
        } else {
            addOverlaysToRender(OverlayManager.getOverlaysByLocations(overlays));
        }
    }

    public static void handleRequestOverlayResourcesPacket(RequestOverlayResourcesPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            Player player = mc.player;
            if (player != null) {
                List<OverlayMetadata> metadataList = OverlayManager.getOverlaysByLocations(packet.getOverlays());
                List<ResourceLocation> resourceLocations = new ArrayList<>();
                for (OverlayMetadata metadata : metadataList) {
                    resourceLocations.addAll(metadata.getFrames());
                }
                NetworkSetup.getChannel().send(PacketDistributor.SERVER.noArg(), new RequestOverlayResourcesPacket(resourceLocations));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
