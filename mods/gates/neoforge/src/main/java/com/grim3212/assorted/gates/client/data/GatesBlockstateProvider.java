package com.grim3212.assorted.gates.client.data;

import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.gates.common.blocks.GateBlock;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
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
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link GatesItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class GatesBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    /**
     * The gates are modelled facing east, a two pixel plane a pixel in from the east side, as
     * GrimPack drew them; the blockstate turns them. The castle gate is a post and a crossbar, the
     * garage door a solid panel, and the open top block is the part left hanging from the ceiling.
     */
    private static final ModelTemplate CASTLE_GATE = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(12, 0, 6).to(14, 16, 10).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 10, 2, 6).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 6, 14, 10).cullface(Direction.UP);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 0, 4, 16);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(6, 0, 10, 16);
                }
            }))
            .element(e -> e.from(12, 6, 0).to(14, 10, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 16, 2, 0);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 6, 4, 10).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 6, 14, 10).cullface(Direction.SOUTH);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(0, 6, 16, 10);
                }
            }))
            .build();

    private static final ModelTemplate CASTLE_GATE_OPEN_TOP = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(12, 7, 6).to(14, 16, 10).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 10, 2, 6);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 6, 14, 10).cullface(Direction.UP);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 0, 4, 9);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 9);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(6, 0, 10, 9);
                }
            }))
            .element(e -> e.from(12, 14, 0).to(14, 16, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 16, 2, 0);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16).cullface(Direction.UP);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 0, 4, 2).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 2).cullface(Direction.SOUTH);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(0, 0, 16, 2);
                }
            }))
            .build();

    private static final ModelTemplate GARAGE_DOOR = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(12, 0, 0).to(14, 16, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 16, 2, 0).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16).cullface(Direction.UP);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 0, 4, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16).cullface(Direction.SOUTH);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(0, 0, 16, 16);
                }
            }))
            .build();

    private static final ModelTemplate GARAGE_DOOR_OPEN_TOP = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.TEXTURE)
            .element(e -> e.from(12, 7, 0).to(14, 16, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case DOWN -> face.texture(TextureSlot.TEXTURE).uvs(4, 16, 2, 0);
                    case UP -> face.texture(TextureSlot.TEXTURE).uvs(12, 0, 14, 16).cullface(Direction.UP);
                    case NORTH -> face.texture(TextureSlot.TEXTURE).uvs(2, 7, 4, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.TEXTURE).uvs(12, 7, 14, 16).cullface(Direction.SOUTH);
                    case WEST, EAST -> face.texture(TextureSlot.TEXTURE).uvs(0, 7, 16, 16);
                }
            }))
            .build();

    /** An open gate below its top block: nothing to draw, only the particle for breaking it. */
    private static final ModelTemplate GATE_HIDDEN = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .build();

    public GatesBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Gates block states";
    }

    /**
     * Only the block items belong here; every other item is {@link GatesItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        gate(blockModels, GatesBlocks.CASTLE_GATE.get(), CASTLE_GATE, CASTLE_GATE_OPEN_TOP);
        gate(blockModels, GatesBlocks.GARAGE_DOOR.get(), GARAGE_DOOR, GARAGE_DOOR_OPEN_TOP);
    }

    /** Closed, the open top, and nothing for the rest of an open gate; turned from east to its facing. */
    private void gate(BlockModelGenerators blockModels, Block b, ModelTemplate closedTemplate, ModelTemplate openTopTemplate) {
        String name = name(b);
        Material texture = texture("block/" + name);
        TextureMapping textures = new TextureMapping().put(TextureSlot.PARTICLE, texture).put(TextureSlot.TEXTURE, texture);
        Identifier closed = closedTemplate.create(resource("block/" + name), textures, blockModels.modelOutput);
        Identifier openTop = openTopTemplate.create(resource("block/" + name + "_open_top"), textures, blockModels.modelOutput);
        Identifier hidden = GATE_HIDDEN.create(resource("block/" + name + "_open"), new TextureMapping().put(TextureSlot.PARTICLE, texture), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(GateBlock.FACING, GateBlock.OPEN, GateBlock.TOP).generate((facing, open, top) -> {
                    Identifier model = !open ? closed : top ? openTop : hidden;
                    return BlockModelGenerators.plainVariant(model).with(switch (facing) {
                        case SOUTH -> BlockModelGenerators.Y_ROT_90;
                        case WEST -> BlockModelGenerators.Y_ROT_180;
                        case NORTH -> BlockModelGenerators.Y_ROT_270;
                        default -> BlockModelGenerators.NOP;
                    });
                })));

        blockModels.registerSimpleItemModel(b, closed);
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
