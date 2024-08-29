package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

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
        Minecraft mc = Minecraft.getInstance();
        return player.hasEffect(EffectRegistry.ECHOVEIL.get())
                && !player.onGround()
                && !player.isFallFlying()
                && mc.options.keyJump.isDown(); // Only start flying when the jump key is pressed
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

            if (player.horizontalCollision && !player.level().isClientSide) {
                double d11 = player.getDeltaMovement().horizontalDistance();
                double d7 = d3 - d11;
            }

            // Stop flying if the player touches the ground
            if (player.onGround()) {
                player.stopFallFlying();
                return true;
            }
        }
        return !player.hasEffect(EffectRegistry.ECHOVEIL.get());
    }

}
