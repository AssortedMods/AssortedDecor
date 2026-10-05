package com.grim3212.assorted.buildingblocks;

import com.grim3212.assorted.buildingblocks.client.BuildingBlocksClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedBuildingBlocksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuildingBlocksClient.init();
    }
}
