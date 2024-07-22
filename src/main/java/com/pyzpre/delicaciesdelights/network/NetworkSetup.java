package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class NetworkSetup {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DelicaciesDelights.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void registerMessages() {
        int id = 0;
        CHANNEL.registerMessage(id++, OverlaySyncPacket.class, OverlaySyncPacket::encode, OverlaySyncPacket::decode, OverlaySyncPacketHandler::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        CHANNEL.registerMessage(id++, OverlayTagPacket.class, OverlayTagPacket::encode, OverlayTagPacket::decode, OverlayTagPacketHandler::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static void registerClientMessages() {
        int id = 0;
        CHANNEL.registerMessage(id++, OverlayTagPacket.class, OverlayTagPacket::encode, OverlayTagPacket::decode, OverlayTagPacketHandler::handle, Optional.empty());
    }

    public static SimpleChannel getChannel() {
        return CHANNEL;
    }
}
