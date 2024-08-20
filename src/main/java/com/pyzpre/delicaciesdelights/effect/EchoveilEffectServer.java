package com.pyzpre.delicaciesdelights.effect;

import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.ServerboundPlayerFallFlyingPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EchoveilEffectServer {

    private static final Logger LOGGER = LogManager.getLogger();

    public static void startFallFlying(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Log the player's current state before attempting to start fallFlying
            LOGGER.info("Attempting to start fallFlying for player: {}", player.getName().getString());

            // Send packet to server to ensure the player is considered fallFlying
            NetworkSetup.getChannel().send(PacketDistributor.SERVER.noArg(), new ServerboundPlayerFallFlyingPacket(true));

            // Force the server to recognize the state immediately
            serverPlayer.startFallFlying();

            // Log the result
            LOGGER.info("Player {} is now fallFlying.", player.getName().getString());
        }
    }

    public static void stopFallFlying(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            // Log the player's current state before attempting to stop fallFlying
            LOGGER.info("Attempting to stop fallFlying for player: {}", player.getName().getString());

            // Send packet to server to stop fallFlying
            NetworkSetup.getChannel().send(PacketDistributor.SERVER.noArg(), new ServerboundPlayerFallFlyingPacket(false));

            // Stop fallFlying on the server immediately
            serverPlayer.stopFallFlying();

            // Log the result
            LOGGER.info("Player {} has stopped fallFlying.", player.getName().getString());
        }
    }
}
