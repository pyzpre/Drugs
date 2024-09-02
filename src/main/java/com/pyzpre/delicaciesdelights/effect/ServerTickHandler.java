package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ServerTickHandler {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<UUID, Boolean> jumpKeyPressedMap = new HashMap<>();

    public static void setJumpKeyPressed(Player player, boolean pressed) {
        jumpKeyPressedMap.put(player.getUUID(), pressed);
    }

    public static boolean isJumpKeyPressed(Player player) {
        return jumpKeyPressedMap.getOrDefault(player.getUUID(), false);
    }
    @SubscribeEvent
    public static void onServerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.player instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer) event.player;

            if (player.isAlive()) {
                // Log the player's current speed

                if (shouldStartFallFlying(player)) {
                    player.startFallFlying();
                } else if (shouldStopFallFlying(player)) {
                    player.stopFallFlying();
                }

                // Reset jump key state after processing
                jumpKeyPressedMap.put(player.getUUID(), false);
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
    public static boolean shouldStartFallFlying(Player player) {
        return player.hasEffect(EffectRegistry.ECHOVEIL.get())
                && !player.onGround()
                && !player.isFallFlying();
    }


    public static boolean shouldStopFallFlying(Player player) {
        if (player.isFallFlying()) {
            player.checkSlowFallDistance();
            Vec3 vec3 = player.getDeltaMovement();
            Vec3 vec31 = player.getLookAngle();
            float f = player.getXRot() * ((float) Math.PI / 180F);
            double d1 = Math.sqrt(vec31.x * vec31.x + vec31.z * vec31.z);
            double d3 = vec3.horizontalDistance();
            double d4 = vec31.length();
            double d5 = Math.cos((double) f);
            d5 = d5 * d5 * Math.min(1.0D, d4 / 0.4D);
            vec3 = player.getDeltaMovement().add(0.0D, player.getAttribute(net.minecraftforge.common.ForgeMod.ENTITY_GRAVITY.get()).getValue() * (-1.0D + d5 * 0.75D), 0.0D);

            if (vec3.y < 0.0D && d1 > 0.0D) {
                double d6 = vec3.y * -0.1D * d5;
                vec3 = vec3.add(vec31.x * d6 / d1, d6, vec31.z * d6 / d1);
            }

            if (f < 0.0F && d1 > 0.0D) {
                double d10 = d3 * (double) (-Mth.sin(f)) * 0.04D;
                vec3 = vec3.add(-vec31.x * d10 / d1, d10 * 3.2D, -vec31.z * d10 / d1);
            }

            if (d1 > 0.0D) {
                vec3 = vec3.add((vec31.x / d1 * d3 - vec3.x) * 0.1D, 0.0D, (vec31.z / d1 * d3 - vec3.z) * 0.1D);
            }

            player.setDeltaMovement(vec3.multiply(0.99D, 0.98D, 0.99D));
            player.move(MoverType.SELF, player.getDeltaMovement());

            // Stop flying if the player touches the ground
            if (player.onGround()) {
                player.stopFallFlying();
                return true;
            }
        }
        return !player.hasEffect(EffectRegistry.ECHOVEIL.get());
    }
}
