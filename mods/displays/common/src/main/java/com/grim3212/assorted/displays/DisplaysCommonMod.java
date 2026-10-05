package com.grim3212.assorted.displays;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.blocks.blockentity.DisplaysBlockEntityTypes;
import com.grim3212.assorted.displays.common.helpers.DisplaysCreativeItems;
import com.grim3212.assorted.displays.common.inventory.DisplaysContainerTypes;
import com.grim3212.assorted.displays.common.items.DisplaysItems;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class DisplaysCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "diamond_display_case"), 40)
                .manualOrder(60);

        DisplaysBlocks.init();
        DisplaysItems.init();
        DisplaysBlockEntityTypes.init();
        DisplaysContainerTypes.init();
        DisplaysCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
