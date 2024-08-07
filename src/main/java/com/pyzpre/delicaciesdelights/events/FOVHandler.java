//package com.pyzpre.delicaciesdelights.events;
//
//import com.pyzpre.delicaciesdelights.DelicaciesDelights;
//import net.minecraft.client.Minecraft;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.entity.player.Player;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.client.event.ComputeFovModifierEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
//public class FOVHandler {
//
//    private static List<ResourceLocation> activeOverlays = new ArrayList<>();
//
//    public static void setActiveOverlays(List<ResourceLocation> overlays) {
//        activeOverlays = overlays;
//    }
//
//    public static void clearActiveOverlays() {
//        activeOverlays.clear();
//    }
//
//    @SubscribeEvent
//    public static void onComputeFovModifier(ComputeFovModifierEvent event) {
//        System.out.println("ComputeFovModifierEvent triggered.");
//
//        Player player = event.getPlayer();
//        float newFovModifier = event.getNewFovModifier();
//
//        // Apply FOV changes based on the active overlays
//        for (ResourceLocation overlayLocation : activeOverlays) {
//            List<OverlayMetadata> overlays = OverlayManager.getOverlaysByLocations(List.of(overlayLocation));
//            for (OverlayMetadata overlay : overlays) {
//                if (overlay.getFovChange() != 0.0f) {
//                    // Apply FOV change and log the modification
//                    newFovModifier += overlay.getFovChange();
//                    System.out.println("Applying FOV Change: " + overlay.getFovChange() + " New FOV Modifier: " + newFovModifier);
//                }
//            }
//        }
//
//        // Log the final FOV modifier value
//        System.out.println("Final FOV Modifier: " + newFovModifier);
//
//        event.setNewFovModifier(newFovModifier);
//    }
//}
