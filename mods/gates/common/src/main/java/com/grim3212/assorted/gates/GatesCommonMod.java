package com.grim3212.assorted.gates;

import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.common.helpers.GatesCreativeItems;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.gates.common.sounds.GatesSounds;
import com.grim3212.assorted.lib.family.Families;
import com.grim3212.assorted.lib.migration.MovedIds;
import net.minecraft.resources.Identifier;

public class GatesCommonMod {

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");
        Families.join(Constants.MOD_ID, Constants.FAMILY_ID)
                .icon(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "castle_gate"), 30)
                .manualOrder(60);

        GatesBlocks.init();
        GatesItems.init();
        GatesSounds.init();
        GatesCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Constants.FAMILY_ID, Constants.MOD_ID);
    }
}
