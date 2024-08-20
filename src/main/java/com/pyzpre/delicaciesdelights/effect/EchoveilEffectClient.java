package com.pyzpre.delicaciesdelights.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)  // Ensure this class is only used on the client side
public class EchoveilEffectClient {

    public static void startFallFlying(Player player) {
        if (Minecraft.getInstance().player == player) {
            player.startFallFlying();
        }
    }

    public static void stopFallFlying(Player player) {
        if (Minecraft.getInstance().player == player) {
            player.stopFallFlying();
        }
    }
}
