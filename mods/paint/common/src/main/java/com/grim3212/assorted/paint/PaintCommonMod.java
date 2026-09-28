package com.grim3212.assorted.paint;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.paint.common.crafting.PaintRecipeSerializers;
import com.grim3212.assorted.paint.common.helpers.PaintCreativeItems;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class PaintCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "paint_roller_red"), 10)
                .manualOrder(60);

        PaintItems.init();
        PaintRecipeSerializers.init();
        PaintCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
