package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.Constants;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

/**
 * Colorizer models: each shape is written once as a {@code #stored} template, then as the colorizer
 * loader model that fills the template from the block stored in it.
 */
record Colorized(BlockModelGenerators blockModels) {

    Identifier model(Block block, ExtendedModelTemplateBuilder template, String suffix) {
        String path = "block/" + ColorizerBuildingModels.name(block) + suffix;
        TextureMapping empty = new TextureMapping().put(TextureSlot.PARTICLE, ColorizerBlockstateProvider.COLORIZER_PARTICLE)
                .put(ColorizerBlockstateProvider.STORED, ColorizerBlockstateProvider.COLORIZER_PARTICLE)
                .put(TextureSlot.TEXTURE, ColorizerBlockstateProvider.COLORIZER_PARTICLE).put(TextureSlot.SIDE, ColorizerBlockstateProvider.COLORIZER_PARTICLE)
                .put(TextureSlot.END, ColorizerBlockstateProvider.COLORIZER_PARTICLE).put(ColorizerBuildingModels.PLINTH, ColorizerBlockstateProvider.COLORIZER_PARTICLE);
        Identifier shape = template.requiredTextureSlot(ColorizerBlockstateProvider.STORED).build().create(Identifier.fromNamespaceAndPath(Constants.MOD_ID, path + "_template"), empty, this.blockModels.modelOutput);
        return ColorizerBlockstateProvider.colorizerModel(this.blockModels, path, shape, builder -> {
        });
    }

    MultiVariant variant(Identifier model) {
        return ColorizerBlockstateProvider.colorizerVariant(model);
    }

    void item(Block block, Identifier model) {
        ColorizerBlockstateProvider.colorizerItem(this.blockModels, block, model);
    }
}
