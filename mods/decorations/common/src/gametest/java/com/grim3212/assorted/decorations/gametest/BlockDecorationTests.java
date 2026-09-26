package com.grim3212.assorted.decorations.gametest;

import com.grim3212.assorted.decorations.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.blocks.PlanterPotBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.decorations.gametest.DecorationsTestSupport.*;

/**
 * Decorative blocks: planter pots and the clay and bone decorations.
 */
final class BlockDecorationTests {

    private BlockDecorationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("planter_pot_holds_a_plant", BlockDecorationTests::planterPotHoldsAPlant);
        out.accept("decoration_blocks_place", BlockDecorationTests::decorationBlocksPlace);
    }

    /**
     * A planter pot sustains a plant its soil setting allows and refuses one it does not. The hook
     * is the library's {@code IPlantSustainable}, reached through a mixin on vanilla's
     * {@code VegetationBlock#mayPlaceOn} - so this is really a check that the mixin still applies.
     */
    private static void planterPotHoldsAPlant(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pot = MAIN;
        BlockPos plant = helper.absolutePos(MAIN.above());

        helper.setBlock(pot, DecorationsBlocks.PLANTER_POT.get());
        helper.assertBlockProperty(pot, PlanterPotBlock.TOP, 0);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack poppy = new ItemStack(Items.POPPY);
        player.setItemInHand(InteractionHand.MAIN_HAND, poppy);
        poppy.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(pot))));

        helper.assertBlockPresent(Blocks.POPPY, MAIN.above());
        helper.assertTrue(level.getBlockState(plant).canSurvive(level, plant), "a poppy in a planter pot did not think it could survive there");

        // Soil 2 is the bamboo setting, which a flower has no business growing in.
        helper.setBlock(pot, DecorationsBlocks.PLANTER_POT.get().defaultBlockState().setValue(PlanterPotBlock.TOP, 2));
        helper.assertFalse(Blocks.POPPY.defaultBlockState().canSurvive(level, plant), "the planter pot sustained a flower on its bamboo soil");
        helper.succeed();
    }

    /** The clay and bone decorations place from their items and cycle through their variants when used. */
    private static void decorationBlocksPlace(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        List<Block> plain = List.of(DecorationsBlocks.CLAY_DECORATION.get(), DecorationsBlocks.BONE_DECORATION.get());
        for (int i = 0; i < plain.size(); i++) {
            BlockPos floor = new BlockPos(4 + i, 1, 4);
            helper.setBlock(floor, Blocks.STONE);

            ItemStack stack = new ItemStack(plain.get(i));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(plain.get(i), floor.above());
        }

        // Both decorations cycle through their variants when used, which is the only state they have.
        BlockPos clay = new BlockPos(4, 2, 4);
        BlockPos bone = new BlockPos(5, 2, 4);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        helper.assertBlockProperty(clay, ClayDecorationBlock.DECORATION, 0);
        helper.useBlock(clay, player, hitTop(helper.absolutePos(clay)));
        helper.assertBlockProperty(clay, ClayDecorationBlock.DECORATION, 1);

        helper.assertBlockProperty(bone, BoneDecorationBlock.DECORATION, 0);
        helper.useBlock(bone, player, hitTop(helper.absolutePos(bone)));
        helper.assertBlockProperty(bone, BoneDecorationBlock.DECORATION, 1);

        helper.succeed();
    }
}
