package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayRenderer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OverlaySyncPacketHandler {
    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        OverlayRenderer.handleSyncPacket(packet, ctx);
    }
}
