package com.grim3212.assorted.decorations;

import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.helpers.DecorationsCreativeItems;
import com.grim3212.assorted.decorations.common.items.DecorationsItems;
import com.grim3212.assorted.lib.migration.MovedIds;

public class DecorationsCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        DecorationsBlocks.init();
        DecorationsItems.init();
        DecorationsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
