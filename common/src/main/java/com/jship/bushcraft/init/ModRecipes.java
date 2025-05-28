package com.jship.bushcraft.init;

import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.recipe.ChippingRecipe;
import com.jship.bushcraft.recipe.CoolingRecipe;
import com.jship.bushcraft.recipe.DryingRecipe;
import com.jship.bushcraft.recipe.MeltingRecipe;
import com.jship.bushcraft.recipe.WashingRecipe;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;

public class ModRecipes {
    public static final Registrar<RecipeType<?>> RECIPES = Bushcraft.MANAGER.get().get(Registries.RECIPE_TYPE);
    public static final Registrar<RecipeSerializer<?>> RECIPE_SERIALIZERS = Bushcraft.MANAGER.get()
            .get(Registries.RECIPE_SERIALIZER);

    public static final RegistrySupplier<RecipeType<ChippingRecipe>> CHIPPING = RECIPES.register(Bushcraft.id("chipping"),
            () -> new RecipeType<ChippingRecipe>() {
                @Override
                public String toString() {
                    return Bushcraft.MOD_ID + ":chipping";
                }
            });
    public static final RegistrySupplier<ChippingRecipe.Serializer> CHIPPING_SERIALIZER = RECIPE_SERIALIZERS
            .register(Bushcraft.id("chipping"), ChippingRecipe.Serializer::new);

    public static final RegistrySupplier<RecipeType<CoolingRecipe>> COOLING = RECIPES.register(Bushcraft.id("cooling"),
            () -> new RecipeType<CoolingRecipe>() {
                @Override
                public String toString() {
                    return Bushcraft.MOD_ID + ":cooling";
                }
            });
    public static final RegistrySupplier<RecipeSerializer<CoolingRecipe>> COOLING_SERIALIZER = RECIPE_SERIALIZERS
            .register(Bushcraft.id("cooling"), CoolingRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeType<DryingRecipe>> DRYING = RECIPES.register(Bushcraft.id("drying"),
            () -> new RecipeType<DryingRecipe>() {
                @Override
                public String toString() {
                    return Bushcraft.MOD_ID + ":drying";
                }
            });
    public static final RegistrySupplier<RecipeSerializer<DryingRecipe>> DRYING_SERIALIZER = RECIPE_SERIALIZERS
            .register(Bushcraft.id("drying"), DryingRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeType<MeltingRecipe>> MELTING = RECIPES.register(Bushcraft.id("melting"),
            () -> new RecipeType<MeltingRecipe>() {
                @Override
                public String toString() {
                    return Bushcraft.MOD_ID + ":melting";
                }
            });
    public static final RegistrySupplier<RecipeSerializer<MeltingRecipe>> MELTING_SERIALIZER = RECIPE_SERIALIZERS
            .register(Bushcraft.id("melting"), MeltingRecipe.Serializer::new);
    public static final RegistrySupplier<RecipeType<WashingRecipe>> WASHING = RECIPES.register(Bushcraft.id("washing"),
            () -> new RecipeType<WashingRecipe>() {
                @Override
                public String toString() {
                    return Bushcraft.MOD_ID + ":washing";
                }
            });
    public static final RegistrySupplier<SimpleCookingSerializer<WashingRecipe>> WASHING_SERIALIZER = RECIPE_SERIALIZERS
            .register(Bushcraft.id("washing"), () -> new SimpleCookingSerializer<WashingRecipe>(
                    WashingRecipe::new, 200));

    public static void init() {
    }
}
