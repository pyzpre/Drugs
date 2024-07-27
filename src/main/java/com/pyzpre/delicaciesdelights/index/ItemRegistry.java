package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.item.ConceptItem;
import com.pyzpre.delicaciesdelights.item.ConceptItem2;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static com.pyzpre.delicaciesdelights.DelicaciesDelights.MODID;

public class ItemRegistry {
    public static final String MODID = "delicacies_delights";
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<ConceptItem> CONCEPT_ITEM = ITEMS.register("concept_item",
            () -> new ConceptItem(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat()
                    .nutrition(1)
                    .saturationMod(2f)
                    .build()), "Unanchored", "Slowness"));
    public static final RegistryObject<ConceptItem2> CONCESPT_ITEM = ITEMS.register("concespt_item",
            () -> new ConceptItem2(new Item.Properties().food(new FoodProperties.Builder()
                    .alwaysEat()
                    .nutrition(1)
                    .saturationMod(2f)
                    .build()), "SomeOtherOverlay", "Nothing"));

    public static final RegistryObject<Item> INJECTION_STAND = ITEMS.register("injection_stand",
            () -> new BlockItem(BlockRegistry.INJECTION_STAND.get(), new Item.Properties()));
}
