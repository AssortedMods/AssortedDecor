package com.grim3212.assorted.buildingblocks.client;

import com.grim3212.assorted.buildingblocks.client.color.SidingItemTintSource;
import com.grim3212.assorted.buildingblocks.client.screen.LumberMillScreen;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.buildingblocks.common.inventory.BuildingBlocksContainerTypes;
import com.grim3212.assorted.lib.platform.ClientServices;
import net.minecraft.util.ARGB;

import java.util.Arrays;

public class BuildingBlocksClient {

    public static void init() {
        ClientServices.CLIENT.registerScreen(BuildingBlocksContainerTypes.LUMBER_MILL::get, LumberMillScreen::new);

        // Colours are ARGB now, so the map colour's RGB has to be made opaque.
        ClientServices.CLIENT.registerBlockColor(state -> ARGB.opaque(state.getValue(ColorChangingBlock.COLOR).getMapColor().col), () -> Arrays.asList(BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), BuildingBlocksBlocks.SIDING_VERTICAL.get()));

        // The generated siding item models name this in their "tints" lists.
        ClientServices.CLIENT.registerItemTintSource(SidingItemTintSource.ID, SidingItemTintSource.MAP_CODEC);
    }
}
