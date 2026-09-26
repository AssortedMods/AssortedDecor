package com.grim3212.assorted.decor.client.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.building.ColumnPart;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;
import org.jetbrains.annotations.Nullable;

/**
 * Colorizer models: each shape is written once as a {@code #stored} template, then as the colorizer
 * loader model that fills the template from the block stored in it.
 */
record Colorized(BlockModelGenerators blockModels) implements ShapeModels {

    @Override
    public boolean stored() {
        return true;
    }

    @Override
    public Identifier model(Block block, ExtendedModelTemplateBuilder template, String suffix, @Nullable ColumnPart part) {
        String path = "block/" + BuildingBlockModels.name(block) + suffix;
        TextureMapping empty = new TextureMapping().put(TextureSlot.PARTICLE, DecorBlockstateProvider.COLORIZER_PARTICLE)
                .put(DecorBlockstateProvider.STORED, DecorBlockstateProvider.COLORIZER_PARTICLE)
                .put(TextureSlot.TEXTURE, DecorBlockstateProvider.COLORIZER_PARTICLE).put(TextureSlot.SIDE, DecorBlockstateProvider.COLORIZER_PARTICLE)
                .put(TextureSlot.END, DecorBlockstateProvider.COLORIZER_PARTICLE).put(BuildingBlockModels.PLINTH, DecorBlockstateProvider.COLORIZER_PARTICLE);
        Identifier shape = template.requiredTextureSlot(DecorBlockstateProvider.STORED).build().create(Identifier.fromNamespaceAndPath(Constants.MOD_ID, path + "_template"), empty, this.blockModels.modelOutput);
        return DecorBlockstateProvider.colorizerModel(this.blockModels, path, shape, builder -> {
        });
    }

    @Override
    public MultiVariant variant(Identifier model) {
        return DecorBlockstateProvider.colorizerVariant(model);
    }

    @Override
    public void item(Block block, Identifier model) {
        DecorBlockstateProvider.colorizerItem(this.blockModels, block, model);
    }
}
