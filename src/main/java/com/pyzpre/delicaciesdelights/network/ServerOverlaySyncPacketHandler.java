package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class ServerOverlaySyncPacketHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerOverlaySyncPacketHandler.class);

    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            OverlayManager.syncOverlays(packet.getOverlays());
        });
        ctx.get().setPacketHandled(true);
    }
}
