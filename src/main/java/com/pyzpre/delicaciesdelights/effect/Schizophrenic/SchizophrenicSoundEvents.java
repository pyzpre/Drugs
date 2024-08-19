package com.pyzpre.delicaciesdelights.effect.Schizophrenic;

import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = "delicacies_delights", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class SchizophrenicSoundEvents {

    private static final Random random = new Random();
    private static final int SOUND_COOLDOWN_TICKS = 80; // 4 seconds (80t at 20 tps)
    private static int soundCooldown = 0;

    private static final SoundEvent[] randomSounds = new SoundEvent[]{
            SoundEvents.CREEPER_PRIMED,
            SoundEvents.WOODEN_DOOR_OPEN,
            SoundEvents.ZOMBIE_AMBIENT,
            SoundEvents.ARMOR_EQUIP_DIAMOND,
            SoundEvents.SKELETON_AMBIENT,
            SoundEvents.PHANTOM_AMBIENT,
            SoundEvents.WOODEN_PRESSURE_PLATE_CLICK_ON,
            SoundEvents.PISTON_EXTEND
    };

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        if (player.level().isClientSide && player.hasEffect(EffectRegistry.CRAZY.get())) {
            if (soundCooldown > 0) {
                soundCooldown--;
            } else if (random.nextFloat() < 0.005) { // 0.5% chance each tick
                playRandomSound(player);
                soundCooldown = SOUND_COOLDOWN_TICKS; // Reset cooldown
            }
        }
    }

    private static void playRandomSound(Player player) {
        SoundEvent sound = randomSounds[random.nextInt(randomSounds.length)];
        // Play sound locally for the player
        player.level().playLocalSound(player.getX(), player.getY(), player.getZ(), sound, SoundSource.PLAYERS, 1.0F, 1.0F, false);
    }
}
