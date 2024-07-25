package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import com.pyzpre.delicaciesdelights.events.OverlayRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OverlaySyncPacket {
    private final List<ResourceLocation> overlays;

    public OverlaySyncPacket(List<ResourceLocation> overlays) {
        this.overlays = overlays;
    }

    public static void encode(OverlaySyncPacket packet, FriendlyByteBuf buf) {
        buf.writeInt(packet.overlays.size());
        for (ResourceLocation overlay : packet.overlays) {
            buf.writeResourceLocation(overlay);
        }
    }

    public static OverlaySyncPacket decode(FriendlyByteBuf buf) {
        int size = buf.readInt();
        List<ResourceLocation> overlays = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            overlays.add(buf.readResourceLocation());
        }
        return new OverlaySyncPacket(overlays);
    }

    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (packet.getOverlays().isEmpty()) {
                OverlayRenderer.startFadingOut();
            } else {
                OverlayRenderer.addOverlaysToRender(OverlayManager.getOverlaysByLocations(packet.getOverlays()));
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public List<ResourceLocation> getOverlays() {
        return overlays;
    }
}
