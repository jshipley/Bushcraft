package com.jship.bushcraft.mixin;

import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface AbstractFurnaceBlockEntityAccessor {
    @Accessor("cookingProgress")
    public void setCookingProgress(int cookingProgress);

    @Accessor
    public int getCookingProgress();

    @Accessor("cookingTotalTime")
    public void setCookingTotalTime(int cookingTotalTime);

    @Accessor("cookingTotalTime")
    public int getCookingTotalTime();

    @Invoker("getTotalCookTime")
    public static int invokeGetTotalCookTime(Level level, AbstractFurnaceBlockEntity blockEntity) {
        throw new AssertionError();
    }

    @Invoker("isLit")
    public boolean invokeIsLit();

    @Accessor("litTime")
    public void setLitTime(int litTime);

    @Accessor("litTime")
    public int getLitTime();

    @Accessor("litDuration")
    public void setLitDuration(int litDuration);

    @Accessor("litDuration")
    public int getLitDuration();

    @Accessor("quickCheck")
    public RecipeManager.CachedCheck<SingleRecipeInput, ? extends AbstractCookingRecipe> getQuickCheck();
}
