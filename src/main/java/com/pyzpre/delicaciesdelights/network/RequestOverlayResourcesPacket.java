package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.List;
import java.util.function.Supplier;

public class RequestOverlayResourcesPacket {
    private final List<ResourceLocation> overlays;

    public RequestOverlayResourcesPacket(List<ResourceLocation> overlays) {
        this.overlays = overlays;
    }

    public RequestOverlayResourcesPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        overlays = buf.readList(FriendlyByteBuf::readResourceLocation);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(overlays.size());
        buf.writeCollection(overlays, FriendlyByteBuf::writeResourceLocation);
    }

    public List<ResourceLocation> getOverlays() {
        return overlays;
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // Process the overlays on the client side
            OverlayManager.syncOverlays(overlays);
        });
        ctx.get().setPacketHandled(true);
    }
}
