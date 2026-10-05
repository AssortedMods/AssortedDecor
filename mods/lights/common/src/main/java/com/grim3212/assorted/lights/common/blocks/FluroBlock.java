package com.grim3212.assorted.lights.common.blocks;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.core.block.ICanColor;
import net.minecraft.util.Util;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.function.Supplier;

public class FluroBlock extends Block implements ICanColor {

    public static final Map<DyeColor, Supplier<FluroBlock>> FLURO_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (map) -> {
        map.put(DyeColor.WHITE, () -> LightsBlocks.FLURO_WHITE.get());
        map.put(DyeColor.ORANGE, () -> LightsBlocks.FLURO_ORANGE.get());
        map.put(DyeColor.MAGENTA, () -> LightsBlocks.FLURO_MAGENTA.get());
        map.put(DyeColor.LIGHT_BLUE, () -> LightsBlocks.FLURO_LIGHT_BLUE.get());
        map.put(DyeColor.YELLOW, () -> LightsBlocks.FLURO_YELLOW.get());
        map.put(DyeColor.LIME, () -> LightsBlocks.FLURO_LIME.get());
        map.put(DyeColor.PINK, () -> LightsBlocks.FLURO_PINK.get());
        map.put(DyeColor.GRAY, () -> LightsBlocks.FLURO_GRAY.get());
        map.put(DyeColor.LIGHT_GRAY, () -> LightsBlocks.FLURO_LIGHT_GRAY.get());
        map.put(DyeColor.CYAN, () -> LightsBlocks.FLURO_CYAN.get());
        map.put(DyeColor.PURPLE, () -> LightsBlocks.FLURO_PURPLE.get());
        map.put(DyeColor.BLUE, () -> LightsBlocks.FLURO_BLUE.get());
        map.put(DyeColor.BROWN, () -> LightsBlocks.FLURO_BROWN.get());
        map.put(DyeColor.GREEN, () -> LightsBlocks.FLURO_GREEN.get());
        map.put(DyeColor.RED, () -> LightsBlocks.FLURO_RED.get());
        map.put(DyeColor.BLACK, () -> LightsBlocks.FLURO_BLACK.get());
    });

    private final DyeColor color;

    public FluroBlock(DyeColor color, Properties props) {
        super(props.mapColor(color));
        this.color = color;
    }

    public DyeColor getColor() {
        return color;
    }

    @Override
    public DyeColor currentColor(BlockState state) {
        return color;
    }

    @Override
    public BlockState stateForColor(BlockState state, DyeColor color) {
        return FLURO_BY_DYE.get(color).get().defaultBlockState();
    }

}
