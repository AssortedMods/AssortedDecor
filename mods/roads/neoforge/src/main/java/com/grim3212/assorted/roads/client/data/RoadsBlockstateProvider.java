package com.grim3212.assorted.roads.client.data;

import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
import com.grim3212.assorted.roads.common.blocks.RoadwayLightBlock;
import com.grim3212.assorted.roads.common.blocks.RoadwayManholeBlock;
import com.grim3212.assorted.roads.common.blocks.RoadwayWhiteBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link RoadsItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class RoadsBlockstateProvider extends ModelProvider {

    public RoadsBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Roads block states";
    }

    /**
     * Only the block items belong here; every other item is {@link RoadsItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(RoadsBlocks.SIDEWALK.get());
        blockModels.createTrivialCube(RoadsBlocks.STONE_PATH.get());

        roadway(blockModels, RoadsBlocks.ROADWAY.get());
        RoadsBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
            if (color != DyeColor.WHITE) {
                roadway(blockModels, roadway.get());
            }
        });
        roadwayWhite(blockModels);
        roadwayLight(blockModels);
        roadwayManhole(blockModels);
    }

    /** A plain roadway: one cube model, plus the blockstate and item model pointing at it. */
    private void roadway(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Identifier model = roadwayModel(blockModels, name, texture("block/roadways/" + name));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleItemModel(b, model);
    }

    private Identifier roadwayModel(BlockModelGenerators blockModels, String name, Material top) {
        return ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, texture("block/roadways/roadway_side"))
                .put(TextureSlot.BOTTOM, texture("block/roadways/roadway_bottom"))
                .put(TextureSlot.TOP, top), blockModels.modelOutput);
    }

    private void roadwayWhite(BlockModelGenerators blockModels) {
        Block b = RoadsBlocks.ROADWAY_COLORS.get(DyeColor.WHITE).get();
        List<Identifier> models = new ArrayList<>();
        for (int type = 0; type <= 11; type++) {
            models.add(roadwayModel(blockModels, "roadway_white_" + type, texture("block/roadways/roadway_white_" + type)));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(RoadwayWhiteBlock.TYPE).generate(type -> BlockModelGenerators.plainVariant(models.get(type)))));
        blockModels.registerSimpleItemModel(b, models.get(11));
    }

    private void roadwayLight(BlockModelGenerators blockModels) {
        Block b = RoadsBlocks.ROADWAY_LIGHT.get();
        Identifier on = roadwayModel(blockModels, "roadway_light_on", texture("block/roadways/roadway_light_on"));
        Identifier off = roadwayModel(blockModels, "roadway_light_off", texture("block/roadways/roadway_light_off"));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(RoadwayLightBlock.ACTIVE, BlockModelGenerators.plainVariant(on), BlockModelGenerators.plainVariant(off))));
        blockModels.registerSimpleItemModel(b, on);
    }

    private void roadwayManhole(BlockModelGenerators blockModels) {
        Block b = RoadsBlocks.ROADWAY_MANHOLE.get();
        Identifier open = manholeModel(blockModels, "roadway_manhole_open", "block/roadways/roadway_manhole_bottom", "block/roadways/roadway_manhole_open");
        Identifier closed = manholeModel(blockModels, "roadway_manhole_closed", "block/roadways/roadway_manhole_closed", "block/roadways/roadway_manhole_closed");

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(BlockModelGenerators.createBooleanModelDispatch(RoadwayManholeBlock.OPEN, BlockModelGenerators.plainVariant(open), BlockModelGenerators.plainVariant(closed))));
        blockModels.registerSimpleItemModel(b, closed);
    }

    private Identifier manholeModel(BlockModelGenerators blockModels, String name, String bottom, String top) {
        return ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.SIDE, texture("block/roadways/roadway_side"))
                .put(TextureSlot.BOTTOM, texture(bottom))
                .put(TextureSlot.TOP, texture(top)), blockModels.modelOutput);
    }

    private static String name(Block b) {
        return BuiltInRegistries.BLOCK.getKey(b).getPath();
    }

    private static Identifier resource(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }

    private static Material texture(String path) {
        return new Material(resource(path));
    }
}
