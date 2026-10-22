package com.grim3212.assorted.hangeables.data;

import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.data.loot.LootTableSubProvider;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HangeablesBlockLoot extends LibBlockLootProvider {

    public HangeablesBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> HangeablesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        // The wall sign borrows the standing sign's table, so it needs none of its own.
        this.dropSelf(HangeablesBlocks.NEON_SIGN.get());
        this.dropSelf(HangeablesBlocks.CALENDAR.get());
        this.dropSelf(HangeablesBlocks.WALL_CLOCK.get());
    }
}
