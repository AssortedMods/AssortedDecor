package com.grim3212.assorted.roads;

import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.helpers.RoadsCreativeItems;
import com.grim3212.assorted.roads.common.items.RoadsItems;

public class RoadsCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        RoadsBlocks.init();
        RoadsItems.init();
        RoadsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
