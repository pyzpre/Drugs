package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.effect.CrazyEffect;
import com.pyzpre.delicaciesdelights.effect.UnanchoredEffect;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;

public class EffectRegistry {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);

    public static final RegistryObject<MobEffect> UNANCHORED = MOB_EFFECTS.register("unanchored",
            () -> new UnanchoredEffect(MobEffectCategory.NEUTRAL, 800000980));
    public static final RegistryObject<MobEffect> CRAZY = MOB_EFFECTS.register("crazy",
            () -> new CrazyEffect(MobEffectCategory.NEUTRAL, 800000980));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}

