package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.common.blocks.building.ColumnPart;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

/** How a shaped building block's models are written. */
interface ShapeModels {

    /** {@code part} is only read by columns, whose side texture changes from part to part. */
    Identifier model(Block block, ExtendedModelTemplateBuilder template, String suffix, @Nullable ColumnPart part);

    MultiVariant variant(Identifier model);

    void item(Block block, Identifier model);
}
