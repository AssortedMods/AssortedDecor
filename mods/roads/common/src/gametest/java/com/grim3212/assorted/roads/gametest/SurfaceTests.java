package com.grim3212.assorted.roads.gametest;

import com.grim3212.assorted.lib.test.TestSupport;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.items.RoadsItems;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/** Laying a road without a crafting table, and washing the paint back off one. */
final class SurfaceTests {

    private static final BlockPos AT = new BlockPos(2, 1, 2);

    private SurfaceTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("asphalt_paves_stone_into_roadway", SurfaceTests::asphaltPavesStoneIntoRoadway);
        out.accept("water_bucket_washes_a_painted_roadway", SurfaceTests::waterBucketWashesAPaintedRoadway);
    }

    private static void asphaltPavesStoneIntoRoadway(GameTestHelper helper) {
        helper.setBlock(AT, Blocks.STONE);
        ItemStack asphalt = new ItemStack(RoadsItems.ASPHALT.get(), 2);
        ServerPlayer player = TestSupport.survivalPlayer(helper, asphalt);
        asphalt.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, TestSupport.hitTop(helper.absolutePos(AT))));
        helper.assertBlockPresent(RoadsBlocks.ROADWAY.get(), AT);
        helper.assertValueEqual(asphalt.getCount(), 1, "asphalt left after paving one stone");
        helper.succeed();
    }

    private static void waterBucketWashesAPaintedRoadway(GameTestHelper helper) {
        helper.setBlock(AT, RoadsBlocks.ROADWAY_COLORS.get(DyeColor.YELLOW).get());
        ServerPlayer player = TestSupport.survivalPlayer(helper, new ItemStack(Items.WATER_BUCKET));
        TestSupport.rightClick(player, helper.getLevel(), player.getMainHandItem(), helper.absolutePos(AT));
        helper.assertBlockPresent(RoadsBlocks.ROADWAY.get(), AT);
        helper.succeed();
    }
}
