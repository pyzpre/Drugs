//package com.pyzpre.delicaciesdelights.events;
//
//import com.mojang.blaze3d.systems.RenderSystem;
//import com.pyzpre.delicaciesdelights.DelicaciesDelights;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.GuiGraphics;
//import net.minecraft.nbt.CompoundTag;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.effect.MobEffectInstance;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ComputeFovModifierEvent;
//import net.minecraftforge.client.event.RenderGuiOverlayEvent;
//import net.minecraftforge.event.entity.living.LivingEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.*;
//
//import static com.pyzpre.delicaciesdelights.events.OverlayManager.*;
//
//@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//public class OverlayEvent {
//    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayEvent.class);
//    private static final Map<ResourceLocation, Float> currentAlphas = new HashMap<>(); // Current alpha values for fading
//    private static final Set<OverlayMetadata> overlaysToRender = new HashSet<>(); // Use Set to avoid duplicates
//    private static boolean shouldRenderOverlays = false;
//    private static boolean isFadingOut = false; // Track if the overlay should fade out
//    private static long startTime = System.currentTimeMillis(); // Track the start time for pulsating effect
//    private static boolean resetElapsedTime = false;
//
//    @SubscribeEvent
//    public static void onPlayerTick(LivingEvent.LivingTickEvent event) {
//        if (event.getEntity() instanceof Player player) {
//            boolean anyEffectActive = false;
//
//            // Iterate over all active effects
//            for (MobEffectInstance effect : player.getActiveEffects()) {
//                anyEffectActive = true;
//
//                // Check if the player has a debuff tag for any of the active effects
//                for (String key : DebuffManager.DEBUFF_MAP.keySet()) {
//                    if (DebuffManager.hasDebuffTag(player, key)) {
//                        CompoundTag persistentTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
//                        for (String tagKey : persistentTag.getAllKeys()) {
//                            if (OverlayManager.hasOverlayTag(player, tagKey)) {
//                                List<OverlayMetadata> overlays = OverlayManager.getOverlays(tagKey);
//                                if (overlays != null) {
//                                    overlaysToRender.addAll(overlays);
//                                    for (OverlayMetadata overlay : overlays) {
//                                        currentAlphas.putIfAbsent(overlay.location, 0.0f); // Initialize alpha values
//                                    }
//                                }
//                                shouldRenderOverlays = true;
//                                isFadingOut = false;
//                            }
//                        }
//                    }
//                }
//            }
//
//            // If no effects are active, start fading out
//            if (!anyEffectActive && shouldRenderOverlays) {
//                isFadingOut = true;
//                shouldRenderOverlays = false;
//            }
//        }
//    }
//
//    @SubscribeEvent
//    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
//        Minecraft mc = Minecraft.getInstance();
//        Player player = mc.player;
//
//        if (shouldRenderOverlays || currentAlphas.values().stream().anyMatch(alpha -> alpha > 0.0f)) {
//            if (shouldRenderOverlays) {
//                handleEffects(); // Call handleEffects to handle both pulsating and non-pulsating effects
//            } else if (isFadingOut) {
//                fadeOutEffects(player);
//            }
//
//            if (player != null && mc.level != null) {
//                // Render overlays with fade in/out effect
//                for (OverlayMetadata overlay : overlaysToRender) {
//                    float alpha = currentAlphas.get(overlay.location);
//                    renderOverlay(event.getGuiGraphics(), alpha, overlay.location);
//                }
//
//                // Clear overlaysToRender when fully faded out and reset flags
//                if (isFadingOut && currentAlphas.values().stream().allMatch(alpha -> alpha == 0.0f)) {
//                    overlaysToRender.clear();
//                    currentAlphas.clear();
//                    isFadingOut = false; // Reset fading out state
//                    resetElapsedTime = false; // Reset the elapsed time flag
//                }
//            }
//        }
//    }
//
//    private static void handleEffects() {
//        long currentTime = System.currentTimeMillis();
//        float elapsedTime = (currentTime - startTime) / 1000.0f; // Convert to seconds
//
//        for (OverlayMetadata overlay : overlaysToRender) {
//            float alpha;
//
//            if (overlay.pulsate) {
//                // Reset elapsedTime to 0 only once when the overlay starts pulsating
//                if (!resetElapsedTime) {
//                    startTime = System.currentTimeMillis();
//                    elapsedTime = 0;
//                    resetElapsedTime = true;
//                }
//                alpha = 0.05f * (1 - (float) Math.cos((elapsedTime / overlay.pulsateDuration) * 2 * Math.PI));
//            } else {
//                alpha = currentAlphas.getOrDefault(overlay.location, 0.0f);
//                if (alpha < 0.1f) {
//                    alpha += overlay.alphaIncrement;
//                    if (alpha > 0.1f) {
//                        alpha = 0.1f;
//                    }
//                }
//                resetElapsedTime = false;
//            }
//
//            currentAlphas.put(overlay.location, alpha);
//        }
//    }
//
//    private static void fadeOutEffects(Player player) {
//        for (OverlayMetadata overlay : overlaysToRender) {
//            float alpha = currentAlphas.get(overlay.location);
//            if (alpha > 0.0f) {
//                alpha -= overlay.alphaIncrement;
//                if (alpha < 0.0f) {
//                    alpha = 0.0f;
//                }
//                currentAlphas.put(overlay.location, alpha);
//            }
//        }
//
//        if (currentAlphas.values().stream().allMatch(alpha -> alpha == 0.0f)) {
//            // Remove all overlay tags associated with the current overlays
//            for (OverlayMetadata overlay : overlaysToRender) {
//                String tag = getOverlayTag(overlay);
//                if (tag != null) {
//                    OverlayManager.removeOverlayTag(player, tag);
//                }
//            }
//        }
//    }
//
//    private static String getOverlayTag(OverlayMetadata overlay) {
//        for (Map.Entry<String, List<OverlayMetadata>> entry : OverlayManager.getOverlayMap().entrySet()) {
//            if (entry.getValue().contains(overlay)) {
//                return entry.getKey();
//            }
//        }
//        return null;
//    }
//
//    public static void renderOverlay(GuiGraphics guiGraphics, float alpha, ResourceLocation overlay) {
//        Minecraft mc = Minecraft.getInstance();
//        int screenWidth = mc.getWindow().getGuiScaledWidth();
//        int screenHeight = mc.getWindow().getGuiScaledHeight();
//
//        // Save current OpenGL state
//        RenderSystem.disableDepthTest();
//        RenderSystem.depthMask(false);
//        RenderSystem.enableBlend();
//        RenderSystem.defaultBlendFunc();
//        RenderSystem.setShaderTexture(0, overlay);
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);
//
//        // Render the overlay texture
//        guiGraphics.blit(overlay, 0, 0, 0, 0, screenWidth, screenHeight, screenWidth, screenHeight);
//
//        // Restore previous OpenGL state
//        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
//        RenderSystem.depthMask(true);
//        RenderSystem.enableDepthTest();
//        RenderSystem.disableBlend();
//    }
//
//    @SubscribeEvent
//    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
//        Player player = event.getPlayer();
//        float newFovModifier = event.getNewFovModifier();
//
//        // Adjust the FOV based on all active effects
//        for (MobEffectInstance effect : player.getActiveEffects()) {
//            String effectName = effect.getEffect().getDescriptionId(); // Use description ID as key
//            List<OverlayMetadata> overlays = OverlayManager.getOverlays(effectName);
//            if (overlays != null) {
//                for (OverlayMetadata overlay : overlays) {
//                    if (overlay.fovChange != 0.0f) {
//                        newFovModifier += (overlay.fovChange * (effect.getAmplifier() + 1));
//                    }
//                }
//            }
//        }
//
//        event.setNewFovModifier(newFovModifier);
//    }
//}
