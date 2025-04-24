package com.jship.bushcraft.compat.jade;

import com.google.common.collect.Lists;
import com.jship.bushcraft.Bushcraft;
import com.jship.bushcraft.block.entity.DryingRackBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

public enum BushcraftDryingRackProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView> {
    INSTANCE;

    private static final MapCodec<Integer> DRYING_TIME_CODEC = Codec.INT.fieldOf("bushcraftjade:drying");

    @Override
    public ResourceLocation getUid() {
        return Bushcraft.ModBlocks.DRYING_RACK.getId();
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(
            groups,
            stack -> {
                ItemView stackView = new ItemView(stack);
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
                if (customData.isEmpty()) {
                    return stackView;
                }
                Optional<Integer> result = customData.read(DRYING_TIME_CODEC).result();
                if (result.isEmpty()) {
                    return stackView;
                }
                String text = IThemeHelper.get().seconds(result.get(), accessor.tickRate()).getString();
                return stackView.amountText(text);
            },
            null
        );
    }

    @Override
    public @Nullable List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        if (accessor.getTarget() instanceof DryingRackBlockEntity dryingRack) {
            List<ItemStack> list = Lists.newArrayList();
            for (int i = 0; i < dryingRack.dryingTime.length; i++) {
                ItemStack stack = dryingRack.getItems().get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                int dryingTime = dryingRack.dryingTime[i] - dryingRack.dryingProgress[i];
                if (dryingTime <= 0) {
                    list.add(stack);
                    continue;
                }

                stack = stack.copy();
                CustomData customData = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).update(NbtOps.INSTANCE, DRYING_TIME_CODEC, dryingTime).getOrThrow();
                stack.set(DataComponents.CUSTOM_DATA, customData);
                list.add(stack);
            }
            return List.of(new ViewGroup<>(list));
        }
        return null;
    }
}
