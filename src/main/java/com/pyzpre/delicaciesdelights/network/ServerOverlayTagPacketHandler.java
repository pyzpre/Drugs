package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class ServerOverlayTagPacketHandler {
    private static final Logger LOGGER = LogManager.getLogger();

    public static void handle(OverlayTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null) {
                // Validate and update overlay tag
                OverlayManager.updateOverlayTag(serverPlayer, packet.tag, packet.add, true);

                // Send packet back to client
                NetworkSetup.getChannel().sendTo(
                        new OverlayTagPacket(packet.tag, packet.add),
                        serverPlayer.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            } else {
                LOGGER.warn("Server player was null while handling packet: {}", packet);
            }
        });
        context.setPacketHandled(true);
    }
}
