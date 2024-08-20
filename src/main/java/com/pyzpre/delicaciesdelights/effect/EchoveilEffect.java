package com.pyzpre.delicaciesdelights.effect;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class EchoveilEffect extends MobEffect {

    private static final int BOOST_COOLDOWN = 100; // 5 seconds cooldown (100 ticks)
    private int boostCooldown = BOOST_COOLDOWN;

    public EchoveilEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Handle boost on punch
            if (boostCooldown <= 0 && player.swinging) {
                boostPlayer(player);
                boostCooldown = BOOST_COOLDOWN; // Reset cooldown after boosting
            }

            // Decrease cooldown each tick
            if (boostCooldown > 0) {
                boostCooldown--;
            }
        }
    }

    private void boostPlayer(Player player) {
        Vec3 lookVec = player.getLookAngle();
        player.push(lookVec.x * 1.5, lookVec.y * 1.5, lookVec.z * 1.5);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.ELYTRA_FLYING, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }

    // Override these methods to enable Elytra flight
    public boolean canElytraFly(ItemStack stack, LivingEntity entity) {
        return entity.hasEffect(this);
    }

    public boolean elytraFlightTick(ItemStack stack, LivingEntity entity, int flightTicks) {
        if (entity.hasEffect(this)) {
            return true;
        }
        return false;
    }
}
