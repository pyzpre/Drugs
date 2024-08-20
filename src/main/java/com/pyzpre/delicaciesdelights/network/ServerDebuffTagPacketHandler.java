package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.DebuffManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ServerDebuffTagPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerDebuffTagPacketHandler.class);

    public static void handle(DebuffTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer serverPlayer = context.getSender();
            if (serverPlayer != null) {
                // Validate and update debuff tag
                DebuffManager.updateDebuffTag(serverPlayer, packet.tag, packet.add, true);

                // Send packet back to client
                NetworkSetup.getChannel().sendTo(
                        new DebuffTagPacket(packet.tag, packet.add),
                        serverPlayer.connection.connection,
                        NetworkDirection.PLAY_TO_CLIENT
                );
            } else {
                LOGGER.warn("Server player was null while handling debuff tag packet: {}", packet);
            }
        });
        context.setPacketHandled(true);
    }
}
