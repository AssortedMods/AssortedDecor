package com.grim3212.assorted.buildingblocks;

import net.fabricmc.api.ModInitializer;

public class AssortedBuildingBlocksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BuildingBlocksCommonMod.init();
    }
}
