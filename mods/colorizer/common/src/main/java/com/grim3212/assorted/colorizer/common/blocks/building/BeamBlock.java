package com.grim3212.assorted.colorizer.common.blocks.building;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Map;

/**
 * A beam along the top or bottom of the block, running the way the placer looked. A beam running into
 * its side grows an arm to meet it, so beams cross and tee without a block of their own.
 */
public class BeamBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;
    public static final EnumProperty<Half> HALF = BlockStateProperties.HALF;
    public static final BooleanProperty NORTH = BlockStateProperties.NORTH;
    public static final BooleanProperty EAST = BlockStateProperties.EAST;
    public static final BooleanProperty SOUTH = BlockStateProperties.SOUTH;
    public static final BooleanProperty WEST = BlockStateProperties.WEST;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Map<Direction, BooleanProperty> ARMS = Map.of(Direction.NORTH, NORTH, Direction.EAST, EAST, Direction.SOUTH, SOUTH, Direction.WEST, WEST);

    private static final Map<Direction.Axis, VoxelShape> TOP_CORE = Shapes.rotateHorizontalAxis(Block.box(5, 10, 0, 11, 16, 16));
    private static final Map<Direction, VoxelShape> TOP_ARM = Shapes.rotateHorizontal(Block.box(5, 10, 0, 11, 16, 5));
    private static final Map<Direction.Axis, VoxelShape> BOTTOM_CORE = Shapes.rotateHorizontalAxis(Block.box(5, 0, 0, 11, 6, 16));
    private static final Map<Direction, VoxelShape> BOTTOM_ARM = Shapes.rotateHorizontal(Block.box(5, 0, 0, 11, 6, 5));

    public BeamBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Z).setValue(HALF, Half.TOP)
                .setValue(NORTH, false).setValue(EAST, false).setValue(SOUTH, false).setValue(WEST, false).setValue(WATERLOGGED, false));
    }

    public static BooleanProperty arm(Direction side) {
        return ARMS.get(side);
    }

    /** Whether a beam reaches out through {@code side}: along its own run always, across only where joined. */
    public static boolean reaches(BlockState state, Direction side) {
        return state.getBlock() instanceof BeamBlock && (side.getAxis() == state.getValue(AXIS) || state.getValue(ARMS.get(side)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, HALF, NORTH, EAST, SOUTH, WEST, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        boolean top = face == Direction.DOWN || (face != Direction.UP && context.getClickLocation().y - context.getClickedPos().getY() > 0.5);
        BlockState state = this.defaultBlockState().setValue(AXIS, context.getHorizontalDirection().getAxis()).setValue(HALF, top ? Half.TOP : Half.BOTTOM)
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
        for (Direction side : Direction.Plane.HORIZONTAL) {
            state = joined(state, side, context.getLevel().getBlockState(context.getClickedPos().relative(side)));
        }
        return state;
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return direction.getAxis().isHorizontal() ? joined(state, direction, neighbourState) : state;
    }

    // Two beams laid side by side stay apart: an arm only grows to meet a beam already pointing at this one.
    private static BlockState joined(BlockState state, Direction side, BlockState neighbour) {
        if (side.getAxis() == state.getValue(AXIS)) {
            return state;
        }
        boolean meets = neighbour.getBlock() instanceof BeamBlock && neighbour.getValue(HALF) == state.getValue(HALF) && reaches(neighbour, side.getOpposite());
        return state.setValue(ARMS.get(side), meets);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        boolean top = state.getValue(HALF) == Half.TOP;
        VoxelShape shape = (top ? TOP_CORE : BOTTOM_CORE).get(state.getValue(AXIS));
        for (Direction side : Direction.Plane.HORIZONTAL) {
            if (state.getValue(ARMS.get(side))) {
                shape = Shapes.or(shape, (top ? TOP_ARM : BOTTOM_ARM).get(side));
            }
        }
        return shape;
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        BlockState rotated = state;
        if (rotation == Rotation.CLOCKWISE_90 || rotation == Rotation.COUNTERCLOCKWISE_90) {
            rotated = rotated.setValue(AXIS, state.getValue(AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
        }
        for (Direction side : Direction.Plane.HORIZONTAL) {
            rotated = rotated.setValue(ARMS.get(rotation.rotate(side)), state.getValue(ARMS.get(side)));
        }
        return rotated;
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        BlockState mirrored = state;
        for (Direction side : Direction.Plane.HORIZONTAL) {
            mirrored = mirrored.setValue(ARMS.get(mirror.mirror(side)), state.getValue(ARMS.get(side)));
        }
        return mirrored;
    }
}
