package com.grim3212.assorted.hangeables.client.data;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
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

/** Item models for everything but block items, which {@link HangeablesBlockstateProvider} models. */
public class HangeablesItemModelProvider extends ModelProvider {

    public HangeablesItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Hangeables item models";
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
        // HangeablesItems.NEON_SIGN is a BlockItem, so it belongs to HangeablesBlockstateProvider;
        // listing it here too would write items/neon_sign.json twice.
        itemModels.generateFlatItem(HangeablesItems.WALLPAPER.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(HangeablesItems.WOOD_FRAME.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(HangeablesItems.IRON_FRAME.get(), ModelTemplates.FLAT_ITEM);
    }
}
