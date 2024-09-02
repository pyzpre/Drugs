package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerTickHandler {
    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (player.isAlive()) {
                if (shouldStartFallFlying(player)) {
                    player.startFallFlying();
                } else if (shouldStopFallFlying(player)) {
                    player.stopFallFlying();
                }

            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (player.hasEffect(EffectRegistry.ECHOVEIL.get())) {
                event.setCanceled(true);
            }
        }
    }
    public static boolean shouldStartFallFlying(Player player) {
        return player.hasEffect(EffectRegistry.ECHOVEIL.get())
                && !player.onGround()
                && !player.isFallFlying();
    }


    public static boolean shouldStopFallFlying(Player player) {

        return !player.hasEffect(EffectRegistry.ECHOVEIL.get());
    }
}