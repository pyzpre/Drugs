package com.pyzpre.delicaciesdelights.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OverlayTagPacket {
    public final String tag;
    public final boolean add;

    public OverlayTagPacket(String tag, boolean add) {
        this.tag = tag;
        this.add = add;
    }

    public static void encode(OverlayTagPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.tag);
        buffer.writeBoolean(packet.add);
    }

    public static OverlayTagPacket decode(FriendlyByteBuf buffer) {
        return new OverlayTagPacket(buffer.readUtf(), buffer.readBoolean());
    }

    public static void handle(OverlayTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientOverlayTagPacketHandler.handle(packet, contextSupplier);
            } else {
                ServerOverlayTagPacketHandler.handle(packet, contextSupplier);
            }
        });
        context.setPacketHandled(true);
    }
}
