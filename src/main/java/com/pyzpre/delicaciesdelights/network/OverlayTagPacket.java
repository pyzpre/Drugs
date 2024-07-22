package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class OverlayTagPacket {
    private static final Logger LOGGER = LogManager.getLogger();

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
