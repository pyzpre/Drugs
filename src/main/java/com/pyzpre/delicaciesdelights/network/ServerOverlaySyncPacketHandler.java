package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerOverlaySyncPacketHandler {
    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            OverlayManager.syncOverlays(packet.getOverlays());
        });
        ctx.get().setPacketHandled(true);
    }
}
