package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.client.color.SidingItemTintSource;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link BuildingBlocksItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class BuildingBlocksBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    private static final ModelTemplate COLOR_CUBE_BOTTOM_TOP = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.SIDE)
            .requiredTextureSlot(TextureSlot.TOP)
            .requiredTextureSlot(TextureSlot.BOTTOM)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(switch (dir) {
                case DOWN -> TextureSlot.BOTTOM;
                case UP -> TextureSlot.TOP;
                default -> TextureSlot.SIDE;
            }).cullface(dir).tintindex(0)))
            .build();

    public BuildingBlocksBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Building Blocks block states";
    }

    /**
     * Only the block items belong here; every other item is {@link BuildingBlocksItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(BuildingBlocksBlocks.DECORATIVE_STONE.get());

        siding(blockModels, BuildingBlocksBlocks.SIDING_HORIZONTAL.get());
        siding(blockModels, BuildingBlocksBlocks.SIDING_VERTICAL.get());

        blockModels.createDoor(BuildingBlocksBlocks.QUARTZ_DOOR.get());
        blockModels.createDoor(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get());
        blockModels.createDoor(BuildingBlocksBlocks.GLASS_DOOR.get());
        blockModels.createDoor(BuildingBlocksBlocks.STEEL_DOOR.get());

        chainLinkFence(blockModels);

        new BuildingBlockModels(blockModels).run();
    }

    /**
     * A siding block, tinted by the dye recorded in its block state. The item carries
     * {@link SidingItemTintSource} at index 0 - the index the model stamps on every face - which is
     * what replaced the deleted {@code registerItemColor} handler.
     */
    private void siding(BlockModelGenerators blockModels, Block b) {
        String name = name(b);
        Material side = texture("block/" + name);
        Material topBottom = texture("block/siding_top_bottom");

        Identifier model = COLOR_CUBE_BOTTOM_TOP.create(resource("block/" + name), new TextureMapping()
                .put(TextureSlot.PARTICLE, side)
                .put(TextureSlot.SIDE, side)
                .put(TextureSlot.TOP, topBottom)
                .put(TextureSlot.BOTTOM, topBottom), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
        blockModels.registerSimpleTintedItemModel(b, model, new SidingItemTintSource());
    }

    /**
     * The chain link fence is an {@code IronBarsBlock}, which is a glass pane in blockstate terms.
     * Both the pane face and the edge use the door's lower texture, exactly as in 1.20.1.
     */
    private void chainLinkFence(BlockModelGenerators blockModels) {
        Block b = BuildingBlocksBlocks.CHAIN_LINK_FENCE.get();
        Material link = texture("block/chain_link_door_bottom");
        TextureMapping textures = new TextureMapping().put(TextureSlot.PANE, link).put(TextureSlot.EDGE, link);

        MultiVariant post = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_POST.create(b, textures, blockModels.modelOutput));
        MultiVariant side = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE.create(b, textures, blockModels.modelOutput));
        MultiVariant sideAlt = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_SIDE_ALT.create(b, textures, blockModels.modelOutput));
        MultiVariant noSide = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE.create(b, textures, blockModels.modelOutput));
        MultiVariant noSideAlt = BlockModelGenerators.plainVariant(ModelTemplates.STAINED_GLASS_PANE_NOSIDE_ALT.create(b, textures, blockModels.modelOutput));

        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(b)
                .with(post)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, true), side)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, true), side.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, true), sideAlt)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, true), sideAlt.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.NORTH, false), noSide)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.EAST, false), noSideAlt)
                .with(BlockModelGenerators.condition().term(BlockStateProperties.SOUTH, false), noSideAlt.with(BlockModelGenerators.Y_ROT_90))
                .with(BlockModelGenerators.condition().term(BlockStateProperties.WEST, false), noSide.with(BlockModelGenerators.Y_ROT_270)));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(link), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
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
