package com.pyzpre.delicaciesdelights.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EchoveilEffect extends MobEffect {

    public EchoveilEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity instanceof Player) {
            Player player = (Player) entity;

            // Server-side logic can be added here if needed, but most logic for the boost is client-side
        }
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
