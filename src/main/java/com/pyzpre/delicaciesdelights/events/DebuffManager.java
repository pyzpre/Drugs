package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
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
    }

    public static List<MobEffect> getDebuff(String tag) {
        List<MobEffect> debuffs = DEBUFF_MAP.get(tag);
        return debuffs;
    }

    public static boolean hasDebuffTag(Player player, String tag) {
        boolean hasTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG).contains(tag);
        return hasTag;
    }

    public static void removeDebuffTag(Player player, String tag) {
        // Ensure only tags that exist in the DEBUFF_MAP are removed
        if (DEBUFF_MAP.containsKey(tag)) {
            CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            persistentData.remove(tag);
            player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
        }
    }

    public static void addDebuffTag(Player player, String tag) {
        CompoundTag persistentData = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        persistentData.putBoolean(tag, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persistentData);
    }
}
