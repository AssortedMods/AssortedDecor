package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.common.items.BuildingBlocksItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/** Item models for everything but block items, which {@link BuildingBlocksBlockstateProvider} models. */
public class BuildingBlocksItemModelProvider extends ModelProvider {

    public BuildingBlocksItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Building Blocks item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> !(holder.value() instanceof BlockItem));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(BuildingBlocksItems.CHAIN_LINK.get(), ModelTemplates.FLAT_ITEM);
    }
}
