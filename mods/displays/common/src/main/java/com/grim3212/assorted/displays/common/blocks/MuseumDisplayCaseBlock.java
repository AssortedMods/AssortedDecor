package com.grim3212.assorted.displays.common.blocks;

import com.grim3212.assorted.displays.common.blocks.blockentity.DisplayCaseBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.OptionalInt;

/**
 * A plinth with the glass case on top, two blocks tall like a door. Only the lower half has a block
 * entity; the items are drawn a block above it, within the plinth's own render box.
 */
public class MuseumDisplayCaseBlock extends DisplayCaseBlock {

    public static final MapCodec<MuseumDisplayCaseBlock> CODEC = simpleCodec(MuseumDisplayCaseBlock::new);
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

    public MuseumDisplayCaseBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected MapCodec<? extends MuseumDisplayCaseBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxY() || !level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }

        return super.getStateForPlacement(context).setValue(HALF, DoubleBlockHalf.LOWER);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        level.setBlockAndUpdate(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER));
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? super.newBlockEntity(pos, state) : null;
    }

    @Override
    protected BlockPos casePos(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        if (state.getValue(HALF) != DoubleBlockHalf.UPPER) {
            return super.canSurvive(state, level, pos);
        }

        BlockState below = level.getBlockState(pos.below());
        return below.is(this) && below.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    /** Losing one half takes the other with it, the way a door does. */
    @Override
    protected BlockState updateShape(BlockState state, LevelReader level, ScheduledTickAccess ticks, BlockPos pos, Direction directionToNeighbour, BlockPos neighbourPos, BlockState neighbourState, RandomSource random) {
        DoubleBlockHalf half = state.getValue(HALF);
        boolean towardsOtherHalf = directionToNeighbour.getAxis() == Direction.Axis.Y && (half == DoubleBlockHalf.LOWER) == (directionToNeighbour == Direction.UP);
        if (towardsOtherHalf && (!neighbourState.is(this) || neighbourState.getValue(HALF) == half)) {
            return Blocks.AIR.defaultBlockState();
        }

        return super.updateShape(state, level, ticks, pos, directionToNeighbour, neighbourPos, neighbourState, random);
    }

    /**
     * Breaking the glass half in creative would otherwise leave the plinth to drop a second case:
     * the plinth is the half the loot table pays out on, so it is cleared without drops first.
     */
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.preventsBlockDrops() && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos below = pos.below();
            BlockState belowState = level.getBlockState(below);
            if (belowState.is(this) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                level.setBlock(below, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                level.levelEvent(player, 2001, below, Block.getId(belowState));
            }
        }

        return super.playerWillDestroy(level, pos, state, player);
    }

    /** Both halves carry the size: the glass half's own picks its riser model and its hit boxes. */
    @Override
    public BlockState resize(BlockState state, Level level, BlockPos pos) {
        BlockPos plinth = this.casePos(state, pos);
        BlockState resized = super.resize(level.getBlockState(plinth), level, plinth);

        BlockPos glass = plinth.above();
        BlockState glassState = level.getBlockState(glass);
        if (glassState.is(this)) {
            level.setBlockAndUpdate(glass, glassState.setValue(SIZE, resized.getValue(SIZE)));
        }

        return resized;
    }

    /** The grid is behind the glass, so the plinth half is not part of it. */
    @Override
    public OptionalInt getHitSlot(BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? super.getHitSlot(state, pos, player, hit) : OptionalInt.empty();
    }

    /** A named tag writes the placard, an unnamed one wipes it and is not spent doing so. */
    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!stack.is(Items.NAME_TAG)) {
            return super.useItemOn(stack, state, level, pos, player, hand, hit);
        }

        DisplayCaseBlockEntity displayCase = this.getDisplayCase(state, level, pos);
        if (displayCase == null) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            if (stack.has(DataComponents.CUSTOM_NAME)) {
                displayCase.setCustomName(stack.getHoverName());
                stack.consume(1, player);
            } else {
                displayCase.setCustomName(null);
            }
            level.playSound(null, pos, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0F, 1.0F);
        }

        return InteractionResult.SUCCESS;
    }
}
