package com.pyzpre.delicaciesdelights.block.injectionstand;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class InjectionRecipe implements Recipe<InjectionStandEntity> {
    private final ResourceLocation id;
    private final Ingredient ingredient1;
    private final Ingredient ingredient2;
    private final ItemStack result;

    public InjectionRecipe(ResourceLocation id, Ingredient ingredient1, Ingredient ingredient2, ItemStack result) {
        this.id = id;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.result = result;
    }

    @Override
    public boolean matches(InjectionStandEntity inv, Level level) {
        return ingredient1.test(inv.getItem(0)) && ingredient2.test(inv.getItem(1));
    }

    @Override
    public ItemStack assemble(InjectionStandEntity inv, RegistryAccess registryAccess) {
        return assembleWithCount(inv, registryAccess, 1); // Default to 1 if no count is provided
    }

    public ItemStack assembleWithCount(InjectionStandEntity inv, RegistryAccess registryAccess, int count) {
        ItemStack resultStack = result.copy();
        resultStack.setCount(result.getCount() * count);
        return resultStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return InjectionRecipeSerializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return InjectionRecipeType.INSTANCE;
    }

    public Ingredient getIngredient1() {
        return ingredient1;
    }

    public Ingredient getIngredient2() {
        return ingredient2;
    }

    public ItemStack getResult() {
        return result;
    }
}
