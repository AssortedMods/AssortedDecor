package com.grim3212.assorted.colorizer.gametest;

import com.grim3212.assorted.colorizer.api.colorizer.IColorizer;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.colorizer.gametest.ColorizerTestSupport.*;

/**
 * Light and colorizers: a colorizer blocks and gives off light as its stored block does, and a fireplace lights up.
 */
final class LightTests {

    private LightTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("fireplace_light_follows_active", LightTests::fireplaceLightFollowsActive);
        out.accept("colorizer_takes_its_blocks_light_dampening", LightTests::colorizerTakesItsBlocksLightDampening);
        out.accept("colorizer_blocks_light_like_its_block", LightTests::colorizerBlocksLightLikeItsBlock);
        out.accept("colorizer_blocks_skylight_like_its_block", LightTests::colorizerBlocksSkylightLikeItsBlock);
        out.accept("colorizer_emits_light_like_its_block", LightTests::colorizerEmitsLightLikeItsBlock);
    }

    /**
     * A glowstone filled colorizer glows in the server's light engine, not only the client's. The
     * engine asks from its own thread, where {@code Level#getBlockEntity} answers null, which is what
     * {@code IColorizer#getStoredState}'s chunk read is for. Asserted at the colorizer's own
     * position, where no other test's light can reach 15.
     */
    private static void colorizerEmitsLightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos cubeRel = new BlockPos(2, 1, 4);
        BlockPos stairsRel = new BlockPos(6, 1, 4);
        BlockPos cubePos = helper.absolutePos(cubeRel);
        BlockPos stairsPos = helper.absolutePos(stairsRel);

        helper.setBlock(cubeRel, ColorizerBlocks.COLORIZER.get());
        helper.setBlock(stairsRel, ColorizerBlocks.COLORIZER_STAIRS.get());
        helper.runBeforeTestEnd(() -> {
            helper.setBlock(cubeRel, Blocks.AIR);
            helper.setBlock(stairsRel, Blocks.AIR);
        });

        IColorizer cube = ColorizerBlocks.COLORIZER.get();
        IColorizer stairs = ColorizerBlocks.COLORIZER_STAIRS.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;
        BlockState glowstone = Blocks.GLOWSTONE.defaultBlockState();

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertTrue(cube.setColorizer(level, cubePos, glowstone, player, InteractionHand.MAIN_HAND, false), "setColorizer refused the colorizer");
                    helper.assertTrue(stairs.setColorizer(level, stairsPos, glowstone, player, InteractionHand.MAIN_HAND, false), "setColorizer refused the colorizer stairs");
                })
                .thenWaitUntil(() -> {
                    helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, cubePos), 15, "the server's block light at a glowstone filled colorizer");
                    helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, stairsPos), 15, "the server's block light at glowstone filled colorizer stairs");
                })
                .thenExecute(() -> {
                    helper.assertTrue(cube.clearColorizer(level, cubePos, player, InteractionHand.MAIN_HAND), "clearColorizer refused the colorizer");
                    helper.assertTrue(stairs.clearColorizer(level, stairsPos, player, InteractionHand.MAIN_HAND), "clearColorizer refused the colorizer stairs");
                })
                .thenWaitUntil(() -> {
                    helper.assertTrue(level.getBrightness(LightLayer.BLOCK, cubePos) < 15, "a cleared colorizer still reads as a light source on the server");
                    helper.assertTrue(level.getBrightness(LightLayer.BLOCK, stairsPos) < 15, "cleared colorizer stairs still read as a light source on the server");
                })
                .thenSucceed();
    }

    /**
     * The same for sky light, a separate engine with its own copy of the opacity question. The probe
     * is walled in on all four sides under the colorizer, because sky light spreads sideways as well
     * as down and would otherwise arrive from the neighbouring columns.
     */
    private static void colorizerBlocksSkylightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos probe = new BlockPos(2, 1, 6);
        BlockPos colorizerRel = probe.above();
        List<BlockPos> walls = List.of(probe.north(), probe.south(), probe.east(), probe.west());

        walls.forEach(wall -> helper.setBlock(wall, Blocks.STONE));
        helper.setBlock(colorizerRel, ColorizerBlocks.COLORIZER.get());
        helper.runBeforeTestEnd(() -> {
            walls.forEach(wall -> helper.setBlock(wall, Blocks.AIR));
            helper.setBlock(colorizerRel, Blocks.AIR);
        });

        BlockPos probePos = helper.absolutePos(probe);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        IColorizer colorizer = ColorizerBlocks.COLORIZER.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.startSequence()
                // An empty colorizer is a whole block and stops light itself, so glass is what lets
                // the sky in. Says the roofed probe really does see sky through it; without this the
                // assertion below would also hold in a box that never had sky access at all.
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.GLASS.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused an empty colorizer"))
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.SKY, probePos) > 0,
                        "no sky light reached under a glass filled colorizer, so this test would prove nothing"))
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.STONE.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused a filled colorizer"))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.SKY, probePos), 0,
                        "the sky light a stone filled colorizer let through to the probe beneath it"))
                .thenSucceed();
    }

    /**
     * A filled colorizer stops block light the way its stored block would, asserted through the light
     * engine itself. The probe is sealed in stone on five sides with the colorizer as the sixth and
     * glowstone beyond it, so the only path is through the colorizer and no neighbouring test box can
     * bleed in. Glass dampens by one, so light arrives; stone takes all fifteen, so none does.
     */
    private static void colorizerBlocksLightLikeItsBlock(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos probe = new BlockPos(4, 2, 4);
        BlockPos colorizerRel = probe.east();
        BlockPos glowstoneRel = probe.east(2);
        List<BlockPos> walls = List.of(probe.below(), probe.above(), probe.west(), probe.north(), probe.south());

        walls.forEach(wall -> helper.setBlock(wall, Blocks.STONE));
        helper.setBlock(colorizerRel, ColorizerBlocks.COLORIZER.get());
        helper.setBlock(glowstoneRel, Blocks.GLOWSTONE);
        helper.runBeforeTestEnd(() -> {
            walls.forEach(wall -> helper.setBlock(wall, Blocks.AIR));
            helper.setBlock(colorizerRel, Blocks.AIR);
            helper.setBlock(glowstoneRel, Blocks.AIR);
        });

        BlockPos probePos = helper.absolutePos(probe);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        IColorizer colorizer = ColorizerBlocks.COLORIZER.get();
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        helper.startSequence()
                // An empty colorizer is a whole block and stops light itself, so glass is what lets
                // the light in. Without this the rest proves nothing: it says the sealed probe really
                // is lit through the colorizer while it holds glass.
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.GLASS.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused an empty colorizer"))
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.BLOCK, probePos) > 0,
                        "no light reached the sealed probe through a glass filled colorizer"))
                .thenExecute(() -> helper.assertTrue(
                        colorizer.setColorizer(level, colorizerPos, Blocks.STONE.defaultBlockState(), player, InteractionHand.MAIN_HAND, false),
                        "setColorizer refused a filled colorizer"))
                .thenWaitUntil(() -> helper.assertValueEqual(level.getBrightness(LightLayer.BLOCK, probePos), 0,
                        "the light a stone filled colorizer let through to a sealed probe"))
                .thenSucceed();
    }

    /**
     * A filled colorizer answers the light questions with the block it stands in for, and only the
     * full cube does: a stairs colorizer keeps its own dampening, or a colorized staircase would cast
     * the shadow of a solid block. Every answer is stated as "the same answer the real block gives",
     * which keeps absolute light values, which a neighbouring test can influence, out of it:
     * <ul>
     * <li>the dampening the light engines are handed per position,</li>
     * <li>whether skylight passes, as the library reports it, and</li>
     * <li>the skylight heightmap. A colorizer's block state does not change when its stored block
     * does, so the column has to be recomputed when it is filled; glass is the telling case, since an
     * empty colorizer already stops the column on its own.</li>
     * </ul>
     */
    private static void colorizerTakesItsBlocksLightDampening(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos colorizerRel = new BlockPos(2, 1, 2);
        BlockPos stairsRel = new BlockPos(4, 1, 2);
        BlockPos referenceRel = new BlockPos(6, 1, 2);
        BlockPos colorizerPos = helper.absolutePos(colorizerRel);
        BlockPos stairsPos = helper.absolutePos(stairsRel);
        BlockPos referencePos = helper.absolutePos(referenceRel);

        helper.setBlock(colorizerRel, ColorizerBlocks.COLORIZER.get());
        helper.setBlock(stairsRel, ColorizerBlocks.COLORIZER_STAIRS.get());
        helper.runBeforeTestEnd(() -> {
            helper.setBlock(colorizerRel, Blocks.AIR);
            helper.setBlock(stairsRel, Blocks.AIR);
            helper.setBlock(referenceRel, Blocks.AIR);
        });

        IColorizer colorizer = ColorizerBlocks.COLORIZER.get();
        IColorizer stairs = ColorizerBlocks.COLORIZER_STAIRS.get();
        BlockState colorizerState = level.getBlockState(colorizerPos);
        BlockState stairsState = level.getBlockState(stairsPos);
        helper.assertValueEqual(colorizer.getLightDampening(colorizerState, level, colorizerPos), colorizerState.getLightDampening(),
                "an empty colorizer's light dampening, which should still be its own");

        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        player.getAbilities().instabuild = true;

        for (Block block : List.of(Blocks.STONE, Blocks.GLASS)) {
            BlockState stored = block.defaultBlockState();
            String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
            helper.setBlock(referenceRel, block);
            helper.assertTrue(colorizer.setColorizer(level, colorizerPos, stored, player, InteractionHand.MAIN_HAND, false),
                    "setColorizer refused the colorizer");
            helper.assertTrue(stairs.setColorizer(level, stairsPos, stored, player, InteractionHand.MAIN_HAND, false),
                    "setColorizer refused the colorizer stairs");

            // Re-read: a colorizer may carry the answer in its block state, which filling replaces.
            BlockState filledColorizer = level.getBlockState(colorizerPos);
            BlockState filledStairs = level.getBlockState(stairsPos);
            helper.assertValueEqual(colorizer.getLightDampening(filledColorizer, level, colorizerPos), stored.getLightDampening(),
                    "a " + name + " filled colorizer's light dampening, against the real block's");
            helper.assertValueEqual(filledColorizer.getLightDampening(), stored.getLightDampening(),
                    "a " + name + " filled colorizer's baked light dampening, which is what the light engines read");
            helper.assertValueEqual(stairs.getLightDampening(filledStairs, level, stairsPos), stairsState.getLightDampening(),
                    "a " + name + " filled colorizer stairs' light dampening, which should still be its own");
            helper.assertValueEqual(Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, colorizerPos),
                    Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, referencePos),
                    "whether skylight passes a " + name + " filled colorizer, against the real block");
            helper.assertValueEqual(Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, stairsPos),
                    Services.LEVEL_PROPERTIES.propagatesSkylightDown(level, referencePos),
                    "whether skylight passes " + name + " filled colorizer stairs, against the real block");
            helper.assertValueEqual(lowestSkySource(level, colorizerPos), lowestSkySource(level, referencePos),
                    "where the sky stops reaching down a " + name + " filled colorizer's column, against the real block's");
        }

        helper.succeed();
    }

    private static int lowestSkySource(ServerLevel level, BlockPos pos) {
        return level.getChunkAt(pos).getSkyLightSources().getLowestSourceY(pos.getX() & 15, pos.getZ() & 15);
    }

    /**
     * Lighting a fireplace makes it emit light, and putting it out stops it. Guards
     * {@code getLightEmission(state, level, pos)} reaching the light engine on both loaders.
     */
    private static void fireplaceLightFollowsActive(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, ColorizerBlocks.COLORIZER_FIREPLACE.get());
        helper.assertValueEqual(lightEmission(helper, MAIN), 0, "an unlit fireplace claimed to emit light");

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        ItemStack flintAndSteel = new ItemStack(Items.FLINT_AND_STEEL);
        player.setItemInHand(InteractionHand.MAIN_HAND, flintAndSteel);
        helper.runBeforeTestEnd(() -> helper.setBlock(MAIN, Blocks.AIR));

        helper.startSequence()
                .thenExecute(() -> rightClick(player, level, flintAndSteel, pos))
                .thenExecute(() -> helper.assertBlockProperty(MAIN, ColorizerFireplaceBaseBlock.ACTIVE, true))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, MAIN), 15, "a lit fireplace did not claim to emit light"))
                // The one check against the light engine, since the bug was emission never reaching
                // it. A lower bound, so light from a neighbouring test can only raise it.
                .thenWaitUntil(() -> helper.assertTrue(level.getBrightness(LightLayer.BLOCK, pos) >= lightEmission(helper, MAIN),
                        "a lit fireplace's light emission never reached the light engine"))
                .thenExecute(() -> level.setBlockAndUpdate(pos, level.getBlockState(pos).setValue(ColorizerFireplaceBaseBlock.ACTIVE, false)))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, MAIN), 0, "an extinguished fireplace kept claiming to emit light"))
                .thenSucceed();
    }
}
