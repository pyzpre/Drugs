package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(Dist.CLIENT)
public class ClientTickHandler {
    private static final double MAX_SPEED = 1.0; // 10 blocks per second
    private static final double BOOST_FORCE = 0.1; // Force applied per tick

    @SubscribeEvent
    public static void onClientTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            Minecraft mc = Minecraft.getInstance();

            // Ensure this is the client-side player
            if (mc.player == player && player.isAlive() && player instanceof LocalPlayer) {
                // Apply boost logic only if needed
                applyBoostIfNeeded((LocalPlayer) player);
            }
        }
    }

    private static void applyBoostIfNeeded(LocalPlayer player) {
        // Check if the player has the Echoveil effect and is fall flying
        if (player.hasEffect(EffectRegistry.ECHOVEIL.get()) && player.isFallFlying()) {
            Vec3 lookVec = player.getLookAngle();
            Vec3 movementVec = new Vec3(lookVec.x, 0, lookVec.z).normalize();

            // Check if the forward movement key is pressed
            if (player.input.forwardImpulse > 0) {
                // Calculate current horizontal speed
                double currentSpeed = Math.sqrt(player.getDeltaMovement().x * player.getDeltaMovement().x +
                        player.getDeltaMovement().z * player.getDeltaMovement().z);

                // Only apply boost if the current speed is below the maximum allowed speed
                if (currentSpeed < MAX_SPEED) {
                    // Apply a force in the direction the player is looking
                    player.push(movementVec.x * BOOST_FORCE, 0, movementVec.z * BOOST_FORCE);
                }
            }
        }
    }
}
