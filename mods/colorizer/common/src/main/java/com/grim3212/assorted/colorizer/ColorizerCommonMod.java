package com.grim3212.assorted.colorizer;

import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.blockentity.ColorizerBlockEntityTypes;
import com.grim3212.assorted.colorizer.common.events.ColorizerEvents;
import com.grim3212.assorted.colorizer.common.helpers.ColorizerCreativeItems;
import com.grim3212.assorted.colorizer.common.items.ColorizerDataComponents;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.colorizer.config.ColorizerCommonConfig;
import com.grim3212.assorted.lib.migration.MovedIds;

public class ColorizerCommonMod {

    public static final ColorizerCommonConfig COMMON_CONFIG = new ColorizerCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        ColorizerDataComponents.init();
        ColorizerBlocks.init();
        ColorizerItems.init();
        ColorizerBlockEntityTypes.init();
        ColorizerEvents.init();
        ColorizerCreativeItems.init();

        // Recipes and advancements unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
