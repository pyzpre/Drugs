package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.OverlaySyncPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientPlayerTickHandler {

    // Map to track the effect state for each player
    private static final ConcurrentMap<Player, Boolean> playerEffectActiveMap = new ConcurrentHashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient()) {
            handlePlayerTickClient(event.player);
        }
    }

    private static void handlePlayerTickClient(Player player) {
        // Ensure that the logic only runs for the current client player
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null || !clientPlayer.equals(player)) {
            return; // Exit early if the player is not the client player
        }

        ConcurrentMap<ResourceLocation, Boolean> activeOverlays = new ConcurrentHashMap<>();
        boolean anyEffectActive = false;


        // Iterate over all active effects for this player
        for (MobEffectInstance effect : player.getActiveEffects()) {
            anyEffectActive = true;
            String effectDescriptionId = effect.getEffect().getDescriptionId();


            // Only process overlays related to the player's specific active effects
            for (String key : OverlayManager.getOverlayMap().keySet()) {
                if (OverlayManager.hasOverlayTag(player, key)) {
                    List<OverlayMetadata> overlays = OverlayManager.getOverlays(key);
                    if (overlays != null) {
                        for (OverlayMetadata overlay : overlays) {
                            activeOverlays.putIfAbsent(overlay.getLocation(), Boolean.TRUE);
                        }
                    }
                }
            }
        }

        // Update the map with the current player's effect state
        playerEffectActiveMap.put(player, anyEffectActive);

        if (anyEffectActive) {
            OverlayRenderer.addOverlaysToRender(OverlayManager.getOverlaysByLocations(new ArrayList<>(activeOverlays.keySet())));
        } else {
            OverlayRenderer.startFadingOut();
        }
    }
}