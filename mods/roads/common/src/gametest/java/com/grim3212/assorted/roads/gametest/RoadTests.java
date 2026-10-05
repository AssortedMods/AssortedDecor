package com.grim3212.assorted.roads.gametest;

import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.roads.api.RoadsTags;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.roads.common.blocks.RoadwayManholeBlock;
import com.grim3212.assorted.roads.common.blocks.RoadwayWhiteBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.roads.gametest.RoadsTestSupport.*;

/**
 * Road surfaces: roadways and their markings, the manhole, the roadway light and the sidewalk.
 */
final class RoadTests {

    private RoadTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("roadway_blocks_place_and_react", RoadTests::roadwayBlocksPlaceAndReact);
        out.accept("sidewalk_is_the_faster_surface", RoadTests::sidewalkIsTheFasterSurface);
        out.accept("roadway_light_follows_redstone", RoadTests::roadwayLightFollowsRedstone);
    }

    /**
     * A sidewalk is walked along faster than the stone beside it, and no block of ours is slowed by
     * a friction below vanilla's. One rule, two halves: {@code getFrictionInfluencedSpeed} only pays
     * the acceleration back above 0.6, so a lower friction just shortens momentum. Speeding a
     * surface up is {@code speedFactor}, which the nudge below measures through {@code Entity#move}.
     */
    private static void sidewalkIsTheFasterSurface(GameTestHelper helper) {
        double onStone = nudge(helper, new BlockPos(1, 1, 1), Blocks.STONE);
        double onSidewalk = nudge(helper, new BlockPos(5, 1, 1), RoadsBlocks.SIDEWALK.get());

        helper.assertTrue(onSidewalk > onStone, "a nudge along the sidewalk ended at " + onSidewalk + " where the same nudge along stone ended at " + onStone + "; the sidewalk is meant to be the faster surface");

        float vanilla = Blocks.STONE.getFriction();
        List<String> slowed = new ArrayList<>();
        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            if (Constants.MOD_ID.equals(entry.getKey().identifier().getNamespace()) && entry.getValue().getFriction() < vanilla) {
                slowed.add(entry.getKey().identifier() + " (" + entry.getValue().getFriction() + ")");
            }
        }

        helper.assertTrue(slowed.isEmpty(), "a friction under " + vanilla + " only slows a walker down since 26.2, so it cannot be how a block is made quick: " + slowed);
        helper.succeed();
    }

    /** The horizontal speed one step of movement along a floor of {@code block} is left with. */
    private static double nudge(GameTestHelper helper, BlockPos floor, Block block) {
        helper.setBlock(floor, block);

        ItemEntity walker = new ItemEntity(helper.getLevel(), 0.0D, 0.0D, 0.0D, new ItemStack(Items.STICK));
        stand(helper, walker, floor);
        helper.getLevel().addFreshEntity(walker);
        helper.runBeforeTestEnd(walker::discard);

        walker.setDeltaMovement(0.2D, 0.0D, 0.0D);
        walker.move(MoverType.SELF, walker.getDeltaMovement());
        return walker.getDeltaMovement().x;
    }

    /**
     * Every road surface places from its item, and the manhole and white roadway react to use.
     * Roadways have no connection properties (their textures just tile), so there is none to check.
     */
    private static void roadwayBlocksPlaceAndReact(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        List<Block> road = List.of(RoadsBlocks.ROADWAY.get(), RoadsBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get(), RoadsBlocks.ROADWAY_LIGHT.get(),
                RoadsBlocks.ROADWAY_MANHOLE.get(), RoadsBlocks.SIDEWALK.get(), RoadsBlocks.STONE_PATH.get());

        for (int i = 0; i < road.size(); i++) {
            BlockPos floor = new BlockPos(1 + i, 1, 2);
            helper.setBlock(floor, Blocks.STONE);

            ItemStack stack = new ItemStack(road.get(i));
            player.setItemInHand(InteractionHand.MAIN_HAND, stack);
            stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(floor))));

            helper.assertBlockPresent(road.get(i), floor.above());
        }

        BlockPos white = new BlockPos(2, 2, 2);
        BlockPos manhole = new BlockPos(4, 2, 2);

        // The manhole answers an empty hand, so it goes through useBlock - the real interaction path.
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, false);
        helper.useBlock(manhole, player, hitTop(helper.absolutePos(manhole)));
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, true);
        helper.useBlock(manhole, player, hitTop(helper.absolutePos(manhole)));
        helper.assertBlockProperty(manhole, RoadwayManholeBlock.OPEN, false);

        // The white roadway cycles its marking in useItemOn, so this goes through useBlock too. Only
        // Assorted Paint puts anything in the tag, so without it there is nothing to cycle with.
        Iterator<Holder<Item>> painters = BuiltInRegistries.ITEM.getTagOrEmpty(RoadsTags.Items.ROAD_LINE_PAINTERS).iterator();
        if (painters.hasNext()) {
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(painters.next()));
            helper.assertBlockProperty(white, RoadwayWhiteBlock.TYPE, 0);
            helper.useBlock(white, player, hitTop(helper.absolutePos(white)));
            helper.assertBlockProperty(white, RoadwayWhiteBlock.TYPE, 1);
        }

        helper.succeed();
    }

    /**
     * The roadway light turns on the moment it is powered and off four ticks after the power goes
     * away, and its light level follows the state both ways.
     */
    private static void roadwayLightFollowsRedstone(GameTestHelper helper) {
        BlockPos lamp = new BlockPos(4, 1, 4);
        BlockPos power = new BlockPos(5, 1, 4);

        helper.setBlock(lamp, RoadsBlocks.ROADWAY_LIGHT.get());
        helper.runBeforeTestEnd(() -> helper.setBlock(lamp, Blocks.AIR));

        helper.startSequence()
                .thenExecute(() -> helper.setBlock(power, Blocks.REDSTONE_BLOCK))
                .thenExecute(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, true))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, lamp), 15, "a powered roadway light did not claim to emit light"))
                .thenExecute(() -> helper.setBlock(power, Blocks.AIR))
                .thenWaitUntil(() -> helper.assertBlockProperty(lamp, RoadwayLightBlock.ACTIVE, false))
                .thenExecute(() -> helper.assertValueEqual(lightEmission(helper, lamp), 0, "an unpowered roadway light kept claiming to emit light"))
                .thenSucceed();
    }
}
