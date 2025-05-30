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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

@Builder
@Accessors(fluent = true)
public record CoolingRecipe(String group, CookingBookCategory category, FluidStack input, ItemStack result,
        int time) implements Recipe<FluidStackRecipeInput> {
    private static final String DEFAULT_GROUP = "";
    private static final CookingBookCategory DEFAULT_CATEGORY = CookingBookCategory.MISC;
    private static final int DEFAULT_TIME = 3600;

    public static class CoolingRecipeBuilder {
        CoolingRecipeBuilder() {
            group = DEFAULT_GROUP;
            category = DEFAULT_CATEGORY;
            time = DEFAULT_TIME;
        }

        public CoolingRecipeBuilder input(Fluid fluid, long amount) {
            this.input = FluidStack.create(fluid, amount);
            return this;
        }

        public CoolingRecipeBuilder result(ItemLike item) {
            this.result = new ItemStack(item);
            return this;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    @Override
    public boolean matches(FluidStackRecipeInput recipeInput, Level level) {
        return !level.isClientSide && !level.dimensionType().ultraWarm()
                && this.input().isFluidStackEqual(recipeInput.fluidStack());
    }

    @Override
    public ItemStack assemble(FluidStackRecipeInput recipeInput, HolderLookup.Provider provider) {
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
        return ModRecipes.COOLING_SERIALIZER.get();
    }

    @Override
    public RecipeType<?> getType() {
        return ModRecipes.COOLING.get();
    }

    public static class Serializer implements RecipeSerializer<CoolingRecipe> {

        public static final MapCodec<CoolingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(
                        Codec.STRING.optionalFieldOf("group", DEFAULT_GROUP).forGetter(CoolingRecipe::group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(DEFAULT_CATEGORY)
                                .forGetter(CoolingRecipe::category),
                        Codec.pair(
                                BuiltInRegistries.FLUID.byNameCodec().fieldOf("fluid").codec(),
                                Codec.FLOAT.fieldOf("buckets").codec()).xmap(
                                        pair -> FluidStack.create(pair.getFirst(),
                                                (long) (pair.getSecond() * FluidStack.bucketAmount())),
                                        stack -> Pair.of(stack.getFluid(),
                                                (float) stack.getAmount() / FluidStack.bucketAmount()))
                                .fieldOf("input").forGetter(CoolingRecipe::input),
                        ItemStack.CODEC.fieldOf("result").forGetter(CoolingRecipe::result),
                        Codec.INT.fieldOf("time").orElse(DEFAULT_TIME).forGetter(CoolingRecipe::time))
                .apply(instance, CoolingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, CoolingRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8,
                CoolingRecipe::group,
                ByteBufCodecs.fromCodec(CookingBookCategory.CODEC),
                CoolingRecipe::category,
                FluidStack.STREAM_CODEC,
                CoolingRecipe::input,
                ItemStack.STREAM_CODEC,
                CoolingRecipe::result,
                ByteBufCodecs.INT,
                CoolingRecipe::time,
                CoolingRecipe::new);

        @Override
        public MapCodec<CoolingRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CoolingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
