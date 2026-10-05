package com.grim3212.assorted.displays.data;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.blocks.MuseumDisplayCaseBlock;
import com.grim3212.assorted.lib.data.LibBlockLootProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

import java.util.function.Supplier;
import java.util.stream.Collectors;

public class DisplaysBlockLoot extends LibBlockLootProvider {

    // Loot sub providers are handed the registry lookup at construction now.
    public DisplaysBlockLoot(HolderLookup.Provider registries) {
        super(registries, () -> DisplaysBlocks.BLOCKS.getEntries().stream().map(Supplier::get).collect(Collectors.toList()));
    }

    @Override
    public void generate() {
        this.dropSelf(DisplaysBlocks.CAGE.get());

        // A case keeps the name it was given; its contents are dropped by the block entity, not here.
        DisplaysBlocks.displayCaseBlocks().stream().map(IRegistryObject::get).forEach(displayCase -> this.add(displayCase, displayCase instanceof MuseumDisplayCaseBlock
                ? createMuseumDisplayCaseTable(displayCase)
                : createNameableBlockEntityTable(displayCase)));
    }

    /** One case per column: only the plinth the case stands on drops, as a door's bottom half does. */
    private LootTable.Builder createMuseumDisplayCaseTable(Block b) {
        return LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F))
                .add(LootItem.lootTableItem(b).apply(CopyComponentsFunction.copyComponentsFromBlockEntity(LootContextParams.BLOCK_ENTITY).include(DataComponents.CUSTOM_NAME)))
                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(b).setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.LOWER)))
                .when(ExplosionCondition.survivesExplosion()));
    }
}
