package com.pyzpre.delicaciesdelights.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ServerboundPlayerFallFlyingPacket {
    private final boolean fallFlying;

    public ServerboundPlayerFallFlyingPacket(boolean fallFlying) {
        this.fallFlying = fallFlying;
    }

    public ServerboundPlayerFallFlyingPacket(FriendlyByteBuf buf) {
        this.fallFlying = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.fallFlying);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                if (this.fallFlying) {
                    player.startFallFlying();
                } else {
                    player.stopFallFlying();
                }
                // Use the Forge networking channel to send a packet to the client
                NetworkSetup.getChannel().sendTo(new ClientboundPlayerAbilitiesPacket(player), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
