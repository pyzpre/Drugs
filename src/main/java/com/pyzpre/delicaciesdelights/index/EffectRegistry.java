package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.effect.EchoveilEffect;
import com.pyzpre.delicaciesdelights.effect.Schizophrenic.SchizophrenicEffect;
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

    public static final RegistryObject<MobEffect> CRAZY = MOB_EFFECTS.register("schizophrenic",
            () -> new SchizophrenicEffect(MobEffectCategory.NEUTRAL, 800000980));

    public static final RegistryObject<MobEffect> ECHOVEIL = MOB_EFFECTS.register("echo_veil",
            () -> new EchoveilEffect(MobEffectCategory.NEUTRAL, 800000980));

    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}

