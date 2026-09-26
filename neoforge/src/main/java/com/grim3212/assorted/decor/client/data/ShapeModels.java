package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.common.blocks.building.ColumnPart;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

/** How a shaped building block's models are written: textured, or as a colorizer drawing its stored block. */
interface ShapeModels {

    boolean stored();

    /** {@code part} is only read by columns, whose side texture changes from part to part. */
    Identifier model(Block block, ExtendedModelTemplateBuilder template, String suffix, @Nullable ColumnPart part);

    MultiVariant variant(Identifier model);

    void item(Block block, Identifier model);
}
