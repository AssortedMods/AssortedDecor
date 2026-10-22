package com.grim3212.assorted.gates.data;

import com.grim3212.assorted.gates.common.blocks.GateBlock;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GatesBlockLoot extends LibBlockLootProvider {

    public GatesBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> GatesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.add(GatesBlocks.CASTLE_GATE.get(), createGateTable(GatesBlocks.CASTLE_GATE.get()));
        this.add(GatesBlocks.GARAGE_DOOR.get(), createGateTable(GatesBlocks.GARAGE_DOOR.get()));
    }

    /** One item per column: only the block at the top of a gate drops. */
    private LootTable.Builder createGateTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(LootItem.lootTableItem(b)
                .when(MatchBlock.blockMatches(this.blocks, b, StatePropertiesPredicate.Builder.properties().hasProperty(GateBlock.TOP, true))))
                .when(ExplosionCondition.survivesExplosion()));
    }
}
