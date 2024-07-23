package com.pyzpre.delicaciesdelights.network;

import net.minecraft.network.FriendlyByteBuf;

public class OverlayTagPacket {
    public final String tag;
    public final boolean add;

    public OverlayTagPacket(String tag, boolean add) {
        this.tag = tag;
        this.add = add;
    }

    public OverlayTagPacket(FriendlyByteBuf buffer) {
        this.tag = buffer.readUtf(32767);
        this.add = buffer.readBoolean();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.tag);
        buffer.writeBoolean(this.add);
    }

    public static OverlayTagPacket decode(FriendlyByteBuf buffer) {
        return new OverlayTagPacket(buffer);
    }
}
