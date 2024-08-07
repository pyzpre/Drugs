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
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientOverlaySyncPacketHandler.class);

    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            LOGGER.info("Handling overlay sync packet on client with {} overlays.", packet.getOverlays().size());
            if (packet.getOverlays().isEmpty()) {
                LOGGER.info("Received empty overlay sync packet on client.");
                OverlayRenderer.startFadingOut();
            } else {
                LOGGER.info("Received overlay sync packet with {} overlays on client.", packet.getOverlays().size());

                // Convert ResourceLocation to OverlayMetadata
                List<OverlayMetadata> overlays = OverlayManager.getOverlaysByLocations(packet.getOverlays());

                OverlayRenderer.addOverlaysToRender(overlays);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
