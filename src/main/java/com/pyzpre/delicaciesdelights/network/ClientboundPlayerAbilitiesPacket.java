package com.pyzpre.delicaciesdelights.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ClientboundPlayerAbilitiesPacket {
    private final boolean mayFly;
    private final boolean flying;
    private final boolean invulnerable;
    private final boolean canBuild;
    private final float flyingSpeed;
    private final float walkingSpeed;
    private final boolean fallFlying;  // New field for Elytra fall flying state

    public ClientboundPlayerAbilitiesPacket(Player player) {
        this.mayFly = player.getAbilities().mayfly;
        this.flying = player.getAbilities().flying;
        this.invulnerable = player.getAbilities().invulnerable;
        this.canBuild = player.getAbilities().mayBuild;
        this.flyingSpeed = player.getAbilities().getFlyingSpeed();
        this.walkingSpeed = player.getAbilities().getWalkingSpeed();
        this.fallFlying = player.isFallFlying();  // Capture the fall flying state
    }

    public ClientboundPlayerAbilitiesPacket(FriendlyByteBuf buf) {
        this.mayFly = buf.readBoolean();
        this.flying = buf.readBoolean();
        this.invulnerable = buf.readBoolean();
        this.canBuild = buf.readBoolean();
        this.flyingSpeed = buf.readFloat();
        this.walkingSpeed = buf.readFloat();
        this.fallFlying = buf.readBoolean();  // Read the fall flying state from the buffer
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.mayFly);
        buf.writeBoolean(this.flying);
        buf.writeBoolean(this.invulnerable);
        buf.writeBoolean(this.canBuild);
        buf.writeFloat(this.flyingSpeed);
        buf.writeFloat(this.walkingSpeed);
        buf.writeBoolean(this.fallFlying);  // Write the fall flying state to the buffer
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = ctx.get().getSender();
            if (player != null) {
                player.getAbilities().mayfly = this.mayFly;
                player.getAbilities().flying = this.flying;
                player.getAbilities().invulnerable = this.invulnerable;
                player.getAbilities().mayBuild = this.canBuild;
                player.getAbilities().setFlyingSpeed(this.flyingSpeed);
                player.getAbilities().setWalkingSpeed(this.walkingSpeed);
                if (this.fallFlying) {
                    player.startFallFlying();  // Start Elytra flight if fallFlying is true
                } else {
                    player.stopFallFlying();   // Stop Elytra flight if fallFlying is false
                }
                player.onUpdateAbilities();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
