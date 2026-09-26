package com.grim3212.assorted.buildingblocks.common.blocks;

import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

/**
 * Vanilla's DoorBlock constructor is protected, so the plain iron style doors this mod adds need a
 * public entry point of their own
 */
public class BuildingBlocksDoorBlock extends DoorBlock {

    public BuildingBlocksDoorBlock(Properties props) {
        super(BlockSetType.IRON, props);
    }
}
