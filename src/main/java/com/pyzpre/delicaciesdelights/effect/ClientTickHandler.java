package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientTickHandler {

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Minecraft mc = Minecraft.getInstance();

            // Ensure this is the client-side player
            if (mc.player == player && player.isAlive()) {
                // Handle client-specific logic here, such as starting Elytra fall flight
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
