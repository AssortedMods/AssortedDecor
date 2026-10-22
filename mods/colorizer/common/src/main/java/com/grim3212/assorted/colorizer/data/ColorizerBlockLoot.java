package com.grim3212.assorted.colorizer.data;

import com.grim3212.assorted.colorizer.api.util.VerticalSlabType;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.MatchBlock;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ColorizerBlockLoot extends LibBlockLootProvider {

    private final List<Block> selfDrops = new ArrayList<>();

    public ColorizerBlockLoot(LootTableSubProvider.Context context) {
        super(context, () -> ColorizerBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

        selfDrops.add(ColorizerBlocks.COLORIZER.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_CHAIR.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_TABLE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_STOOL.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_COUNTER.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FENCE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FENCE_GATE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_WALL.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_STAIRS.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_TRAP_DOOR.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_LAMP_POST.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_SLOPE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_SLOPED_POST.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_CORNER.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_SLANTED_CORNER.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_PYRAMID.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FULL_PYRAMID.get());

        selfDrops.add(ColorizerBlocks.COLORIZER_CHIMNEY.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FIREPLACE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FIREPIT.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_FIRERING.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_STOVE.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_BEAM.get());
        selfDrops.add(ColorizerBlocks.COLORIZER_COLUMN.get());
    }

    @Override
    public void generate() {
        for (Block b : selfDrops) {
            this.dropSelf(b);
        }

        this.add(ColorizerBlocks.COLORIZER_DOOR.get(), createDoorTable(ColorizerBlocks.COLORIZER_DOOR.get()));
        this.add(ColorizerBlocks.COLORIZER_SLAB.get(), createSlabItemTable(ColorizerBlocks.COLORIZER_SLAB.get()));
        this.add(ColorizerBlocks.COLORIZER_PANEL.get(), createMultifaceBlockDrops(ColorizerBlocks.COLORIZER_PANEL.get()));
        this.add(ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get(), createVerticalSlabItemTable(ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get()));
    }

    private LootTable.Builder createVerticalSlabItemTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ContextIntProviders.exactly(1)).add(applyExplosionDecay(b, LootItem.lootTableItem(b).apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(2)).when(MatchBlock.blockMatches(this.blocks, b, StatePropertiesPredicate.Builder.properties().hasProperty(ColorizerVerticalSlabBlock.TYPE, VerticalSlabType.DOUBLE)))))));
    }
}
