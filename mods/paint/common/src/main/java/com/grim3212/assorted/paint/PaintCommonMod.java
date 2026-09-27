package com.grim3212.assorted.paint;

import com.grim3212.assorted.paint.common.crafting.PaintRecipeSerializers;
import com.grim3212.assorted.paint.common.helpers.PaintCreativeItems;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.migration.MovedIds;

public class PaintCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        PaintItems.init();
        PaintRecipeSerializers.init();
        PaintCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
