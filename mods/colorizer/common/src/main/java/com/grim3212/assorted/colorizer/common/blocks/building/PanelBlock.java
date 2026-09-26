package com.grim3212.assorted.colorizer.common.blocks.building;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

/**
 * A one pixel board lining any face of the block, as glow lichen does: floor, ceiling or wall. Using
 * another on it lines a second face, so one block can be a corner, a ceiling and walls at once.
 */
public class PanelBlock extends MultifaceBlock {

    public PanelBlock(Properties props) {
        super(props);
    }

    // A fresh panel lines the face that was clicked; one added to a panel goes where the player is looking.
    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState existing = context.getLevel().getBlockState(context.getClickedPos());
        if (!existing.is(this)) {
            BlockState placed = this.getStateForPlacement(existing, context.getLevel(), context.getClickedPos(), context.getClickedFace().getOpposite());
            if (placed != null) {
                return placed;
            }
        }
        return super.getStateForPlacement(context);
    }

    // Free standing, unlike lichen: a panel needs nothing behind it.
    @Override
    public boolean isValidStateForPlacement(BlockGetter level, BlockState oldState, BlockPos placementPos, Direction placementDirection) {
        return !oldState.is(this) || !hasFace(oldState, placementDirection);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return hasAnyFace(state);
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return state;
    }
}
