package com.grim3212.assorted.lights.client.data;

import com.grim3212.assorted.lights.Constants;
import com.grim3212.assorted.lights.client.color.BlockMapColorItemTintSource;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.IlluminationTubeBlock;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

/** Block states, block models and item models. Every item here is a block item, so this is the only model provider. */
public class LightsBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    /** A full cube whose faces all carry tint index 0, where the fluro blocks' tint sources colour it. */
    private static final ModelTemplate COLOR_CUBE_ALL = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.ALL)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(TextureSlot.ALL).cullface(dir).tintindex(0)))
            .build();

    private static final ModelTemplate ILLUMINATION_PLATE_FLOOR = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(4, 0, 4).to(12, 2, 12).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST, NORTH, SOUTH, WEST -> face.texture(TextureSlot.TEXTURE).uvs(4, 14, 12, 16);
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(12, 12, 4, 4).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12);
                }
            }))
            .build();

    private static final ModelTemplate ILLUMINATION_PLATE_WALL = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(0, 4, 4).to(2, 12, 12).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(14, 4, 16, 12);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(0, 4, 2, 12);
                    case WEST -> face.texture(TextureSlot.TEXTURE).uvs(4, 4, 12, 12).cullface(Direction.WEST);
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(12, 16, 4, 14).rotation(com.mojang.math.Quadrant.R270);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(4, 14, 12, 16).rotation(com.mojang.math.Quadrant.R90);
                }
            }))
            .build();

    public LightsBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Lights block states";
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        fluro(blockModels);
        illuminationTube(blockModels);
        illuminationPlate(blockModels);

        cross(blockModels, LightsBlocks.PAPER_LANTERN.get());
        cross(blockModels, LightsBlocks.BONE_LANTERN.get());
        cross(blockModels, LightsBlocks.IRON_LANTERN.get());
    }

    /**
     * The sixteen fluro blocks share one model and are told apart by tint: {@link
     * BlockMapColorItemTintSource} and the block tint source registered in {@code LightsClient}.
     */
    private void fluro(BlockModelGenerators blockModels) {
        Identifier model = COLOR_CUBE_ALL.create(resource("block/fluro"), new TextureMapping()
                .put(TextureSlot.PARTICLE, texture("block/fluro"))
                .put(TextureSlot.ALL, texture("block/fluro")), blockModels.modelOutput);

        FluroBlock.FLURO_BY_DYE.values().forEach(supplier -> {
            Block b = supplier.get();
            blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));
            blockModels.registerSimpleTintedItemModel(b, model, new BlockMapColorItemTintSource());
        });
    }

    /**
     * The tube is a torch: a standing model for the vertical facings and a wall model, turned by
     * {@link BlockModelGenerators#ROTATION_TORCH}, for the horizontal ones.
     */
    private void illuminationTube(BlockModelGenerators blockModels) {
        Block b = LightsBlocks.ILLUMINATION_TUBE.get();
        Material tube = texture("block/illumination_tube");
        Identifier standing = ModelTemplates.TORCH.create(resource("block/illumination_tube"), TextureMapping.torch(tube), blockModels.modelOutput);
        Identifier wall = ModelTemplates.WALL_TORCH.create(resource("block/illumination_tube_wall"), TextureMapping.torch(tube), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(IlluminationTubeBlock.FACING).generate(dir -> orientTorch(standing, wall, dir))));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(tube), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    private void illuminationPlate(BlockModelGenerators blockModels) {
        Block b = LightsBlocks.ILLUMINATION_PLATE.get();
        Material plate = texture("block/illumination_plate");
        TextureMapping textures = new TextureMapping().put(TextureSlot.PARTICLE, plate).put(TextureSlot.TEXTURE, plate);

        Identifier floor = ILLUMINATION_PLATE_FLOOR.create(resource("block/illumination_plate"), textures, blockModels.modelOutput);
        Identifier wall = ILLUMINATION_PLATE_WALL.create(resource("block/illumination_plate_wall"), textures, blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(IlluminationTubeBlock.FACING).generate(dir -> orientTorch(floor, wall, dir))));

        blockModels.registerSimpleItemModel(b, wall);
    }

    /**
     * A lantern drawn as two crossed quads, with a flat item sprite over the same texture. Spelled
     * out because vanilla's {@code createCrossBlock} writes a {@code PlantType}-specific item
     * model.
     */
    private void cross(BlockModelGenerators blockModels, Block b) {
        Material tex = texture("block/" + name(b));
        Identifier model = ModelTemplates.CROSS.create(b, TextureMapping.cross(tex), blockModels.modelOutput);
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, BlockModelGenerators.plainVariant(model)));

        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)), TextureMapping.layer0(tex), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(b, itemModel);
    }

    /**
     * A torch style facing: the vertical facings draw the standing model (flipped for {@code DOWN}) and
     * the horizontal ones draw the wall model turned to face outwards.
     */
    private static MultiVariant orientTorch(Identifier standing, Identifier wall, Direction dir) {
        if (dir == Direction.DOWN) {
            return BlockModelGenerators.plainVariant(standing).with(BlockModelGenerators.X_ROT_180);
        }

        if (dir == Direction.UP) {
            return BlockModelGenerators.plainVariant(standing);
        }

        return BlockModelGenerators.plainVariant(wall).with(yRot(((int) dir.toYRot() + 90) % 360));
    }

    private static VariantMutator yRot(int degrees) {
        return switch (Math.floorMod(degrees, 360) / 90) {
            case 1 -> BlockModelGenerators.Y_ROT_90;
            case 2 -> BlockModelGenerators.Y_ROT_180;
            case 3 -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
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
