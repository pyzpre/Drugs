package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.DebuffManager;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ClientDebuffTagPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientDebuffTagPacketHandler.class);

    public static void handle(DebuffTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            Player clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer != null) {
                DebuffManager.handleNetworkUpdate(clientPlayer, packet.tag, packet.add);
            } else {
                LOGGER.warn("Client player was null while handling debuff tag packet: {}", packet);
            }
        });
        context.setPacketHandled(true);
    }
}
