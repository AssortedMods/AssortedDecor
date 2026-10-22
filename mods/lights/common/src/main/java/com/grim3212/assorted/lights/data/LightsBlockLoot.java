package com.grim3212.assorted.lights.data;

import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LightsBlockLoot extends LibBlockLootProvider {

    public LightsBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> LightsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        LightsBlocks.BLOCKS.getEntries().forEach(block -> this.dropSelf(block.get()));
    }
}
