package com.grim3212.assorted.buildingblocks;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.crafting.BuildingBlocksRecipeTypes;
import com.grim3212.assorted.buildingblocks.common.helpers.BuildingBlocksCreativeItems;
import com.grim3212.assorted.buildingblocks.common.inventory.BuildingBlocksContainerTypes;
import com.grim3212.assorted.buildingblocks.common.items.BuildingBlocksItems;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.lib.platform.Services;

public class BuildingBlocksCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        BuildingBlocksBlocks.init();
        BuildingBlocks.init();
        BuildingBlocks.flammables().forEach(block -> Services.PLATFORM.registerFlammable(block, 5, 20));
        BuildingBlocksItems.init();
        BuildingBlocksContainerTypes.init();
        BuildingBlocksRecipeTypes.init();
        BuildingBlocksCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
