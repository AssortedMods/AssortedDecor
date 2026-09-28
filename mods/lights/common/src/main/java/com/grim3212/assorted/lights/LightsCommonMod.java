package com.grim3212.assorted.lights;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lights.common.helpers.LightsCreativeItems;
import net.minecraft.resources.Identifier;

public class LightsCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "fluro_white"), 70)
                .manualOrder(60);

        LightsBlocks.init();
        LightsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
