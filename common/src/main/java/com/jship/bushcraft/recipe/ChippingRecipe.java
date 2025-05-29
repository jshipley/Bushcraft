package com.jship.bushcraft.recipe;

import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lombok.Builder;
import lombok.experimental.Accessors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;

@Builder
@Accessors(fluent = true)
public record ChippingRecipe(String group, CookingBookCategory category, Ingredient input, ItemStack result, float experience, int time) implements Recipe<SingleRecipeInput> {
    private static final String DEFAULT_GROUP = "";
    private static final CookingBookCategory DEFAULT_CATEGORY = CookingBookCategory.MISC;
    private static final int DEFAULT_TIME = 200;

    public static class ChippingRecipeBuilder {
        ChippingRecipeBuilder() {
            group = DEFAULT_GROUP;
            category = DEFAULT_CATEGORY;
            time = DEFAULT_TIME;
        }

        public ChippingRecipeBuilder input(ItemLike item) {
            this.input = Ingredient.of(item);
            return this;            
        }

        public ChippingRecipeBuilder result(ItemLike item) {
            this.result = new ItemStack(item);
            return this;
        }

        public ChippingRecipeBuilder result(ItemLike item, int count) {
            this.result = new ItemStack(item, count);
            return this;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.of(Ingredient.EMPTY, this.input);
    }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return !level.isClientSide && this.input().test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return this.result();
    }

    @Override
    public boolean canCraftInDimensions(int i, int j) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHIPPING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CHIPPING.get();
    }

    public static class Serializer implements RecipeSerializer<ChippingRecipe> {
      private static final MapCodec<ChippingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ChippingRecipe::group),
                CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(ChippingRecipe::category),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ChippingRecipe::input),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ChippingRecipe::result),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(ChippingRecipe::experience),
                Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(ChippingRecipe::time))
            .apply(instance, ChippingRecipe::new));

        private final StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ChippingRecipe::group,
                ByteBufCodecs.fromCodec(CookingBookCategory.CODEC),
                ChippingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC,
                ChippingRecipe::input,
                ItemStack.STREAM_CODEC,
                ChippingRecipe::result,
                ByteBufCodecs.FLOAT,
                ChippingRecipe::experience,
                ByteBufCodecs.INT,
                recipe -> recipe.time,
                ChippingRecipe::new);

        public MapCodec<ChippingRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
