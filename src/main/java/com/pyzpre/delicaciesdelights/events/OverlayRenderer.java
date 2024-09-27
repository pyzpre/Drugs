package com.pyzpre.delicaciesdelights.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.OverlaySyncPacket;
import com.pyzpre.delicaciesdelights.network.RequestOverlayResourcesPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ComputeFovModifierEvent;
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
    private static final Map<String, Float> currentAlphas = new HashMap<>();
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
                String overlayTag = getOverlayTag(Minecraft.getInstance().player);  // Get the tag instead of location
                currentAlphas.putIfAbsent(overlayTag, 0.0f);
                shouldRenderOverlays = true;
                isFadingOut = false;
                currentFrame = 0;
                startTime = System.currentTimeMillis();
                adjustFovForCurrentOverlay(Minecraft.getInstance().player);
            }
        }
    }

    public static synchronized void startFadingOut() {
        isFadingOut = true;
        shouldRenderOverlays = false;
        resetFovAdjustment();
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        synchronized (OverlayRenderer.class) {
            if (currentOverlay != null && (shouldRenderOverlays || currentAlphas.values().stream().anyMatch(alpha -> alpha > 0.0f))) {

                if (shouldRenderOverlays) {
                    handleEffects(player);
                }

                if (isFadingOut) {
                    fadeOutEffects(player);
                }

                if (player != null && mc.level != null) {
                    if (currentOverlay == null) {
                        return;
                    }

                    String overlayTag = getOverlayTag(player);
                    Float alpha = currentAlphas.get(overlayTag);

                    // Safeguard against null alpha value
                    if (alpha == null) {
                        alpha = 0.0f;  // Fallback to a default alpha value
                        currentAlphas.put(overlayTag, alpha);
                    }

                    renderOverlay(event.getGuiGraphics(), alpha);

                    if (isFadingOut && alpha == 0.0f) {
                        clearCurrentOverlay();
                    }
                }
            }
        }
    }


    private static synchronized void handleEffects(Player player) {
        long currentTime = System.currentTimeMillis();
        float elapsedTime = (currentTime - startTime) / 1000.0f;

        if (currentOverlay != null) {
            // Retrieve the overlay tag for the player
            String overlayTag = getOverlayTag(player);

            // Initialize alpha value for the overlay tag if it's not already present
            Float alpha = currentAlphas.get(overlayTag);
            if (alpha == null) {
                // If the tag doesn't require a debuff, initialize the alpha value
                if (!currentOverlay.requiresDebuffTag()) {
                    alpha = 0.0f;
                    currentAlphas.put(overlayTag, alpha);
                } else {
                    startFadingOut();  // Start fading out if debuff tag is required but not found
                    return;
                }
            }

            // Handle the pulsate effect
            if (currentOverlay.isPulsate()) {
                if (!resetElapsedTime) {
                    startTime = System.currentTimeMillis();  // Reset start time
                    elapsedTime = 0;  // Reset elapsed time
                    resetElapsedTime = true;  // Ensure this block only executes once
                }
                // Calculate new alpha value based on a sinusoidal pulsate effect
                alpha = 0.05f * (1 - (float) Math.cos((elapsedTime / currentOverlay.getPulsateDuration()) * 2 * Math.PI));
            } else {
                // Increment alpha gradually until it reaches a threshold of 0.1f
                if (alpha < 0.1f) {
                    alpha += currentOverlay.getAlphaIncrement();  // Increment alpha
                    if (alpha > 0.1f) {
                        alpha = 0.1f;  // Cap alpha at 0.1f
                    }
                }
                resetElapsedTime = false;  // Ensure this block can be revisited
            }

            // Update the alpha value for the current overlay
            currentAlphas.put(overlayTag, alpha);
            updateCurrentFrame(elapsedTime);  // Update the current frame for rendering
        }
    }

    private static synchronized void fadeOutEffects(Player player) {
        if (currentOverlay != null) {
            String tag = getOverlayTag(player);
            Float alpha = currentAlphas.get(tag);
            if (alpha != null && alpha > 0.0f) {
                alpha -= currentOverlay.getAlphaIncrement();
                if (alpha < 0.0f) {
                    alpha = 0.0f;
                }
                currentAlphas.put(tag, alpha);
            }

            if (alpha != null && alpha == 0.0f) {

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
    }

    private static boolean hasOverlayTag(Player player, String tag) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.contains(tag);
    }

    private static String getOverlayTag(Player player) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);

        for (Map.Entry<String, List<OverlayMetadata>> entry : OverlayManager.getOverlayMap().entrySet()) {
            String tag = entry.getKey();
            if (persistentData.contains(tag)) {
                // If the player's data contains this tag, we assume it is the correct one
                return tag;
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
            Player player = Minecraft.getInstance().player;
            if (player != null) {  // Ensure the player is not null
                if (packet.getOverlays().isEmpty()) {
                    OverlayRenderer.startFadingOut();  // Stop rendering the overlay if no overlays are active
                } else {
                    // Attempt to find the corresponding overlay tag for the resources
                    String tag = getOverlayTag(player);
                    if (tag != null) {
                        List<OverlayMetadata> overlays = OverlayManager.getOverlays(tag);
                        if (!overlays.isEmpty()) {
                            OverlayRenderer.addOverlaysToRender(overlays);
                        }
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public static synchronized void syncOverlays(List<ResourceLocation> overlays) {
        if (overlays.isEmpty()) {
            startFadingOut();
        } else {
            // Attempt to find the corresponding overlay tag for the resources
            String tag = getOverlayTag(Minecraft.getInstance().player);
            if (tag != null) {
                List<OverlayMetadata> metadataList = OverlayManager.getOverlays(tag);
                if (!metadataList.isEmpty()) {
                    addOverlaysToRender(metadataList);
                }
            }
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

    private static void adjustFovForCurrentOverlay(Player player) {
        String overlayTag = getOverlayTag(player);
        if (overlayTag != null && OverlayManager.getOverlayMap().containsKey(overlayTag)) {
            OverlayMetadata metadata = OverlayManager.getOverlayMap().get(overlayTag).get(0);
            fovAdjustment = metadata.getFovChange(); // Set the FOV change based on tag
            // Log the overlay and FOV change
        } else {
            fovAdjustment = 0.0f; // Reset if no overlay is active
            // Log that there is no active overlay
        }
    }

    private static void resetFovAdjustment() {
        fovAdjustment = 0.0f;
        // Log the reset of FOV adjustment
    }

    @SubscribeEvent
    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
        float baseFov = event.getNewFovModifier();
        float adjustedFov = baseFov * (1.0f + fovAdjustment);

        // Ensure adjustedFov stays within reasonable bounds
        adjustedFov = Math.max(0.5f, Math.min(adjustedFov, 26.6f)); // You can adjust these bounds as needed

        event.setNewFovModifier(adjustedFov);
    }
}
