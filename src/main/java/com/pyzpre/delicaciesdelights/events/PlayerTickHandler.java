package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.OverlaySyncPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = {Dist.CLIENT, Dist.DEDICATED_SERVER})
public class PlayerTickHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerTickHandler.class);

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer()) {
            handlePlayerTickServer((ServerPlayer) event.player);
        } else {
            handlePlayerTickClient(event.player);
        }
    }

    private static void handlePlayerTickServer(ServerPlayer player) {
        boolean anyEffectActive = false;
        Set<ResourceLocation> activeOverlays = new HashSet<>();

        // Iterate over all active effects
        for (MobEffectInstance effect : player.getActiveEffects()) {
            anyEffectActive = true;
            String effectDescriptionId = effect.getEffect().getDescriptionId();

            // Check if the player has a debuff tag for any of the active effects
            for (String key : OverlayManager.getOverlayMap().keySet()) {
                if (OverlayManager.hasOverlayTag(player, key)) {
                    List<OverlayMetadata> overlays = OverlayManager.getOverlays(key);
                    if (overlays != null) {
                        for (OverlayMetadata overlay : overlays) {
                            if (activeOverlays.add(overlay.getLocation())) {  // Add only if not already present
                                LOGGER.debug("Added overlay {} for effect {}", overlay.getLocation(), effectDescriptionId);
                            }
                        }
                    }
                }
            }
        }

        if (anyEffectActive) {
            LOGGER.debug("Sending overlay sync packet to client with overlays: {}", activeOverlays);
            NetworkSetup.getChannel().sendTo(new OverlaySyncPacket(new ArrayList<>(activeOverlays)), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        } else {
            LOGGER.debug("No active effects, sending empty overlay sync packet to client");
            NetworkSetup.getChannel().sendTo(new OverlaySyncPacket(new ArrayList<>()), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    private static void handlePlayerTickClient(Player player) {
        boolean anyEffectActive = false;
        Set<ResourceLocation> activeOverlays = new HashSet<>();

        // Iterate over all active effects
        for (MobEffectInstance effect : player.getActiveEffects()) {
            anyEffectActive = true;
            String effectDescriptionId = effect.getEffect().getDescriptionId();

            // Check if the player has a debuff tag for any of the active effects
            for (String key : OverlayManager.getOverlayMap().keySet()) {
                if (OverlayManager.hasOverlayTag(player, key)) {
                    List<OverlayMetadata> overlays = OverlayManager.getOverlays(key);
                    if (overlays != null) {
                        for (OverlayMetadata overlay : overlays) {
                            if (activeOverlays.add(overlay.getLocation())) {  // Add only if not already present
                                LOGGER.debug("Added overlay {} for effect {}", overlay.getLocation(), effectDescriptionId);
                            }
                        }
                    }
                }
            }
        }

        if (anyEffectActive) {
            LOGGER.debug("Adding overlays to render: {}", activeOverlays);
            OverlayRenderer.addOverlaysToRender(OverlayManager.getOverlaysByLocations(new ArrayList<>(activeOverlays)));
        } else {
            LOGGER.debug("No active effects, starting fade out");
            OverlayRenderer.startFadingOut();
        }
    }
}
