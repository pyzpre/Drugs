package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import com.pyzpre.delicaciesdelights.events.OverlayMetadata;
import com.pyzpre.delicaciesdelights.events.OverlayRenderer;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ClientOverlaySyncPacketHandler {

    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.getOverlays().isEmpty()) {
                OverlayRenderer.startFadingOut();
            } else {

                // Convert ResourceLocation to OverlayMetadata
                List<OverlayMetadata> overlays = OverlayManager.getOverlaysByLocations(packet.getOverlays());

                OverlayRenderer.addOverlaysToRender(overlays);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
