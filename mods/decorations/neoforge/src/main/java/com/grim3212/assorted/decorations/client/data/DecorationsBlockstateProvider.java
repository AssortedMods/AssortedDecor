package com.grim3212.assorted.decorations.client.data;

import com.grim3212.assorted.decorations.Constants;
import com.grim3212.assorted.decorations.common.blocks.BoneDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.ClayDecorationBlock;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.decorations.common.blocks.PlanterPotBlock;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link DecorationsItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class DecorationsBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    /**
     * The planter pot with a hole in the bottom. {@code top} is filled in per soil by the seven models
     * that inherit this one.
     */
    private static final ModelTemplate PLANTER_POT_DOWN = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.SIDE)
            .element(e -> e.from(3, 0, 3).to(13, 16, 13).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST, NORTH, SOUTH, WEST -> face.texture(TextureSlot.SIDE).uvs(3, 0, 13, 16);
                    case DOWN -> face.texture(TextureSlot.SIDE).cullface(Direction.DOWN).uvs(13, 13, 3, 3);
                    case UP -> face.texture(TextureSlot.TOP).cullface(Direction.UP).uvs(3, 3, 13, 13);
                }
            }))
            .build();

    private static final ModelTemplate PLANTER_POT_DOWN_SOIL = ExtendedModelTemplateBuilder.builder()
            .parent(resource("block/planter_pot_down"))
            .requiredTextureSlot(TextureSlot.TOP)
            .build();

    /** The soils a planter pot's surface can be, indexed by {@link PlanterPotBlock#TOP}. */
    private static final String[] PLANTER_POT_SOILS = {"dirt", "sand", "gravel", "clay", "farmland", "netherrack", "soul_sand"};

    public DecorationsBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Decorations block states";
    }

    /**
     * Only the block items belong here; every other item is {@link DecorationsItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        decoration(blockModels, DecorationsBlocks.CLAY_DECORATION.get(), ClayDecorationBlock.DECORATION, "clay_decoration");
        decoration(blockModels, DecorationsBlocks.BONE_DECORATION.get(), BoneDecorationBlock.DECORATION, "bone_decoration");

        planterPot(blockModels);
        fountain(blockModels);
    }

    private void fountain(BlockModelGenerators blockModels) {
        Block b = DecorationsBlocks.FOUNTAIN.get();
        Identifier model = ModelTemplates.CUBE_BOTTOM_TOP.create(resource("block/" + name(b)), new TextureMapping()
                .put(TextureSlot.SIDE, new Material(Identifier.withDefaultNamespace("block/furnace_side")))
                .put(TextureSlot.BOTTOM, new Material(Identifier.withDefaultNamespace("block/furnace_top")))
                .put(TextureSlot.TOP, texture("block/fountain_top")), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
    }

    /**
     * A clay or bone decoration: one crossed-quad model per {@code decoration} value, with the item
     * showing the first of them as a flat sprite.
     */
    private void decoration(BlockModelGenerators blockModels, Block b, Property<Integer> property, String name) {
        Map<Integer, Identifier> models = new HashMap<>();
        for (int decoration : property.getPossibleValues()) {
            models.put(decoration, ModelTemplates.CROSS.create(resource("block/" + name + "_" + decoration),
                    TextureMapping.cross(texture("block/decorations/" + name + "_" + decoration)), blockModels.modelOutput));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(property).generate(decoration -> BlockModelGenerators.plainVariant(models.get(decoration)))));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name),
                TextureMapping.layer0(texture("block/decorations/" + name + "_0")), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    private void planterPot(BlockModelGenerators blockModels) {
        Block b = DecorationsBlocks.PLANTER_POT.get();
        Material pot = texture("block/planter_pot");
        PLANTER_POT_DOWN.create(resource("block/planter_pot_down"), new TextureMapping()
                .put(TextureSlot.PARTICLE, pot).put(TextureSlot.SIDE, pot), blockModels.modelOutput);

        List<Identifier> down = new ArrayList<>();
        List<Identifier> up = new ArrayList<>();
        for (int top = 0; top < PLANTER_POT_SOILS.length; top++) {
            Material soil = new Material(Identifier.withDefaultNamespace("block/" + PLANTER_POT_SOILS[top]));
            down.add(PLANTER_POT_DOWN_SOIL.create(resource("block/planter_pot_down_" + top),
                    new TextureMapping().put(TextureSlot.TOP, soil), blockModels.modelOutput));
            up.add(ModelTemplates.CUBE_TOP.create(resource("block/planter_pot_" + top), new TextureMapping()
                    .put(TextureSlot.PARTICLE, pot).put(TextureSlot.SIDE, pot).put(TextureSlot.TOP, soil), blockModels.modelOutput));
        }

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(PlanterPotBlock.TOP, PlanterPotBlock.DOWN)
                        .generate((top, isDown) -> BlockModelGenerators.plainVariant(isDown ? down.get(top) : up.get(top)))));

        blockModels.registerSimpleItemModel(b, up.get(0));
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
