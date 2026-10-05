package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.common.blocks.building.ColumnPart;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/** Plain models, textured as the mapping says. */
record Textured(BlockModelGenerators blockModels, Function<@Nullable ColumnPart, TextureMapping> textures) implements ShapeModels {

    @Override
    public Identifier model(Block block, ExtendedModelTemplateBuilder template, String suffix, @Nullable ColumnPart part) {
        return template.build().createWithSuffix(block, suffix, this.textures.apply(part), this.blockModels.modelOutput);
    }

    @Override
    public MultiVariant variant(Identifier model) {
        return BlockModelGenerators.plainVariant(model);
    }

    @Override
    public void item(Block block, Identifier model) {
        this.blockModels.registerSimpleItemModel(block, model);
    }
}
