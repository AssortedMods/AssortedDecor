package com.grim3212.assorted.buildingblocks.data;

import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.concurrent.CompletableFuture;

/** The building blocks' burn times. Mirror of the {@code FuelValueEvents} call in {@code AssortedBuildingBlocksFabric}. */
public class BuildingBlocksDataMapProvider extends DataMapProvider {

    public BuildingBlocksDataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void gather(HolderLookup.Provider registries) {
        Builder<FurnaceFuel, Item> fuels = builder(NeoForgeDataMaps.FURNACE_FUELS);
        BuildingBlocks.fuels().forEach((block, ticks) -> fuels.add(ResourceKey.create(Registries.ITEM, BuiltInRegistries.ITEM.getKey(block.get().asItem())), new FurnaceFuel(ticks), false));
    }
}
