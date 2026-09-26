package com.grim3212.assorted.roads.data;

import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RoadsBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public RoadsBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> RoadsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        RoadsBlocks.BLOCKS.getEntries().forEach(block -> this.dropSelf(block.get()));
    }
}
