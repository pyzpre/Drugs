package com.pyzpre.delicaciesdelights.network;

import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OverlayTagPacketHandler {
    public static void handle(OverlayTagPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isServer()) {
                ServerPlayer serverPlayer = context.getSender();
                if (serverPlayer != null) {
                    OverlayManager.updateOverlayTag(serverPlayer, packet.tag, packet.add, true);
                    NetworkSetup.getChannel().sendTo(new OverlayTagPacket(packet.tag, packet.add), serverPlayer.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
                }
            } else {
                Player clientPlayer = net.minecraft.client.Minecraft.getInstance().player;
                if (clientPlayer != null && !OverlayManager.suppressClientUpdate) {
                    OverlayManager.handleNetworkUpdate(clientPlayer, packet.tag, packet.add);
                }
            }
        });
        context.setPacketHandled(true);
    }
}
