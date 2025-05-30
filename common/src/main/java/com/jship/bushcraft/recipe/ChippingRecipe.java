package com.jship.bushcraft.recipe;

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

@Builder
@Accessors(fluent = true)
public record ChippingRecipe(String group, CookingBookCategory category, Ingredient input, ItemStack result, ChanceResult chanceResult,
        float experience, int time) implements Recipe<SingleRecipeInput> {
    private static final String DEFAULT_GROUP = "";
    private static final CookingBookCategory DEFAULT_CATEGORY = CookingBookCategory.MISC;
    private static final int DEFAULT_TIME = 200;

    public static class ChippingRecipeBuilder {
        ChippingRecipeBuilder() {
            group = DEFAULT_GROUP;
            category = DEFAULT_CATEGORY;
            chanceResult = ChanceResult.EMPTY;
            time = DEFAULT_TIME;
        }

        public ChippingRecipeBuilder input(ItemLike item) {
            this.input = Ingredient.of(item);
            return this;
        }

        public ChippingRecipeBuilder input(TagKey<Item> itemTag) {
            this.input = Ingredient.of(itemTag);
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

        public ChippingRecipeBuilder chanceResult(ItemLike item, float chance) {
            this.chanceResult = new ChanceResult(new ItemStack(item), chance);
            return this;
        }

        public ChippingRecipeBuilder chanceResult(ItemLike item, int count, float chance) {
            this.chanceResult = new ChanceResult(new ItemStack(item, count), chance);
            return this;
        }
    }

    @Override
    public NonNullList<Ingredient> getIngredients() { return NonNullList.of(Ingredient.EMPTY, this.input); }

    @Override
    public boolean matches(SingleRecipeInput recipeInput, Level level) {
        return !level.isClientSide && this.input().test(recipeInput.item());
    }

    @Override
    public ItemStack assemble(SingleRecipeInput recipeInput, HolderLookup.Provider provider) { return this.result(); }

    @Override
    public boolean canCraftInDimensions(int i, int j) { return true; }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider provider) { return ItemStack.EMPTY; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ModRecipes.CHIPPING_SERIALIZER.get(); }

    @Override
    public RecipeType<?> getType() { return ModRecipes.CHIPPING.get(); }

    @Override
    public boolean isSpecial() { return true; }

    public record ChanceResult(ItemStack result, float chance) {
        public static final MapCodec<ChanceResult> CODEC = RecordCodecBuilder
                .mapCodec(instance -> instance
                        .group(ItemStack.OPTIONAL_CODEC.fieldOf("result").forGetter(ChanceResult::result),
                                Codec.FLOAT.fieldOf("chance").orElse(0.05f).forGetter(ChanceResult::chance))
                        .apply(instance, ChanceResult::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, ChanceResult> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC,
                ChanceResult::result, ByteBufCodecs.FLOAT, ChanceResult::chance, ChanceResult::new);
        public static final ChanceResult EMPTY = new ChanceResult(ItemStack.EMPTY, 0.0f);

        public boolean isEmpty() { return this.result.isEmpty(); }
    }

    public static class Serializer implements RecipeSerializer<ChippingRecipe> {
        private static final MapCodec<ChippingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance
                .group(Codec.STRING.optionalFieldOf("group", "").forGetter(ChippingRecipe::group),
                        CookingBookCategory.CODEC.optionalFieldOf("category", CookingBookCategory.MISC).forGetter(ChippingRecipe::category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(ChippingRecipe::input),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(ChippingRecipe::result),
                        ChanceResult.CODEC.codec().optionalFieldOf("chanceResult", ChanceResult.EMPTY)
                                .forGetter(ChippingRecipe::chanceResult),
                        Codec.FLOAT.optionalFieldOf("experience", 0.0F).forGetter(ChippingRecipe::experience),
                        Codec.INT.optionalFieldOf("cookingtime", 200).forGetter(ChippingRecipe::time))
                .apply(instance, ChippingRecipe::new));

        private final StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public ChippingRecipe decode(RegistryFriendlyByteBuf buf) {
                return new ChippingRecipe(
                    ByteBufCodecs.STRING_UTF8.decode(buf),
                    ByteBufCodecs.fromCodec(CookingBookCategory.CODEC).decode(buf),
                    Ingredient.CONTENTS_STREAM_CODEC.decode(buf),
                    ItemStack.STREAM_CODEC.decode(buf),
                    ChanceResult.STREAM_CODEC.decode(buf),
                    ByteBufCodecs.FLOAT.decode(buf),
                    ByteBufCodecs.INT.decode(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, ChippingRecipe recipe) {
                ByteBufCodecs.STRING_UTF8.encode(buf, recipe.group());
                ByteBufCodecs.fromCodec(CookingBookCategory.CODEC).encode(buf, recipe.category());
                Ingredient.CONTENTS_STREAM_CODEC.encode(buf, recipe.input());
                ItemStack.STREAM_CODEC.encode(buf, recipe.result());
                ChanceResult.STREAM_CODEC.encode(buf, recipe.chanceResult());
                ByteBufCodecs.FLOAT.encode(buf, recipe.experience());
                ByteBufCodecs.INT.encode(buf, recipe.time());
            }
        };

        public MapCodec<ChippingRecipe> codec() { return CODEC; }

        public StreamCodec<RegistryFriendlyByteBuf, ChippingRecipe> streamCodec() { return STREAM_CODEC; }
    }
}
