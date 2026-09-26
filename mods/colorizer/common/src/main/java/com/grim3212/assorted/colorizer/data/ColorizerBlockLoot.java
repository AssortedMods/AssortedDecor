package com.grim3212.assorted.colorizer.data;

import com.grim3212.assorted.colorizer.api.util.VerticalSlabType;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ColorizerBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    // Loot sub providers are handed the registry lookup at construction now.
    public ColorizerBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> ColorizerBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

        blocks.add(ColorizerBlocks.COLORIZER.get());
        blocks.add(ColorizerBlocks.COLORIZER_CHAIR.get());
        blocks.add(ColorizerBlocks.COLORIZER_TABLE.get());
        blocks.add(ColorizerBlocks.COLORIZER_STOOL.get());
        blocks.add(ColorizerBlocks.COLORIZER_COUNTER.get());
        blocks.add(ColorizerBlocks.COLORIZER_FENCE.get());
        blocks.add(ColorizerBlocks.COLORIZER_FENCE_GATE.get());
        blocks.add(ColorizerBlocks.COLORIZER_WALL.get());
        blocks.add(ColorizerBlocks.COLORIZER_STAIRS.get());
        blocks.add(ColorizerBlocks.COLORIZER_TRAP_DOOR.get());
        blocks.add(ColorizerBlocks.COLORIZER_LAMP_POST.get());
        blocks.add(ColorizerBlocks.COLORIZER_SLOPE.get());
        blocks.add(ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get());
        blocks.add(ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get());
        blocks.add(ColorizerBlocks.COLORIZER_SLOPED_POST.get());
        blocks.add(ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get());
        blocks.add(ColorizerBlocks.COLORIZER_CORNER.get());
        blocks.add(ColorizerBlocks.COLORIZER_SLANTED_CORNER.get());
        blocks.add(ColorizerBlocks.COLORIZER_PYRAMID.get());
        blocks.add(ColorizerBlocks.COLORIZER_FULL_PYRAMID.get());

        blocks.add(ColorizerBlocks.COLORIZER_CHIMNEY.get());
        blocks.add(ColorizerBlocks.COLORIZER_FIREPLACE.get());
        blocks.add(ColorizerBlocks.COLORIZER_FIREPIT.get());
        blocks.add(ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get());
        blocks.add(ColorizerBlocks.COLORIZER_FIRERING.get());
        blocks.add(ColorizerBlocks.COLORIZER_STOVE.get());
        blocks.add(ColorizerBlocks.COLORIZER_BEAM.get());
        blocks.add(ColorizerBlocks.COLORIZER_COLUMN.get());
    }

    @Override
    public void generate() {
        for (Block b : blocks) {
            this.dropSelf(b);
        }

        this.add(ColorizerBlocks.COLORIZER_DOOR.get(), createDoorTable(ColorizerBlocks.COLORIZER_DOOR.get()));
        this.add(ColorizerBlocks.COLORIZER_SLAB.get(), createSlabItemTable(ColorizerBlocks.COLORIZER_SLAB.get()));
        this.add(ColorizerBlocks.COLORIZER_PANEL.get(), createMultifaceBlockDrops(ColorizerBlocks.COLORIZER_PANEL.get()));
        this.add(ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(), createVerticalSlabItemTable(ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get()));
    }

    private LootTable.Builder createVerticalSlabItemTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(applyExplosionDecay(b, LootItem.lootTableItem(b).apply(SetItemCountFunction.setCount(ConstantValue.exactly(2.0F)).when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)))))));
    }
}
