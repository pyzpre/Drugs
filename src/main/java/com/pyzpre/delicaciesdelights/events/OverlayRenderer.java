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
    private static float fovAdjustment = 0.0f;

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
                adjustFovForCurrentOverlay();
                LOGGER.info("Added new overlay to render: {}", currentOverlay.getLocation());
            }
        }
    }

    public static synchronized void startFadingOut() {
        isFadingOut = true;
        shouldRenderOverlays = false;
        resetFovAdjustment();
        LOGGER.info("Starting to fade out overlay: {}", currentOverlay != null ? currentOverlay.getLocation() : "None");
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
                        return;
                    }

                    Float alpha = currentAlphas.get(currentOverlay.getLocation());

                    // Handle case where the location is not found
                    if (alpha == null) {
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
        LOGGER.info("Cleared current overlay.");
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

        LOGGER.info("Rendered overlay frame: {}", frameLocation);
    }

    private static void updateCurrentFrame(float elapsedTime) {
        int frameDurationMs = currentOverlay.getFrameDuration();
        int frameCount = currentOverlay.getFrames().size();

        currentFrame = (int) ((elapsedTime * 1000) / frameDurationMs) % frameCount;
    }

    public static void handleSyncPacket(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().player;  // Ensure this is specific to the current player
            if (packet.getOverlays().isEmpty()) {
                OverlayRenderer.startFadingOut();  // Fade out only for this player
            } else {
                OverlayRenderer.addOverlaysToRender(OverlayManager.getOverlaysByLocations(packet.getOverlays()));
            }
            LOGGER.info("Client received overlay sync packet with {} overlays for player {}.", packet.getOverlays().size(), player.getName().getString());
        });
        ctx.get().setPacketHandled(true);
    }


    public static synchronized void syncOverlays(List<ResourceLocation> overlays) {
        if (overlays.isEmpty()) {
            startFadingOut();
        } else {
            addOverlaysToRender(OverlayManager.getOverlaysByLocations(overlays));
        }
        LOGGER.info("Syncing {} overlays.", overlays.size());
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
                LOGGER.info("Requested overlay resources: {}", resourceLocations.size());
            }
        });
        ctx.get().setPacketHandled(true);
    }

    private static void adjustFovForCurrentOverlay() {
        if (currentOverlay != null) {
            fovAdjustment = currentOverlay.getFovChange();
            LOGGER.info("Adjusting FOV for current overlay: {} by {}", currentOverlay.getLocation(), fovAdjustment);
        }
    }

    private static void resetFovAdjustment() {
        fovAdjustment = 0.0f;
        LOGGER.info("Resetting FOV adjustment.");
    }
}
