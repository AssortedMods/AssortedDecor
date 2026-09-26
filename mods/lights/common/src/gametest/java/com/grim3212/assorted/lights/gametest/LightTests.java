package com.grim3212.assorted.lights.gametest;

import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.lights.common.blocks.LanternBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.lights.gametest.LightsTestSupport.*;

/**
 * Blocks that give off light: fluro blocks, lanterns and illumination blocks.
 */
final class LightTests {

    private LightTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("fluro_blocks_glow", LightTests::fluroBlocksGlow);
        out.accept("lanterns_place_on_floor_and_ceiling", LightTests::lanternsPlaceOnFloorAndCeiling);
        out.accept("illumination_blocks_emit_light", LightTests::illuminationBlocksEmitLight);
    }

    /** All sixteen fluro blocks declare full block light. */
    private static void fluroBlocksGlow(GameTestHelper helper) {
        Map<DyeColor, BlockPos> at = new java.util.EnumMap<>(DyeColor.class);

        int i = 0;
        for (DyeColor color : DyeColor.values()) {
            BlockPos rel = new BlockPos(3 + i % 4, 1 + i / 4, 4);
            helper.setBlock(rel, FluroBlock.FLURO_BY_DYE.get(color).get());
            at.put(color, rel);
            i++;
        }
        helper.runBeforeTestEnd(() -> at.values().forEach(rel -> helper.setBlock(rel, Blocks.AIR)));

        at.forEach((color, rel) -> helper.assertValueEqual(lightEmission(helper, rel), 15,
                "the " + color.getName() + " fluro block did not claim to emit light"));
        helper.succeed();
    }

    /**
     * Each lantern places from its item onto a floor and onto a ceiling. They carry no attachment
     * property - the model is the same either way - so what is worth asserting is that both
     * placements land a real, unwaterlogged lantern that still emits its light.
     */
    private static void lanternsPlaceOnFloorAndCeiling(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos floor = new BlockPos(4, 1, 2);
        BlockPos ceiling = new BlockPos(4, 5, 2);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (Block lantern : List.of(LightsBlocks.PAPER_LANTERN.get(), LightsBlocks.BONE_LANTERN.get(), LightsBlocks.IRON_LANTERN.get())) {
            String name = BuiltInRegistries.BLOCK.getKey(lantern).getPath();
            helper.setBlock(floor, Blocks.STONE);
            helper.setBlock(ceiling, Blocks.STONE);

            ItemStack stack = new ItemStack(lantern, 2);
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(floor), Direction.UP)));
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(ceiling), Direction.DOWN)));

            for (BlockPos placed : List.of(floor.above(), ceiling.below())) {
                BlockPos pos = helper.absolutePos(placed);
                helper.assertBlockPresent(lantern, placed);
                helper.assertBlockProperty(placed, LanternBlock.WATERLOGGED, false);
                helper.assertTrue(level.getBlockState(pos).canSurvive(level, pos), "a placed " + name + " did not think it could survive where it landed");
                helper.assertValueEqual(lightEmission(helper, placed), 14, name + " light emission");
                helper.setBlock(placed, Blocks.AIR);
            }
        }

        helper.succeed();
    }

    /** The illumination tube and plate both declare full block light standing on the floor. */
    private static void illuminationBlocksEmitLight(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos tube = new BlockPos(3, 1, 4);
        BlockPos plate = new BlockPos(5, 1, 4);

        helper.setBlock(tube, LightsBlocks.ILLUMINATION_TUBE.get().defaultBlockState().setValue(IlluminationTubeBlock.FACING, Direction.UP));
        helper.setBlock(plate, LightsBlocks.ILLUMINATION_PLATE.get().defaultBlockState().setValue(IlluminationTubeBlock.FACING, Direction.UP));

        helper.runBeforeTestEnd(() -> {
            helper.setBlock(tube, Blocks.AIR);
            helper.setBlock(plate, Blocks.AIR);
        });

        for (BlockPos rel : List.of(tube, plate)) {
            BlockPos pos = helper.absolutePos(rel);
            helper.assertTrue(level.getBlockState(pos).canSurvive(level, pos), "an illumination block standing on the floor did not think it could survive there");
        }

        helper.assertValueEqual(lightEmission(helper, tube), 15, "illumination tube light emission");
        helper.assertValueEqual(lightEmission(helper, plate), 15, "illumination plate light emission");
        helper.succeed();
    }
}
