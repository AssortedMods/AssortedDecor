package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.colorizer.client.model.ColorizerItemModel;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.stream.Stream;

/** Item models for everything but block items, which {@link ColorizerBlockstateProvider} models. */
public class ColorizerItemModelProvider extends ModelProvider {

    public ColorizerItemModelProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Colorizer item models";
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
        colorizerBrush(itemModels);
    }

    /**
     * The brush, drawn by the colorizer model loader: its bristles take the {@code #stored} texture
     * of the block it has picked up. Tint index 0, which {@code item/brush} stamps on the bristle
     * faces, colours a tinted stored block.
     */
    private void colorizerBrush(ItemModelGenerators itemModels) {
        Item item = ColorizerItems.COLORIZER_BRUSH.get();
        ModelTemplate template = ColorizerBlockstateProvider.colorizerTemplate(
                resource("item/brush"), builder -> builder.addTexture("handle", resource("block/brush_handle")));

        Identifier model = template.create(modelId("item/" + name(item)), ColorizerBlockstateProvider.colorizerParticle(), itemModels.modelOutput);
        // ItemModelUtils#tintedModel would emit a minecraft:model, which bakes the colorizer's
        // geometry once with no stored block and so always draws an empty brush.
        itemModels.itemModelOutput.accept(item, new ColorizerItemModel.Unbaked(model, List.of(new ColorizerItemTintSource())));
    }

    private static String name(Item item) {
        return BuiltInRegistries.ITEM.getKey(item).getPath();
    }

    private static Identifier modelId(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
