package com.grim3212.assorted.decorations.gametest;

import com.grim3212.assorted.decorations.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.blocks.FountainBlock;
import com.grim3212.assorted.decorations.common.blocks.PlanterPotBlock;
import com.grim3212.assorted.lib.test.TestSupport;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** What a right click and redstone do to the decorations, as the site describes them. */
final class InteractionTests {

    private static final BlockPos AT = new BlockPos(2, 1, 2);

    private InteractionTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("decorations_cycle_their_design_on_a_right_click", InteractionTests::decorationsCycleTheirDesignOnARightClick);
        out.accept("planter_pot_top_cycles_with_an_empty_hand", InteractionTests::planterPotTopCyclesWithAnEmptyHand);
        out.accept("fountain_runs_only_while_powered", InteractionTests::fountainRunsOnlyWhilePowered);
    }

    private static void decorationsCycleTheirDesignOnARightClick(GameTestHelper helper) {
        ServerPlayer player = TestSupport.survivalPlayer(helper);
        helper.setBlock(AT, DecorationsBlocks.BONE_DECORATION.get());
        helper.setBlock(AT.east(2), DecorationsBlocks.CLAY_DECORATION.get());

        TestSupport.rightClick(player, helper.getLevel(), ItemStack.EMPTY, helper.absolutePos(AT));
        TestSupport.rightClick(player, helper.getLevel(), ItemStack.EMPTY, helper.absolutePos(AT.east(2)));
        helper.assertValueEqual(helper.getBlockState(AT).getValue(BoneDecorationBlock.DECORATION), 1, "the bone decoration's design after a right click");
        helper.assertValueEqual(helper.getBlockState(AT.east(2)).getValue(ClayDecorationBlock.DECORATION), 1, "the clay decoration's design after a right click");
        helper.succeed();
    }

    /** Seven tops, so the eighth click comes back round to the first. */
    private static void planterPotTopCyclesWithAnEmptyHand(GameTestHelper helper) {
        ServerPlayer player = TestSupport.survivalPlayer(helper);
        helper.setBlock(AT, DecorationsBlocks.PLANTER_POT.get());
        for (int click = 1; click <= 7; click++) {
            TestSupport.rightClick(player, helper.getLevel(), ItemStack.EMPTY, helper.absolutePos(AT));
            helper.assertValueEqual(helper.getBlockState(AT).getValue(PlanterPotBlock.TOP), click % 7, "the planter pot's top after " + click + " clicks");
        }
        helper.succeed();
    }

    private static void fountainRunsOnlyWhilePowered(GameTestHelper helper) {
        helper.setBlock(AT, DecorationsBlocks.FOUNTAIN.get());
        helper.assertFalse(helper.getBlockState(AT).getValue(FountainBlock.ACTIVE), "an unpowered fountain is running");

        helper.setBlock(AT.east(), Blocks.REDSTONE_BLOCK);
        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(helper.getBlockState(AT).getValue(FountainBlock.ACTIVE), "a powered fountain did not start"))
                .thenExecute(() -> helper.setBlock(AT.east(), Blocks.AIR))
                .thenWaitUntil(() -> helper.assertFalse(helper.getBlockState(AT).getValue(FountainBlock.ACTIVE), "the fountain kept running once its power was gone"))
                .thenSucceed();
    }
}
