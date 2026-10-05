package com.grim3212.assorted.paint.client.data;

import com.grim3212.assorted.paint.Constants;
import com.grim3212.assorted.paint.common.items.PaintItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.Holder;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;

import java.util.stream.Stream;

/** Item models for the rollers, held like a tool; this mod has no blocks. */
public class PaintItemModelProvider extends ModelProvider {

    public PaintItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Paint item models";
    }

    @Override
    protected Stream<? extends Holder<Block>> getKnownBlocks() {
        return Stream.empty();
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(PaintItems.PAINT_ROLLER.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
        PaintItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> itemModels.generateFlatItem(roller.get(), ModelTemplates.FLAT_HANDHELD_ITEM));
    }
}
