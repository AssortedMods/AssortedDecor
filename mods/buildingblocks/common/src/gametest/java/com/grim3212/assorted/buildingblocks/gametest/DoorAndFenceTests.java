package com.grim3212.assorted.buildingblocks.gametest;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.buildingblocks.gametest.BuildingBlocksTestSupport.*;
import static com.grim3212.assorted.lib.test.TestSupport.*;

/** The chain link fence and the four plain doors. */
final class DoorAndFenceTests {

    private DoorAndFenceTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("chain_link_fence_connects", DoorAndFenceTests::chainLinkFenceConnects);
        out.accept("doors_open_on_redstone", DoorAndFenceTests::doorsOpenOnRedstone);
    }

    /** The chain link fence joins up with its neighbours, and only with the sides that have one. */
    private static void chainLinkFenceConnects(GameTestHelper helper) {
        Block fence = BuildingBlocksBlocks.CHAIN_LINK_FENCE.get();

        helper.setBlock(MAIN, fence);
        for (var side : List.of(CrossCollisionBlock.NORTH, CrossCollisionBlock.EAST, CrossCollisionBlock.SOUTH, CrossCollisionBlock.WEST)) {
            helper.assertBlockProperty(MAIN, side, false);
        }

        helper.setBlock(MAIN.north(), fence);
        helper.setBlock(MAIN.east(), fence);

        helper.assertBlockProperty(MAIN, CrossCollisionBlock.NORTH, true);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.EAST, true);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.SOUTH, false);
        helper.assertBlockProperty(MAIN, CrossCollisionBlock.WEST, false);
        helper.succeed();
    }

    /**
     * The four plain doors place as two halves and open together under redstone. Not by hand:
     * {@code BuildingBlocksDoorBlock} uses {@code BlockSetType#IRON}, which cannot be opened by hand.
     */
    private static void doorsOpenOnRedstone(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        List<Block> doors = List.of(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get(), BuildingBlocksBlocks.GLASS_DOOR.get(), BuildingBlocksBlocks.QUARTZ_DOOR.get(), BuildingBlocksBlocks.STEEL_DOOR.get());

        for (int i = 0; i < doors.size(); i++) {
            Block door = doors.get(i);
            String name = BuiltInRegistries.BLOCK.getKey(door).getPath();
            BlockPos floor = new BlockPos(1 + i * 2, 1, 4);
            BlockPos lower = floor.above();
            BlockPos upper = lower.above();

            helper.setBlock(floor, Blocks.STONE);
            ItemStack stack = new ItemStack(door);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(door, lower);
            helper.assertBlockPresent(door, upper);
            helper.assertBlockProperty(lower, DoorBlock.HALF, DoubleBlockHalf.LOWER);
            helper.assertBlockProperty(upper, DoorBlock.HALF, DoubleBlockHalf.UPPER);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, false);

            helper.setBlock(lower.west(), Blocks.REDSTONE_BLOCK);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, true);
            helper.assertBlockProperty(upper, DoorBlock.OPEN, true);

            helper.setBlock(lower.west(), Blocks.AIR);
            helper.assertBlockProperty(lower, DoorBlock.OPEN, false);
            helper.assertBlockProperty(upper, DoorBlock.OPEN, false);

            helper.assertTrue(name.endsWith("door"), name + " is in the door list but is not a door");
        }

        helper.succeed();
    }
}
