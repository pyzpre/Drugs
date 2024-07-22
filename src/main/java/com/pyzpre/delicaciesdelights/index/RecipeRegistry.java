package com.pyzpre.delicaciesdelights.index;

import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionRecipe;
import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionRecipeSerializer;
import com.pyzpre.delicaciesdelights.block.injectionstand.InjectionRecipeType;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RecipeRegistry {
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, "delicacies_delights");
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, "delicacies_delights");

    public static final RegistryObject<RecipeType<InjectionRecipe>> INJECTION_RECIPE_TYPE = RECIPE_TYPES.register("injection", () -> new InjectionRecipeType());
    public static final RegistryObject<RecipeSerializer<InjectionRecipe>> INJECTION_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("injection", () -> InjectionRecipeSerializer.INSTANCE);

    public static void register() {
        RECIPE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
