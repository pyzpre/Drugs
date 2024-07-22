package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionStandEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final RegistryObject<BlockEntityType<InjectionStandEntity>> INJECTION_STAND = BLOCK_ENTITIES.register("injection_stand", () ->
            BlockEntityType.Builder.of(InjectionStandEntity::new, BlockRegistry.INJECTION_STAND.get()).build(null));
}
