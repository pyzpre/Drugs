package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import com.pyzpre.delicaciesdelights.network.NetworkSetup;
import com.pyzpre.delicaciesdelights.network.DebuffTagPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DebuffManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebuffManager.class);
    public static final Map<String, List<MobEffect>> DEBUFF_MAP = new HashMap<>();

    static {
        DEBUFF_MAP.put("Nothing", List.of());
        DEBUFF_MAP.put("Slowness", List.of(MobEffects.MOVEMENT_SLOWDOWN));
        DEBUFF_MAP.put("Sickness", List.of(MobEffects.CONFUSION, MobEffects.POISON));
        DEBUFF_MAP.put("Blackout", List.of(MobEffects.BLINDNESS, MobEffects.CONFUSION));
        DEBUFF_MAP.put("Schizophrenic", List.of(EffectRegistry.CRAZY.get()));
        DEBUFF_MAP.put("Echo", List.of(EffectRegistry.ECHOVEIL.get()));
    }

    public static List<MobEffect> getDebuff(String tag) {
        return DEBUFF_MAP.get(tag);
    }
    public static Map<String, List<MobEffect>> getDebuffMap() {
        return DEBUFF_MAP;
    }
    public static boolean hasDebuffTag(Player player, String tag) {
        return player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).contains(tag);
    }

    public static void updateDebuffTag(Player player, String tag, boolean add, boolean fromNetwork) {
        CompoundTag rootPersistentData = player.getPersistentData();
        if (!rootPersistentData.contains(Player.PERSISTED_NBT_TAG, 10)) { // 10 is the ID for a compound tag
            rootPersistentData.put(Player.PERSISTED_NBT_TAG, new CompoundTag());
        }

        CompoundTag persistentData = rootPersistentData.getCompound(Player.PERSISTED_NBT_TAG);

        if (add) {
            persistentData.putBoolean(tag, true);
        } else {
            persistentData.remove(tag);
        }

        rootPersistentData.put(Player.PERSISTED_NBT_TAG, persistentData);

        if (!fromNetwork) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                if (server.isSingleplayer()) {
                    // Singleplayer environment
                    NetworkSetup.getChannel().sendToServer(new DebuffTagPacket(tag, add));
                } else {
                    // Multiplayer server
                    NetworkSetup.getChannel().send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                            new DebuffTagPacket(tag, add));
                }
            } else if (player.level().isClientSide()) {
                // Client-side in multiplayer
                NetworkSetup.getChannel().sendToServer(new DebuffTagPacket(tag, add));
            }
        }
    }

    public static void handleNetworkUpdate(Player player, String tag, boolean add) {
        if (add) {
            addDebuffTag(player, tag);
        } else {
            removeDebuffTag(player, tag);
        }
    }

    public static void addDebuffTag(Player player, String tag) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putBoolean(tag, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
    }

    public static void removeDebuffTag(Player player, String tag) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.remove(tag);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
    }
}
