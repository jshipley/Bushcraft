package com.jship.bushcraft.recipe;

import com.jship.bushcraft.Bushcraft.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

public record DryingRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, int time) implements Recipe<SingleRecipeInput> {
    private static final int DEFAULT_TIME = 900;

    public DryingRecipe() {
        this("", CookingBookCategory.MISC, Ingredient.EMPTY, ItemStack.EMPTY, DEFAULT_TIME);
    }

    public DryingRecipe group(String withGroup) {
        return new DryingRecipe(withGroup, category, ingredient, result, time);
    }

    public DryingRecipe category(CookingBookCategory withCategory) {
        return new DryingRecipe(group, withCategory, ingredient, result, time);
    }

    public DryingRecipe ingredient(ItemLike... withIngredients) {
        return ingredient(Ingredient.of(withIngredients));
    }

    public DryingRecipe ingredient(TagKey<Item> withIngredient) {
        return ingredient(Ingredient.of(withIngredient));
    }

    public DryingRecipe ingredient(Ingredient withIngredient) {
        return new DryingRecipe(group, category, withIngredient, result, time);
    }

    public DryingRecipe result(ItemLike withResult) {
        return result(new ItemStack(withResult));
    }

    public DryingRecipe result(ItemStack withResult) {
        return new DryingRecipe(group, category, ingredient, withResult, time);
    }

    public DryingRecipe time(int withTime) {
        return new DryingRecipe(group, category, ingredient, result, withTime);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.create();
        list.add(ingredient);
        return list;
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return !level.isClientSide && ingredient.test(recipeInput.getItem(0));
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, Provider provider) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem(Provider provider) {
        return result;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.DRYING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.DRYING.get();
    }

    public static class Serializer implements RecipeSerializer<DryingRecipe> {

        public static final MapCodec<DryingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance
                .group(
                    Codec.STRING.optionalFieldOf("group", "").forGetter(DryingRecipe::group),
                    CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(DryingRecipe::category),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(DryingRecipe::ingredient),
                    ItemStack.STRICT_SINGLE_ITEM_CODEC.fieldOf("result").forGetter(DryingRecipe::result),
                    Codec.INT.fieldOf("time").orElse(DEFAULT_TIME).forGetter(DryingRecipe::time)
                )
                .apply(instance, DryingRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            DryingRecipe::group,
            ByteBufCodecs.fromCodec(CookingBookCategory.CODEC),
            DryingRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC,
            DryingRecipe::ingredient,
            ItemStack.STREAM_CODEC,
            DryingRecipe::result,
            ByteBufCodecs.INT,
            DryingRecipe::time,
            DryingRecipe::new
        );

        @Override
        public MapCodec<DryingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, DryingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
