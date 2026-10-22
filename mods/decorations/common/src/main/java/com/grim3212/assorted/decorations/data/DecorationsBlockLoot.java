package com.grim3212.assorted.decorations.data;

import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DecorationsBlockLoot extends LibBlockLootProvider {

    public DecorationsBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> DecorationsBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        DecorationsBlocks.BLOCKS.getEntries().forEach(block -> this.dropSelf(block.get()));
    }
}
