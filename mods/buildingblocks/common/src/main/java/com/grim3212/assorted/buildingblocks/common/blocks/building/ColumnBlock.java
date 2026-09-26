package com.grim3212.assorted.buildingblocks.common.blocks.building;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.EnumMap;
import java.util.Map;

/**
 * A column that lays itself out: a base where nothing of its kind is below it, a capital where nothing
 * is above, and a plain shaft between. It stands along whichever axis it was placed against, as a log does.
 */
public class ColumnBlock extends Block implements SimpleWaterloggedBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;
    public static final EnumProperty<ColumnPart> PART = EnumProperty.create("part", ColumnPart.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final Map<Direction.Axis, Map<ColumnPart, VoxelShape>> SHAPES = new EnumMap<>(Direction.Axis.class);

    static {
        for (Direction.Axis axis : Direction.Axis.values()) {
            Map<ColumnPart, VoxelShape> parts = new EnumMap<>(ColumnPart.class);
            parts.put(ColumnPart.SHAFT, box(axis, 2, 14, 0, 16));
            parts.put(ColumnPart.BASE, Shapes.or(box(axis, 0, 16, 0, 6), box(axis, 2, 14, 6, 16)));
            parts.put(ColumnPart.CAPITAL, Shapes.or(box(axis, 2, 14, 0, 10), box(axis, 0, 16, 10, 16)));
            parts.put(ColumnPart.SINGLE, Shapes.or(box(axis, 0, 16, 0, 6), box(axis, 2, 14, 6, 10), box(axis, 0, 16, 10, 16)));
            SHAPES.put(axis, parts);
        }
    }

    public ColumnBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y).setValue(PART, ColumnPart.SINGLE).setValue(WATERLOGGED, false));
    }

    /** Which way along an axis the capital is: the blockstate turns the upright model to match. */
    public static Direction top(Direction.Axis axis) {
        return switch (axis) {
            case X -> Direction.EAST;
            case Y -> Direction.UP;
            case Z -> Direction.NORTH;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS, PART, WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = this.defaultBlockState().setValue(AXIS, context.getClickedFace().getAxis())
                .setValue(WATERLOGGED, context.getLevel().getFluidState(context.getClickedPos()).is(Fluids.WATER));
        return this.withPart(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction direction, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            ticks.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return direction.getAxis() == state.getValue(AXIS) ? this.withPart(state, level, pos) : state;
    }

    private BlockState withPart(BlockState state, BlockGetter level, BlockPos pos) {
        Direction top = top(state.getValue(AXIS));
        return state.setValue(PART, ColumnPart.of(this.continues(state, level.getBlockState(pos.relative(top.getOpposite()))),
                this.continues(state, level.getBlockState(pos.relative(top)))));
    }

    private boolean continues(BlockState state, BlockState neighbour) {
        return neighbour.is(this) && neighbour.getValue(AXIS) == state.getValue(AXIS);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(AXIS)).get(state.getValue(PART));
    }

    @Override
    protected FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    // A quarter turn swaps a lying column between X and Z.
    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        Direction.Axis axis = state.getValue(AXIS);
        if (axis == Direction.Axis.Y || rotation == Rotation.NONE || rotation == Rotation.CLOCKWISE_180) {
            return state;
        }
        return state.setValue(AXIS, axis == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);
    }

    /** A box {@code across} wide around the column's centre, from {@code from} to {@code to} along it towards its top. */
    private static VoxelShape box(Direction.Axis axis, double acrossMin, double acrossMax, double from, double to) {
        return switch (axis) {
            case Y -> Block.box(acrossMin, from, acrossMin, acrossMax, to, acrossMax);
            case X -> Block.box(from, acrossMin, acrossMin, to, acrossMax, acrossMax);
            case Z -> Block.box(acrossMin, acrossMin, 16 - to, acrossMax, acrossMax, 16 - from);
        };
    }
}
