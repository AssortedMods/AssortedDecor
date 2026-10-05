package com.grim3212.assorted.buildingblocks.data;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.ColorChangingBlock;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.PanelBlock;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.CopyBlockState;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BuildingBlocksBlockLoot extends LibBlockLootProvider {

    private final List<Block> blocks = new ArrayList<>();

    // Loot sub providers are handed the registry lookup at construction now.
    public BuildingBlocksBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> BuildingBlocksBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));

        blocks.add(BuildingBlocksBlocks.CHAIN_LINK_FENCE.get());
        blocks.add(BuildingBlocksBlocks.DECORATIVE_STONE.get());
        blocks.add(BuildingBlocksBlocks.LUMBER_MILL.get());

        BuildingBlocks.all().stream().map(IRegistryObject::get).filter(b -> !(b instanceof SlabBlock) && !(b instanceof PanelBlock)).forEach(blocks::add);
    }

    @Override
    public void generate() {
        for (Block b : blocks) {
            this.dropSelf(b);
        }

        this.add(BuildingBlocksBlocks.QUARTZ_DOOR.get(), createDoorTable(BuildingBlocksBlocks.QUARTZ_DOOR.get()));
        this.add(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get(), createDoorTable(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get()));
        this.add(BuildingBlocksBlocks.GLASS_DOOR.get(), createDoorTable(BuildingBlocksBlocks.GLASS_DOOR.get()));
        this.add(BuildingBlocksBlocks.STEEL_DOOR.get(), createDoorTable(BuildingBlocksBlocks.STEEL_DOOR.get()));

        BuildingBlocks.cuts().values().forEach(cuts -> this.add(cuts.slab().get(), createSlabItemTable(cuts.slab().get())));
        BuildingBlocks.woods().forEach(wood -> this.add(wood.panel().get(), createMultifaceBlockDrops(wood.panel().get())));

        this.add(BuildingBlocksBlocks.SIDING_VERTICAL.get(), createColorTable(BuildingBlocksBlocks.SIDING_VERTICAL.get()));
        this.add(BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), createColorTable(BuildingBlocksBlocks.SIDING_HORIZONTAL.get()));
    }

    private LootTable.Builder createColorTable(Block b) {
        LootPoolEntryContainer.Builder<?> entry = LootItem.lootTableItem(b);
        CopyBlockState.Builder func = CopyBlockState.copyState(b).copy(ColorChangingBlock.COLOR);
        LootPool.Builder pool = LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(entry).when(ExplosionCondition.survivesExplosion()).apply(func);
        return LootTable.lootTable().withPool(pool);
    }
}
