package com.grim3212.assorted.buildingblocks;

import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FuelValueEvents;

public class AssortedBuildingBlocksFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        BuildingBlocksCommonMod.init();

        // NeoForge's half is the neoforge:furnace_fuels data map in BuildingBlocksDataMapProvider.
        FuelValueEvents.BUILD.register((builder, context) -> BuildingBlocks.fuels().forEach((block, ticks) -> builder.add(block.get(), ticks)));
    }
}
