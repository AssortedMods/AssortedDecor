package com.grim3212.assorted.buildingblocks.gametest;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.GameType;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.buildingblocks.gametest.BuildingBlocksTestSupport.*;

/** Siding keeps the colour its item carries. */
final class SidingTests {

    private SidingTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("siding_item_keeps_color", SidingTests::sidingItemKeepsColor);
    }

    /**
     * A dyed siding item places as that colour: the {@code DataComponents.BLOCK_STATE} the mod
     * writes is what {@code BlockItem} applies. Both sidings, in two colours, so neither can pass
     * by accident.
     */
    private static void sidingItemKeepsColor(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        placeSidingFromItem(helper, player, new BlockPos(3, 1, 4), BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), DyeColor.RED);
        placeSidingFromItem(helper, player, new BlockPos(5, 1, 4), BuildingBlocksBlocks.SIDING_VERTICAL.get(), DyeColor.LIME);

        helper.succeed();
    }
}
