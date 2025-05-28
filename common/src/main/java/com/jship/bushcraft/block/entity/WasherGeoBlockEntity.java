package com.jship.bushcraft.block.entity;

import org.jetbrains.annotations.Nullable;

import com.jship.bushcraft.block.WasherBlock;
import com.jship.bushcraft.init.ModBlockEntities;
import com.jship.bushcraft.init.ModRecipes;
import com.jship.bushcraft.menu.WasherMenu;
import com.jship.bushcraft.mixin.AbstractFurnaceBlockEntityAccessor;
import com.jship.spiritapi.api.fluid.SpiritFluidStorage;
import com.jship.spiritapi.api.fluid.SpiritFluidStorageProvider;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager.ControllerRegistrar;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

@Slf4j
public class WasherGeoBlockEntity extends AbstractFurnaceBlockEntity implements GeoBlockEntity, SpiritFluidStorageProvider {

    public static final String ANIM_NAME = "washer_animation";
    public static final RawAnimation IDLE_ANIM = RawAnimation.begin().thenPlayAndHold("animation.model.idle");
    public static final RawAnimation WORKING_ANIM = RawAnimation.begin().thenPlay("animation.model.working");

    public static final double SECONDS_PER_CYCLE = 3.0d;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    public final SpiritFluidStorage fluidStorage = SpiritFluidStorage.create(
            FluidStack.bucketAmount(),
            FluidStack.bucketAmount(),
            () -> {
                this.setChanged();
            },
            (fluid) -> fluid.isFluidEqual(FluidStack.create(Fluids.WATER, 1L)));

    public WasherGeoBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.WASHER.get(), pos, blockState, ModRecipes.WASHING.get());
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, WasherGeoBlockEntity blockEntity) {
        if (blockEntity.fluidStorage.getFluidInTank(0).getAmount() < FluidStack.bucketAmount() / (Platform.isFabric() ? 9 : 10)) {
            val litTime = ((AbstractFurnaceBlockEntityAccessor)blockEntity).getLitTime();
            if (litTime > 0) {
                ((AbstractFurnaceBlockEntityAccessor)blockEntity).setLitTime(litTime - 1);
            } else {
                level.setBlock(pos, state.setValue(WasherBlock.LIT, false), 3);
            }
            return;
        }
        
        AbstractFurnaceBlockEntity.serverTick(level, pos, state, blockEntity);
    }

    @Override
    public void setRecipeUsed(@Nullable RecipeHolder<?> recipe) {
        if (recipe != null) {
            super.setRecipeUsed(recipe);
            this.fluidStorage.drain(FluidStack.bucketAmount() / (Platform.isFabric() ? 9 : 10), false);
        }
    }

    protected void setItems(NonNullList<ItemStack> items) {
        super.setItems(items);
        setChanged();
    }

    public void setItem(int slot, ItemStack stack) {
        super.setItem(slot, stack);
        setChanged();
    }

    @Override
    public void registerControllers(ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, ANIM_NAME, (anim) -> anim.setAndContinue(IDLE_ANIM))
                .triggerableAnim("wash", WORKING_ANIM).setAnimationSpeed(1.0d / SECONDS_PER_CYCLE));
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
        this.fluidStorage.deserializeNbt(registryLookup, nbt);
    }

    @Override
    public void saveAdditional(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        super.saveAdditional(nbt, registryLookup);
        nbt.merge(fluidStorage.serializeNbt(registryLookup));
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
        return nbt;
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new WasherMenu(id, inventory, this, this.dataAccess);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.bushcraft.washer");
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (!level.isClientSide())
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
    }

    @Override
    public SpiritFluidStorage getFluidStorage(Direction face) {
        return fluidStorage;
    }
}
