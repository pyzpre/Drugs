package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionStandContainer;
import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionStandEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;


public class ContainerRegistry {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);

    public static final RegistryObject<MenuType<InjectionStandContainer>> INJECTION_STAND = MENU_TYPES.register("injection_stand",
            () -> IForgeMenuType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                InjectionStandEntity blockEntity = (InjectionStandEntity) inv.player.level().getBlockEntity(pos);
                return new InjectionStandContainer(windowId, inv, blockEntity, new SimpleContainerData(2));
            }));
}
