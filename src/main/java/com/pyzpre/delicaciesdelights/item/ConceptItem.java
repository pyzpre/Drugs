package com.pyzpre.delicaciesdelights.item;

import com.pyzpre.delicaciesdelights.events.DebuffManager;
import com.pyzpre.delicaciesdelights.events.OverlayManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ConceptItem extends Item {
    private final String overlayTag;
    private final String debuffTag;


    public ConceptItem(Properties properties, String overlayTag, String debuffTag) {
        super(properties);
        this.overlayTag = overlayTag;
        this.debuffTag = debuffTag;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {

        if (entity instanceof Player player) {
            if (!world.isClientSide) {
                MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.HEAL, 200);
                player.addEffect(effectInstance);
                OverlayManager.updateOverlayTag(player, "Unanchored", true, false);
                DebuffManager.addDebuffTag(player, debuffTag);
            }
        }
        return super.finishUsingItem(stack, world, entity);
    }
}

