package com.jship.bushcraft.compat.jade;

import com.jship.bushcraft.block.entity.CrucibleBlockEntity;
import com.jship.bushcraft.block.entity.CrucibleBlockEntity.ThermalMode;
import com.jship.bushcraft.init.ModBlocks;

import lombok.val;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.StreamServerDataProvider;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.theme.IThemeHelper;
import snownee.jade.api.ui.BoxStyle;
import snownee.jade.api.ui.IElementHelper;

public enum BushcraftCrucibleProvider
        implements IBlockComponentProvider, StreamServerDataProvider<BlockAccessor, BushcraftCrucibleProvider.Data> {
    INSTANCE;

    @Override
    public ResourceLocation getUid() {
        return ModBlocks.CRUCIBLE.getId();
    }

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        Data data = decodeFromData(accessor).orElse(null);
        if (data == null) {
            return;
        }
        IElementHelper helper = IElementHelper.get();
        // hot #ff4500
        // cold #00bfff
        // gray #3a3a3a
        val color = data.mode == ThermalMode.MELTING ? 0xff4500 : data.mode == ThermalMode.COOLING ? 0x00bfff : 0x3a3a3a;
        if (data.total > 0 && data.progress < data.total) {
            tooltip.add(helper.progress(((float) data.progress / data.total),
                    IThemeHelper.get().seconds((data.total - data.progress), accessor.tickRate()).withColor(0xffffff),
                    helper.progressStyle().color(color), BoxStyle.getNestedBox(), true));
        }
    }

    @Override
    public Data streamData(BlockAccessor accessor) {
        val crucibleEntity = (CrucibleBlockEntity) accessor.getBlockEntity();
        if (crucibleEntity.workRate == 0)
            return new Data(crucibleEntity.workMode, 0, 0);
        return new Data(crucibleEntity.workMode, crucibleEntity.workProgress / crucibleEntity.workRate,
                crucibleEntity.workTime / crucibleEntity.workRate);
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, Data> streamCodec() {
        return Data.STREAM_CODEC;
    }

    public record Data(ThermalMode mode, int progress, int total) {
        public static final StreamCodec<RegistryFriendlyByteBuf, Data> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.idMapper(ThermalMode::of, ThermalMode::value),
                Data::mode,
                ByteBufCodecs.VAR_INT,
                Data::progress,
                ByteBufCodecs.VAR_INT,
                Data::total,
                Data::new);
    }
}
