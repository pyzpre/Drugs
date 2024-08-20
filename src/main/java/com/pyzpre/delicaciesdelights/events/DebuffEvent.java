package com.pyzpre.delicaciesdelights.events;

import com.pyzpre.delicaciesdelights.DelicaciesDelights;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = DelicaciesDelights.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DebuffEvent {
    private static final Logger LOGGER = LoggerFactory.getLogger(DebuffEvent.class);

    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingTickEvent event) {
        if (event.getEntity() instanceof Player player) {
            List<MobEffectInstance> effectsToAdd = new ArrayList<>();
            List<String> keysToRemove = new ArrayList<>();

            // Iterate over all active effects
            for (MobEffectInstance effect : player.getActiveEffects()) {
                // Check if any effect is about to expire
                if (effect.getDuration() <= 1) {
                    CompoundTag persistentTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
                    for (String key : persistentTag.getAllKeys()) {
                        if (DebuffManager.hasDebuffTag(player, key)) {
                            List<MobEffect> debuffs = DebuffManager.getDebuff(key);
                            if (debuffs != null) {
                                for (MobEffect debuff : debuffs) {
                                    effectsToAdd.add(new MobEffectInstance(debuff, 200)); // 200 ticks = 10 seconds
                                }
                            }
                            keysToRemove.add(key); // Collect keys to be removed
                        }
                    }
                }
            }

            // Add effects after the iteration to avoid ConcurrentModificationException
            for (MobEffectInstance effect : effectsToAdd) {
                player.addEffect(effect);
            }

            // Remove keys after the iteration to avoid ConcurrentModificationException
            for (String key : keysToRemove) {
                DebuffManager.removeDebuffTag(player, key);
            }
        }
    }

    @SubscribeEvent
    public static void onEffectRemoved(MobEffectEvent.Remove event) {
        if (event.getEntity() instanceof Player player) {
            List<String> keysToRemove = new ArrayList<>();
            CompoundTag persistentTag = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
            for (String key : persistentTag.getAllKeys()) {
                if (DebuffManager.hasDebuffTag(player, key)) {
                    keysToRemove.add(key);
                }
            }

            // Remove keys after the iteration to avoid ConcurrentModificationException
            for (String key : keysToRemove) {
                DebuffManager.removeDebuffTag(player, key);
            }
        }
    }
}