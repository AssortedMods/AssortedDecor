package com.grim3212.assorted.gates.data;

import com.grim3212.assorted.gates.common.blocks.GateBlock;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class GatesBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public GatesBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> GatesBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.add(GatesBlocks.CASTLE_GATE.get(), createGateTable(GatesBlocks.CASTLE_GATE.get()));
        this.add(GatesBlocks.GARAGE_DOOR.get(), createGateTable(GatesBlocks.GARAGE_DOOR.get()));
    }

    /** One item per column: only the block at the top of a gate drops. */
    private LootTable.Builder createGateTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(b)
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(GateBlock.TOP, true))))
                .when(ExplosionCondition.survivesExplosion()));
    }
}
