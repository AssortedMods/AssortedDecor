package com.grim3212.assorted.displays.common.blocks;

import com.grim3212.assorted.displays.common.blocks.blockentity.DisplayCaseBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.OptionalInt;

/**
 * A glass case showing items on shelves. No screen: the slot comes from where the player is
 * looking, the way a chiseled bookshelf picks a book.
 */
public class DisplayCaseBlock extends Block implements EntityBlock {

    public static final MapCodec<DisplayCaseBlock> CODEC = simpleCodec(DisplayCaseBlock::new);
    public static final EnumProperty<Direction> FACING = HorizontalDirectionalBlock.FACING;

    /** Items to a side. The storage stays three by three at every size, so indices are stable. */
    public static final IntegerProperty SIZE = IntegerProperty.create("size", 1, 3);
    public static final int MAX_SIZE = 3;
    public static final int SLOTS = MAX_SIZE * MAX_SIZE;

    /** The lip the bottom shelf sits on, and the height the shelves above it share out. */
    private static final double FLOOR = 1.0D / 16.0D;
    private static final double STACK = 12.0D / 16.0D;

    public DisplayCaseBlock(Properties props) {
        super(props);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(SIZE, 1));
    }

    @Override
    protected MapCodec<? extends DisplayCaseBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, SIZE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite()).setValue(SIZE, 1);
    }

    @Override
    protected BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, Mirror mirror) {
        return this.rotate(state, mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DisplayCaseBlockEntity(pos, state);
    }

    /** Overridden by the museum case, whose glass half sits above the half holding the items. */
    protected BlockPos casePos(BlockState state, BlockPos pos) {
        return pos;
    }

    @Nullable
    protected DisplayCaseBlockEntity getDisplayCase(BlockState state, Level level, BlockPos pos) {
        return level.getBlockEntity(this.casePos(state, pos)) instanceof DisplayCaseBlockEntity displayCase ? displayCase : null;
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        DisplayCaseBlockEntity displayCase = this.getDisplayCase(state, level, pos);
        if (displayCase == null) {
            return InteractionResult.PASS;
        }

        OptionalInt slot = this.getHitSlot(state, pos, player, hit);
        if (slot.isEmpty()) {
            return InteractionResult.PASS;
        }

        // An occupied slot, or an empty hand, both mean take the item back out.
        if (stack.isEmpty() || !displayCase.getItem(slot.getAsInt()).isEmpty()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (!level.isClientSide()) {
            displayCase.setItem(slot.getAsInt(), stack.consumeAndReturn(1, player));
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        DisplayCaseBlockEntity displayCase = this.getDisplayCase(state, level, pos);
        if (displayCase == null) {
            return InteractionResult.PASS;
        }

        OptionalInt slot = this.getHitSlot(state, pos, player, hit);
        if (slot.isEmpty()) {
            return InteractionResult.PASS;
        }

        if (displayCase.getItem(slot.getAsInt()).isEmpty()) {
            return InteractionResult.CONSUME;
        }

        if (!level.isClientSide()) {
            ItemStack taken = displayCase.removeItem(slot.getAsInt());
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!player.getInventory().add(taken)) {
                player.drop(taken, false);
            }
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * The slot the look enters first. Splitting the hit face into thirds instead answers the middle
     * row for a back row item: the rows differ in height as well as depth.
     */
    public OptionalInt getHitSlot(BlockState state, BlockPos pos, Player player, BlockHitResult hit) {
        Vec3 eye = player.getEyePosition();
        Vec3 aim = hit.getLocation();
        Vec3 along = aim.subtract(eye);
        if (along.lengthSqr() < 1.0E-7D) {
            return OptionalInt.empty();
        }

        // Past the face hit, far enough to cross the case behind it.
        Vec3 end = aim.add(along.normalize().scale(2.0D));

        int size = state.getValue(SIZE);
        int nearest = -1;
        double nearestDistance = Double.MAX_VALUE;
        for (int slot = 0; slot < SLOTS; slot++) {
            if (!isShown(size, slot)) {
                continue;
            }

            Optional<Vec3> entry = this.slotBox(state, pos, slot).clip(eye, end);
            if (entry.isEmpty()) {
                continue;
            }

            double distance = entry.get().distanceToSqr(eye);
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = slot;
            }
        }

        return nearest < 0 ? OptionalInt.empty() : OptionalInt.of(nearest);
    }

    /** One slot's space, shelf to shelf. The back row takes the rest, so the boxes leave no gap. */
    protected AABB slotBox(BlockState state, BlockPos pos, int slot) {
        Direction facing = state.getValue(FACING);
        int size = state.getValue(SIZE);
        int column = column(slot);
        int row = row(slot);

        double nearAcross = (double) column / size;
        double farAcross = (double) (column + 1) / size;
        double nearDepth = (double) row / size;
        double farDepth = (double) (row + 1) / size;

        double x1 = localX(facing, nearAcross, nearDepth);
        double x2 = localX(facing, farAcross, farDepth);
        double z1 = localZ(facing, nearAcross, nearDepth);
        double z2 = localZ(facing, farAcross, farDepth);

        return new AABB(pos.getX() + Math.min(x1, x2), pos.getY() + shelfY(size, row), pos.getZ() + Math.min(z1, z2),
                pos.getX() + Math.max(x1, x2), pos.getY() + (row + 1 < size ? shelfY(size, row + 1) : 1.0D), pos.getZ() + Math.max(z1, z2));
    }

    public static int column(int slot) {
        return slot % MAX_SIZE;
    }

    public static int row(int slot) {
        return slot / MAX_SIZE;
    }

    public static boolean isShown(int size, int slot) {
        return column(slot) < size && row(slot) < size;
    }

    /** How high the shelf under {@code row} sits, in block units up from the floor of the case. */
    public static double shelfY(int size, int row) {
        return FLOOR + row * (STACK / size);
    }

    /** The gap between shelves, so a single item is drawn three times the size nine are. */
    public static float itemScale(int size) {
        return (float) (STACK / size);
    }

    /**
     * {@code across} left to right, {@code depth} front to back, into block coordinates. The
     * renderer places items with these too, which is what keeps drawn and clicked in step.
     */
    public static double localX(Direction facing, double across, double depth) {
        return switch (facing) {
            case SOUTH -> across;
            case NORTH -> 1.0D - across;
            case WEST -> depth;
            default -> 1.0D - depth;
        };
    }

    public static double localZ(Direction facing, double across, double depth) {
        return switch (facing) {
            case SOUTH -> 1.0D - depth;
            case NORTH -> depth;
            case WEST -> across;
            default -> 1.0D - across;
        };
    }

    /**
     * Keeps the contents when one case becomes another - copper oxidising, waxing, scraping. Without
     * it {@link DisplayCaseBlockEntity#preRemoveSideEffects} empties the case at every step.
     */
    @Override
    protected boolean shouldChangedStateKeepBlockEntity(BlockState oldState) {
        return oldState.getBlock() instanceof DisplayCaseBlock;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (stack.has(DataComponents.CUSTOM_NAME) && level.getBlockEntity(pos) instanceof DisplayCaseBlockEntity displayCase) {
            displayCase.setCustomName(stack.getHoverName());
        }
    }

    /**
     * {@code onRemove} split in two: the block entity is already gone by the time this runs, so
     * dropping what was on show moved onto {@link DisplayCaseBlockEntity#preRemoveSideEffects}.
     */
    @Override
    protected void affectNeighborsAfterRemoval(BlockState state, ServerLevel level, BlockPos pos, boolean movedByPiston) {
        level.updateNeighbourForOutputSignal(pos, this);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    /** Against the slots shown, not all nine, or a full single case would read one. */
    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        DisplayCaseBlockEntity displayCase = this.getDisplayCase(state, level, pos);
        if (displayCase == null) {
            return 0;
        }

        int size = state.getValue(SIZE);
        int shown = 0;
        int filled = 0;
        for (int slot = 0; slot < SLOTS; slot++) {
            if (isShown(size, slot)) {
                shown++;
                if (!displayCase.getItem(slot).isEmpty()) {
                    filled++;
                }
            }
        }

        return filled == 0 ? 0 : Mth.floor((float) filled / shown * 14.0F) + 1;
    }

    /** Next size round, dropping whatever the smaller case can no longer show. */
    public BlockState resize(BlockState state, Level level, BlockPos pos) {
        int size = state.getValue(SIZE) % MAX_SIZE + 1;
        BlockState resized = state.setValue(SIZE, size);
        level.setBlockAndUpdate(pos, resized);

        if (level.getBlockEntity(this.casePos(resized, pos)) instanceof DisplayCaseBlockEntity displayCase) {
            displayCase.dropSlotsBeyond(size);
        }

        return resized;
    }
}
