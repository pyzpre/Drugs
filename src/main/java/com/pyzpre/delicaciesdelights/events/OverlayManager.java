package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.OverlayTagPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class OverlayManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverlayManager.class);
    public static boolean suppressClientUpdate = false;

    private static final Map<String, List<OverlayMetadata>> OVERLAY_MAP = new HashMap<>();

    static {
        OVERLAY_MAP.put("Unanchored", List.of(new OverlayMetadata(new ResourceLocation(DelicaciesDelights.MODID, "textures/misc/silly.png"), 0.0001f, 0.1f, true, 4.0f))); // Pulsate with 1 second duration
        OVERLAY_MAP.put("SomeOtherOverlay", List.of(new OverlayMetadata(new ResourceLocation(DelicaciesDelights.MODID, "textures/misc/saul.png"), 0.0001f, 0.0f, false, 1.0f))); // No pulsate
    }

    public static List<OverlayMetadata> getOverlays(String tag) {
        return OVERLAY_MAP.get(tag);
    }

    public static boolean hasOverlayTag(Player player, String tag) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        return persistentData.contains(tag);
    }

    public static void updateOverlayTag(Player player, String tag, boolean add, boolean fromNetwork) {
        CompoundTag rootPersistentData = player.getPersistentData();
        if (!rootPersistentData.contains(Player.PERSISTED_NBT_TAG, 10)) { // 10 is the ID for a compound tag
            rootPersistentData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }

        CompoundTag persistentData = rootPersistentData.getCompound(Player.PERSISTED_NBT_TAG);

        if (add) {
            LOGGER.info("Adding overlay tag: {}", tag);
            persistentData.putBoolean(tag, true);
            LOGGER.info("Added overlay tag: {}. Current Persistent Data after update: {}", tag, persistentData);
        } else {
            LOGGER.info("Removing overlay tag: {}", tag);
            persistentData.remove(tag);
            LOGGER.info("Removed overlay tag: {}. Current Persistent Data after update: {}", tag, persistentData);
        }

        rootPersistentData.put(Player.PERSISTED_NBT_TAG, persistentData);

        if (!fromNetwork && player.level().isClientSide()) {
            suppressClientUpdate = true;
            // Send packet to server to update server-side data
            NetworkSetup.getChannel().sendToServer(new OverlayTagPacket(tag, add));
        } else if (!fromNetwork && !player.level().isClientSide()) {
            // Send packet to client to update client-side data
            NetworkSetup.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player), new OverlayTagPacket(tag, add));
        }
    }

    public static void handleNetworkUpdate(Player player, String tag, boolean add) {
        updateOverlayTag(player, tag, add, true);
        suppressClientUpdate = false;
    }

    public static class OverlayMetadata {
        public final ResourceLocation location;
        public final float alphaIncrement;
        public final float fovChange;
        public final boolean pulsate;
        public final float pulsateDuration; // Added for pulsate duration

        public OverlayMetadata(ResourceLocation location, float alphaIncrement, float fovChange, boolean pulsate, float pulsateDuration) {
            this.location = location;
            this.alphaIncrement = alphaIncrement;
            this.fovChange = fovChange;
            this.pulsate = pulsate;
            this.pulsateDuration = pulsateDuration; // Initialize pulsateDuration
        }
    }

    public static Map<String, List<OverlayMetadata>> getOverlayMap() {
        return OVERLAY_MAP;
    }

    public static List<OverlayMetadata> getOverlaysByLocations(List<ResourceLocation> locations) {
        List<OverlayMetadata> result = new ArrayList<>();
        for (List<OverlayMetadata> metadataList : OVERLAY_MAP.values()) {
            for (OverlayMetadata metadata : metadataList) {
                if (locations.contains(metadata.location)) {
                    result.add(metadata);
                }
            }
        }
        return result;
    }
}
