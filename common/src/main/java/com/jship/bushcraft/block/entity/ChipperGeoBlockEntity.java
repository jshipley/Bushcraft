package com.jship.bushcraft.block.entity;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.menu.ChipperMenu;
import com.jship.bushcraft.mixin.AbstractFurnaceBlockEntityAccessor;
import com.jship.bushcraft.recipe.ChippingRecipe;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@Slf4j
public class ChipperGeoBlockEntity extends AbstractFurnaceBlockEntity implements GeoBlockEntity {

    public static final String ANIM_NAME = "chipper_animation";
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("animation.model.idle");
    public static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenLoop("animation.model.working");

    public static final double SECONDS_PER_CYCLE = 2.0d;

    public float progress = 0f;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public ChipperGeoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CHIPPER.get(), pos, blockState, ModRecipes.CHIPPING.get());
    }

    // public static void serverTick(Level level, BlockPos pos, BlockState state, ChipperGeoBlockEntity blockEntity) {        
    //     AbstractFurnaceBlockEntity.serverTick(level, pos, state, blockEntity);
    //     if (Math.abs(blockEntity.progress - blockEntity.getProgress()) > 0.001) {
    //         blockEntity.progress = blockEntity.getProgress();
    //         blockEntity.setChanged();
    //     }
    // }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ChipperGeoBlockEntity blockEntity) {
      val entityAccessor = ((AbstractFurnaceBlockEntityAccessor)blockEntity);
      boolean bl = entityAccessor.invokeIsLit();
      boolean bl2 = false;
      if (entityAccessor.invokeIsLit()) {
         entityAccessor.setLitTime(entityAccessor.getLitTime() - 1);
      }

      ItemStack itemStack = (ItemStack)blockEntity.items.get(1);
      ItemStack itemStack2 = (ItemStack)blockEntity.items.get(0);
      boolean bl3 = !itemStack2.isEmpty();
      boolean bl4 = !itemStack.isEmpty();
      if (entityAccessor.invokeIsLit() || bl4 && bl3) {
         RecipeHolder<? extends AbstractCookingRecipe> recipeHolder;
         if (bl3) {
            recipeHolder = entityAccessor.getQuickCheck().getRecipeFor(new SingleRecipeInput(itemStack2), level).orElse(null);
         } else {
            recipeHolder = null;
         }

         int i = blockEntity.getMaxStackSize();
         if (!entityAccessor.invokeIsLit() && canBurn(level.registryAccess(), recipeHolder, blockEntity.items, i)) {
            entityAccessor.setLitTime(blockEntity.getBurnDuration(itemStack));
            entityAccessor.setLitDuration(entityAccessor.getLitTime());
            if (entityAccessor.invokeIsLit()) {
               bl2 = true;
               if (bl4) {
                  Item item = itemStack.getItem();
                  itemStack.shrink(1);
                  if (itemStack.isEmpty()) {
                     Item item2 = item.getCraftingRemainingItem();
                     blockEntity.items.set(1, item2 == null ? ItemStack.EMPTY : new ItemStack(item2));
                  }
               }
            }
         }

         if (entityAccessor.invokeIsLit() && canBurn(level.registryAccess(), recipeHolder, blockEntity.items, i)) {
            cookingProgress(blockEntity, entityAccessor.getCookingProgress() + 1);
            if (entityAccessor.getCookingProgress() == entityAccessor.getCookingTotalTime()) {
               cookingProgress(blockEntity, 0);
               entityAccessor.setCookingTotalTime(AbstractFurnaceBlockEntityAccessor.invokeGetTotalCookTime(level, blockEntity));
               if (burn(level.registryAccess(), recipeHolder, blockEntity.items, i)) {
                  blockEntity.setRecipeUsed(recipeHolder);
               }

               bl2 = true;
            }
         } else {
            cookingProgress(blockEntity, 0);
         }
      } else if (!entityAccessor.invokeIsLit() && entityAccessor.getCookingProgress() > 0) {
         cookingProgress(blockEntity, Mth.clamp(entityAccessor.getCookingProgress() - 2, 0, entityAccessor.getCookingTotalTime()));
      }

      if (bl != entityAccessor.invokeIsLit()) {
         bl2 = true;
         state = (BlockState)state.setValue(AbstractFurnaceBlock.LIT, entityAccessor.invokeIsLit());
         level.setBlock(pos, state, 3);
      }

      if (bl2) {
         setChanged(level, pos, state);
      }

   }

   private static void cookingProgress(ChipperGeoBlockEntity blockEntity, int progress) {
      ((AbstractFurnaceBlockEntityAccessor)blockEntity).setCookingProgress(progress);
      blockEntity.progress = blockEntity.getProgress();
      blockEntity.setChanged();
   }

   private static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize) {
      if (!(inventory.get(0)).isEmpty() && recipe != null) {
         ItemStack resultStack = recipe.value().getResultItem(registryAccess);
         if (resultStack.isEmpty()) {
            return false;
         } else {
            ItemStack outputStack = inventory.get(2);
            if (outputStack.isEmpty()) {
               return true;
            } else if (!ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
               return false;
            } else if (outputStack.getCount() + resultStack.getCount() < maxStackSize && outputStack.getCount() + resultStack.getCount() < outputStack.getMaxStackSize()) {
               return true;
            } else {
               return outputStack.getCount() + resultStack.getCount() < resultStack.getMaxStackSize();
            }
         }
      } else {
         return false;
      }
   }

   private static boolean burn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> inventory, int maxStackSize) {
      if (recipe != null && canBurn(registryAccess, recipe, inventory, maxStackSize)) {
         ItemStack inputStack = (ItemStack)inventory.get(0);
         ItemStack resultStack = recipe.value().getResultItem(registryAccess);
         ItemStack outputStack = (ItemStack)inventory.get(2);
         if (outputStack.isEmpty()) {
            inventory.set(2, resultStack.copy());
         } else if (ItemStack.isSameItemSameComponents(outputStack, resultStack)) {
            outputStack.grow(resultStack.getCount());
         }

         if (inputStack.is(Blocks.WET_SPONGE.asItem()) && !(inventory.get(1)).isEmpty() && (inventory.get(1)).is(Items.BUCKET)) {
            inventory.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         inputStack.shrink(1);
         return true;
      } else {
         return false;
      }
   }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
        setChanged();
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        setChanged();
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, ANIM_NAME, (anim) -> anim.setAndContinue(IDLE_ANIM))
                .triggerableAnim("chip", WORKING_ANIM).setAnimationSpeed(1.0d / SECONDS_PER_CYCLE));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput dataComponentInput) {
        super.applyImplicitComponents(dataComponentInput);
        dataComponentInput.getOrDefault(DataComponents.CONTAINER, ItemContainerContents.EMPTY).copyInto(items);
    }

    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        builder.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
    }

    @Override
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.loadAdditional(nbt, registryLookup);
        if (nbt.contains("Progress")) {
            this.progress = nbt.getFloat("Progress");
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.putFloat("Progress", this.getProgress());
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registryLookup) {
        CompoundTag nbt = super.getUpdateTag(registryLookup);
        saveAdditional(nbt, registryLookup);
        ContainerHelper.saveAllItems(nbt, items, true, registryLookup);
        nbt.putFloat("Progress", this.getProgress());
        return nbt;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new ChipperMenu(id, inventory, this, this.dataAccess);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.bushcraft.chipper");
    }

    public float getProgress() {
        val progress = ((AbstractFurnaceBlockEntityAccessor)this).getCookingProgress();
        val time = ((AbstractFurnaceBlockEntityAccessor)this).getCookingTotalTime();

        return time > 0 ? (float)progress / (float)time : 0f;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!this.getLevel().isClientSide())
            this.getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }
}
