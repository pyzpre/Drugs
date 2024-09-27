package com.pyzpre.delicaciesdelights.item;

import com.pyzpre.delicaciesdelights.events.DebuffManager;
import com.pyzpre.delicaciesdelights.events.OverlayManager;
import com.pyzpre.delicaciesdelights.index.EffectRegistry;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ConceptItem2 extends Item {
    private final String overlayTag;
    private final String debuffTag;

    public ConceptItem2(Properties properties, String overlayTag, String debuffTag) {
        super(properties);
        this.overlayTag = overlayTag;
        this.debuffTag = debuffTag;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {

        if (entity instanceof Player player) {
            if (!world.isClientSide) {
                MobEffectInstance effectInstance = new MobEffectInstance(EffectRegistry.ECHOVEIL.get(), 200);
                player.addEffect(effectInstance);
                OverlayManager.updateOverlayTag(player, "SomeOtherOverlay", true, false);
                DebuffManager.updateDebuffTag(player, "Schizophrenic", true, false);
            }
        }
        return super.finishUsingItem(stack, world, entity);
    }
}

