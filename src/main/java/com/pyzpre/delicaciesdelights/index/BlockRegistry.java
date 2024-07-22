package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionStandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;

public class BlockRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final RegistryObject<InjectionStandBlock> INJECTION_STAND = BLOCKS.register("injection_stand", () ->
            new InjectionStandBlock(BlockBehaviour.Properties.copy(Blocks.BREWING_STAND)
                    .sound(SoundType.WOOD)
                    .strength(3.5f)
                    .lightLevel(state -> state.getValue(InjectionStandBlock.LIT) ? 13 : 0)));
}
