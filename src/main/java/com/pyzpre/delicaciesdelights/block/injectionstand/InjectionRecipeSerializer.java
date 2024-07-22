package com.pyzpre.delicaciesdelights.block.injectionstand;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

public class InjectionRecipeSerializer implements RecipeSerializer<InjectionRecipe> {
    public static final InjectionRecipeSerializer INSTANCE = new InjectionRecipeSerializer();

    @Override
    public InjectionRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
        Ingredient ingredient1 = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient1"));
        Ingredient ingredient2 = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "ingredient2"));
        ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
        return new InjectionRecipe(recipeId, ingredient1, ingredient2, result);
    }

    @Override
    public InjectionRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
        Ingredient ingredient1 = Ingredient.fromNetwork(buffer);
        Ingredient ingredient2 = Ingredient.fromNetwork(buffer);
        ItemStack result = buffer.readItem();
        return new InjectionRecipe(recipeId, ingredient1, ingredient2, result);
    }

    @Override
    public void toNetwork(FriendlyByteBuf buffer, InjectionRecipe recipe) {
        recipe.getIngredient1().toNetwork(buffer);
        recipe.getIngredient2().toNetwork(buffer);
        buffer.writeItem(recipe.getResult());
    }
}
