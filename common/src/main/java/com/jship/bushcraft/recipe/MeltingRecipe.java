package com.jship.bushcraft.recipe;

import com.jship.bushcraft.init.ModRecipes;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import dev.architectury.fluid.FluidStack;
import lombok.Builder;
import lombok.experimental.Accessors;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.material.Fluid;

@Builder
@Accessors(fluent = true)
public record MeltingRecipe(String group, CookingBookCategory category, Ingredient ingredient, FluidStack result,
        int time) implements Recipe<SingleRecipeInput> {
    private static final String DEFAULT_GROUP = "";
    private static final CookingBookCategory DEFAULT_CATEGORY = CookingBookCategory.MISC;
    private static final int DEFAULT_TIME = 3600;

    public static class MeltingRecipeBuilder {
        MeltingRecipeBuilder() {
            group = DEFAULT_GROUP;
            category = DEFAULT_CATEGORY;
            time = DEFAULT_TIME;
        }

        public MeltingRecipeBuilder ingredient(ItemLike... ingredients) {
            this.ingredient = Ingredient.of(ingredients);
            return this;
        }

        public MeltingRecipeBuilder ingredient(TagKey<Item> ingredientTag) {
            this.ingredient = Ingredient.of(ingredientTag);
            return this;
        }

        public MeltingRecipeBuilder result(Fluid fluid, long amount) {
            this.result = FluidStack.create(fluid, amount);
            return this;
        }
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
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
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
        return ModRecipes.MELTING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.MELTING.get();
    }

    public static class Serializer implements RecipeSerializer<MeltingRecipe> {

        public static final MapCodec<MeltingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        Codec.STRING.optionalFieldOf("group", DEFAULT_GROUP).forGetter(MeltingRecipe::group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(DEFAULT_CATEGORY)
                                .forGetter(MeltingRecipe::category),
                        Ingredient.CODEC.fieldOf("ingredient").forGetter(MeltingRecipe::ingredient),
                        Codec.pair(
                                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").codec(),
                                Codec.FLOAT.fieldOf("buckets").codec()).xmap(
                                        pair -> FluidStack.create(pair.getFirst(),
                                                (long) (pair.getSecond() * FluidStack.bucketAmount())),
                                        stack -> Pair.of(stack.getFluid(),
                                                (float) stack.getAmount() / FluidStack.bucketAmount()))
                                .fieldOf("result").forGetter(MeltingRecipe::result),
                        Codec.INT.fieldOf("time").orElse(DEFAULT_TIME).forGetter(MeltingRecipe::time))
                .apply(instance, MeltingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                MeltingRecipe::group,
                ByteBufCodecs.fromCodec(CookingBookCategory.CODEC),
                MeltingRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC,
                MeltingRecipe::ingredient,
                FluidStack.STREAM_CODEC,
                MeltingRecipe::result,
                ByteBufCodecs.INT,
                MeltingRecipe::time,
                MeltingRecipe::new);

        @Override
        public MapCodec<MeltingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, MeltingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
