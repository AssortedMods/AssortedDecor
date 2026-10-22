package com.grim3212.assorted.lights.gametest;

import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.hitSide;

/** Tubes and plates go on every side of a block, underneath included, and a plate can sit in water. */
final class PlacementTests {

    private static final BlockPos BLOCK = new BlockPos(4, 3, 4);

    private PlacementTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("illumination_lights_place_on_every_side", PlacementTests::illuminationLightsPlaceOnEverySide);
        out.accept("illumination_plate_sits_in_water", PlacementTests::illuminationPlateSitsInWater);
    }

    private static void illuminationLightsPlaceOnEverySide(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (Block light : new Block[]{LightsBlocks.ILLUMINATION_TUBE.get(), LightsBlocks.ILLUMINATION_PLATE.get()}) {
            for (Direction face : Direction.values()) {
                helper.setBlock(BLOCK, Blocks.STONE);
                helper.setBlock(BLOCK.relative(face), Blocks.AIR);
                ItemStack stack = new ItemStack(light);
                player.setItemInHand(InteractionHand.MAIN_HAND, stack);
                stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(BLOCK), face)));
                helper.assertBlockPresent(light, BLOCK.relative(face));
                helper.assertBlockProperty(BLOCK.relative(face), BlockStateProperties.FACING, face);
                helper.setBlock(BLOCK.relative(face), Blocks.AIR);
            }
        }
        helper.succeed();
    }

    private static void illuminationPlateSitsInWater(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(BLOCK, Blocks.STONE);
        helper.setBlock(BLOCK.above(), Blocks.WATER);
        ItemStack stack = new ItemStack(LightsBlocks.ILLUMINATION_PLATE.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(BLOCK), Direction.UP)));
        helper.assertBlockPresent(LightsBlocks.ILLUMINATION_PLATE.get(), BLOCK.above());
        helper.assertBlockProperty(BLOCK.above(), BlockStateProperties.WATERLOGGED, true);
        helper.succeed();
    }
}
