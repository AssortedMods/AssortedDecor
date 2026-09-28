package com.grim3212.assorted.roads;

import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.helpers.RoadsCreativeItems;
import com.grim3212.assorted.roads.common.items.RoadsItems;
import net.minecraft.resources.Identifier;

public class RoadsCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "roadway"), 60)
                .manualOrder(60);

        RoadsBlocks.init();
        RoadsItems.init();
        RoadsCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
