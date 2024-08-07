package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import com.pyzpre.delicaciesdelights.events.OverlayRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class OverlaySyncPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverlaySyncPacket.class);
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
            ResourceLocation overlay = buf.readResourceLocation();
            overlays.add(overlay);
        }
        OverlaySyncPacket packet = new OverlaySyncPacket(overlays);
        return packet;
    }

    public static void handle(OverlaySyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientOverlaySyncPacketHandler.handle(packet, ctx);
            } else {
                ServerOverlaySyncPacketHandler.handle(packet, ctx);
            }
        });
        context.setPacketHandled(true);
    }

    public List<ResourceLocation> getOverlays() {
        return overlays;
    }
}
