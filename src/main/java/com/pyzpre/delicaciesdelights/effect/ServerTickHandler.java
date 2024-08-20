package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerTickHandler {

    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (player.isAlive()) {
                // Handle server-specific logic here, such as starting/stopping Elytra fall flight
                if (shouldStartFallFlying(player)) {
                    player.startFallFlying();
                } else if (shouldStopFallFlying(player)) {
                    player.stopFallFlying();
                }
            }
        }
    }

    private static boolean shouldStartFallFlying(Player player) {
        // Check if the player has the EchoveilEffect before starting Elytra flight
        return player.hasEffect(EffectRegistry.ECHOVEIL.get()) && !player.isFallFlying();
    }

    private static boolean shouldStopFallFlying(Player player) {
        // Stop Elytra flight if the player no longer has the EchoveilEffect
        return !player.hasEffect(EffectRegistry.ECHOVEIL.get()) && player.isFallFlying();
    }
}
