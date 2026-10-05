package com.grim3212.assorted.roads.common.blocks;

import com.grim3212.assorted.lib.core.block.ICanColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class RoadwayBlock extends Block implements ICanColor {

	public RoadwayBlock(Properties props) {
		super(props);
	}

	@Override
	public DyeColor currentColor(BlockState state) {
		return null;
	}

	@Override
	public BlockState stateForColor(BlockState state, DyeColor color) {
		return RoadsBlocks.ROADWAY_COLORS.get(color).get().defaultBlockState();
	}

}
