package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayRenderer;
import net.minecraftforge.network.NetworkEvent;
import java.util.function.Supplier;

public class ClientOverlaySyncPacketHandler {
    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            OverlayRenderer.syncOverlays(packet.getOverlays());
        });
        ctx.get().setPacketHandled(true);
    }
}
