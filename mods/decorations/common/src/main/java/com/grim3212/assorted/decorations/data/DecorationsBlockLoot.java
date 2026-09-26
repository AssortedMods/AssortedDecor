package com.grim3212.assorted.decorations.data;

import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DecorationsBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public DecorationsBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> DecorationsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        DecorationsBlocks.BLOCKS.getEntries().forEach(block -> this.dropSelf(block.get()));
    }
}
