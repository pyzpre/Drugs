package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class ClientOverlayTagPacketHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handle(OverlayTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer != null) {
                // Handle the network update only if updates are not suppressed
                if (!OverlayManager.suppressClientUpdate) {
                    OverlayManager.handleNetworkUpdate(clientPlayer, packet.tag, packet.add);
                    LOGGER.info("Packet handled on client: {}", packet);
                } else {
                    LOGGER.warn("Client updates are suppressed while handling packet: {}", packet);
                }
            } else {
                LOGGER.warn("Client player was null while handling packet: {}", packet);
            }
        });
        context.setPacketHandled(true);
    }
}
