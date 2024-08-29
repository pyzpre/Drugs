package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.pyzpre.delicaciesdelights.effect.ClientTickHandler.shouldStopFallFlying;

@Mod.EventBusSubscriber
public class ServerTickHandler {

    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (player.isAlive() && shouldStopFallFlying(player)) {
                    LOGGER.info("Stopping fall flying for player: {}", player.getName().getString());
                    player.stopFallFlying();
            }
        }
    }

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Log if fall damage is being canceled
            if (player.hasEffect(EffectRegistry.ECHOVEIL.get())) {
                event.setCanceled(true);
            }
        }
    }
}
