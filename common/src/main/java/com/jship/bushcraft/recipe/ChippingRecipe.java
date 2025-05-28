package com.jship.bushcraft.recipe;

import com.jship.bushcraft.init.ModBlocks;
import com.jship.bushcraft.init.ModRecipes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class ChippingRecipe extends AbstractCookingRecipe {
    public String group() {
        return this.group;
    }
    public CookingBookCategory category() {
        return this.category;
    }
    public Ingredient ingredient() {
        return this.ingredient;
    }
    public ItemStack result() {
        return this.result;
    }
    public float experience() {
        return this.experience;
    }
    public int cookingTime() {
        return this.cookingTime;
    }

    public ChippingRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result,
            float experience, int cookingTime) {
        super(ModRecipes.CHIPPING.get(), group, category, ingredient, result, experience, cookingTime);
    }

    public ItemStack getToastSymbol() {
        return new ItemStack(ModBlocks.CHIPPER.get());
    }

    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.CHIPPING_SERIALIZER.get();
    }

    public boolean isSpecial() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ChippingRecipe> {
      private static final MapCodec<ChippingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(ChippingRecipe::group),
                CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(ChippingRecipe::category),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ChippingRecipe::ingredient),
                ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ChippingRecipe::result),
                Codec.FLOAT.fieldOf("experience").orElse(0.0F).forGetter(ChippingRecipe::experience),
                Codec.INT.fieldOf("cookingtime").orElse(200).forGetter(ChippingRecipe::cookingTime))
            .apply(instance, ChippingRecipe::new));

        private final StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                ChippingRecipe::group,
                ByteBufCodecs.fromCodec(CookingBookCategory.CODEC),
                ChippingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC,
                ChippingRecipe::ingredient,
                ItemStack.STREAM_CODEC,
                ChippingRecipe::result,
                ByteBufCodecs.FLOAT,
                ChippingRecipe::experience,
                ByteBufCodecs.INT,
                recipe -> recipe.cookingTime,
                ChippingRecipe::new);

        public MapCodec<ChippingRecipe> codec() {
            return CODEC;
        }

        public StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
