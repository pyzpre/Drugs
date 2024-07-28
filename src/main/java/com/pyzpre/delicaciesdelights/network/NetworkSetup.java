package com.pyzpre.delicaciesdelights.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkSetup {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("delicaciesdelights", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static SimpleChannel getChannel() {
        return CHANNEL;
    }

    public static void registerMessages() {
        int id = 0;
        CHANNEL.messageBuilder(OverlayTagPacket.class, id++)
                .encoder(OverlayTagPacket::encode)
                .decoder(OverlayTagPacket::decode)
                .consumerMainThread(OverlayTagPacket::handle)
                .add();

        CHANNEL.messageBuilder(OverlaySyncPacket.class, id++)
                .encoder(OverlaySyncPacket::encode)
                .decoder(OverlaySyncPacket::decode)
                .consumerMainThread(OverlaySyncPacket::handle)
                .add();

        CHANNEL.messageBuilder(RequestOverlayResourcesPacket.class, id++)
                .encoder(RequestOverlayResourcesPacket::toBytes)
                .decoder(RequestOverlayResourcesPacket::new)
                .consumerMainThread(RequestOverlayResourcesPacket::handle)
                .add();
    }
}
