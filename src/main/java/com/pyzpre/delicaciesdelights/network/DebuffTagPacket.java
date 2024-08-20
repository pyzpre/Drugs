package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.DebuffManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DebuffTagPacket {
    public final String tag;
    public final boolean add;

    public DebuffTagPacket(String tag, boolean add) {
        this.tag = tag;
        this.add = add;
    }

    public static void encode(DebuffTagPacket packet, FriendlyByteBuf buffer) {
        buffer.writeUtf(packet.tag);
        buffer.writeBoolean(packet.add);
    }

    public static DebuffTagPacket decode(FriendlyByteBuf buffer) {
        return new DebuffTagPacket(buffer.readUtf(), buffer.readBoolean());
    }

    public static void handle(DebuffTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                ClientDebuffTagPacketHandler.handle(packet, contextSupplier);
            } else {
                ServerDebuffTagPacketHandler.handle(packet, contextSupplier);
            }
        });
        context.setPacketHandled(true);
    }
}
