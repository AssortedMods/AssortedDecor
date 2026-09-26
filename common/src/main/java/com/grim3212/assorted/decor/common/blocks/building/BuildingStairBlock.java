package com.grim3212.assorted.decor.common.blocks.building;

import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;

/** Vanilla's StairBlock constructor is protected; only NeoForge's patch opens it, so common needs its own. */
public class BuildingStairBlock extends StairBlock {

    public BuildingStairBlock(BlockState base, Properties props) {
        super(base, props);
    }
}
