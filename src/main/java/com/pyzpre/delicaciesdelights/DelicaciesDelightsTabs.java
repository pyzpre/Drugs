package com.pyzpre.delicaciesdelights;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;
import static com.pyzpre.delicaciesdelights.index.BlockRegistry.INJECTION_STAND;
import static com.pyzpre.delicaciesdelights.index.ItemRegistry.CONCEPT_ITEM;
import static com.pyzpre.delicaciesdelights.index.ItemRegistry.CONCESPT_ITEM;

public class DelicaciesDelightsTabs {
    private static final Logger LOGGER = LoggerFactory.getLogger(DelicaciesDelightsTabs.class);

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("delicaciesdelights", () -> {
        LOGGER.info("Registering creative mode tab: delicacies_delights_tab");
        return CreativeModeTab.builder()
                .withTabsBefore(CreativeModeTabs.COMBAT)
                .icon(() -> CONCEPT_ITEM.get().getDefaultInstance())
                .title(Component.translatable("item_group.delicaciesdelights"))
                .displayItems((parameters, output) -> {
                    output.accept(CONCEPT_ITEM.get());
                    output.accept(CONCESPT_ITEM.get());
                    output.accept(INJECTION_STAND.get().asItem());
                }).build();
    });
}
