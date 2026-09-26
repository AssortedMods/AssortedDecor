package com.grim3212.assorted.hangeables;

import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.HangeablesBlockEntityTypes;
import com.grim3212.assorted.hangeables.common.entity.HangeablesEntityTypes;
import com.grim3212.assorted.hangeables.common.helpers.HangeablesCreativeItems;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import com.grim3212.assorted.hangeables.common.network.HangeablesPackets;
import com.grim3212.assorted.hangeables.config.HangeablesCommonConfig;
import com.grim3212.assorted.lib.migration.MovedIds;

public class HangeablesCommonMod {

    public static final HangeablesCommonConfig COMMON_CONFIG = new HangeablesCommonConfig();

    public static void init() {
        Constants.LOG.info(Constants.MOD_NAME + " starting up...");

        HangeablesBlocks.init();
        HangeablesItems.init();
        HangeablesBlockEntityTypes.init();
        HangeablesEntityTypes.init();
        HangeablesPackets.init();
        HangeablesCreativeItems.init();

        // Recipes unlocked when this was all one mod carry over to their new ids.
        MovedIds.inherit(Family.ID, Constants.MOD_ID);
    }
}
