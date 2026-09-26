package com.grim3212.assorted.lights;

import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lights.common.helpers.LightsCreativeItems;

public class LightsCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        LightsBlocks.init();
        LightsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
