package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.api.util.VerticalSlabType;
import com.grim3212.assorted.colorizer.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.colorizer.client.model.ColorizerItemModel;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerFireplaceBlock;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerLampPost;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerLampPost.LampPart;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerStoolBlock;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerTableBlock;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerVerticalSlabBlock;
import com.grim3212.assorted.lib.client.data.SpecificationBlockStateModelBuilder;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.BlockModelDefinitionGenerator;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.blockstates.PropertyValueList;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModelDispatcher;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Block states and block models, the colorizer loader models included. This owns every block and
 * block item; {@link ColorizerItemModelProvider} owns the rest, so the two never write the same file.
 */
public class ColorizerBlockstateProvider extends ModelProvider {

    /**
     * The slot every colorizer shape reads its face texture from. {@link TextureSlot} has no
     * {@code equals}, so it has to be created exactly once and shared.
     */
    static final TextureSlot STORED = TextureSlot.create("stored");

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");
    private static final Identifier TINTED_CUBE = resource("block/tinted_cube");

    /** Every colorizer model draws {@code block/colorizer} as its particle. */
    static final Material COLORIZER_PARTICLE = texture("block/colorizer");

    /**
     * The shape {@code colorizer} itself inherits: a full cube whose faces all read {@code #stored}
     * and all carry tint index 0, which is where the colorizer's {@code BlockTintSource} colours it.
     */
    private static final ModelTemplate TINTED_CUBE_TEMPLATE = defaultPerspective(ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(STORED)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(STORED).cullface(dir).tintindex(0))))
            .build();

    public ColorizerBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Colorizer block states";
    }

    /**
     * Only the block items belong here; every other item is {@link ColorizerItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        TINTED_CUBE_TEMPLATE.create(TINTED_CUBE, new TextureMapping()
                .put(TextureSlot.PARTICLE, COLORIZER_PARTICLE)
                .put(STORED, COLORIZER_PARTICLE), blockModels.modelOutput);

        colorizer(blockModels, ColorizerBlocks.COLORIZER.get(), TINTED_CUBE);
        colorizerRotate(blockModels, ColorizerBlocks.COLORIZER_CHAIR.get(), resource("block/chair"));
        Identifier counterModel = colorizerSide(blockModels, ColorizerBlocks.COLORIZER_COUNTER.get(), resource("block/counter"));
        colorizerTable(blockModels, counterModel);
        colorizerStool(blockModels);
        colorizerFence(blockModels);
        colorizerFenceGate(blockModels);
        colorizerWall(blockModels);
        colorizerTrapDoor(blockModels);
        colorizerDoor(blockModels);
        colorizerStairs(blockModels);
        colorizerSlab(blockModels);
        colorizerVerticalSlab(blockModels);
        colorizerLampPost(blockModels);

        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_SLOPE.get(), resource("models/block/slope.obj"));
        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_SLOPED_ANGLE.get(), resource("models/block/sloped_angle.obj"));
        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_SLOPED_INTERSECTION.get(), resource("models/block/sloped_intersection.obj"));
        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_OBLIQUE_SLOPE.get(), resource("models/block/oblique_slope.obj"));
        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_CORNER.get(), resource("models/block/corner.obj"));
        colorizerObj(blockModels, ColorizerBlocks.COLORIZER_SLANTED_CORNER.get(), resource("models/block/slanted_corner.obj"));
        colorizerObjSide(blockModels, ColorizerBlocks.COLORIZER_PYRAMID.get(), resource("models/block/pyramid.obj"));
        colorizerObjSide(blockModels, ColorizerBlocks.COLORIZER_FULL_PYRAMID.get(), resource("models/block/full_pyramid.obj"));
        colorizerObjSide(blockModels, ColorizerBlocks.COLORIZER_SLOPED_POST.get(), resource("models/block/sloped_post.obj"));

        colorizerChimney(blockModels);
        colorizerFireplace(blockModels);
        colorizerFirepit(blockModels);
        colorizerFireringStove(blockModels);

        new ColorizerBuildingModels(blockModels).run();
    }

    // ------------------------------------------------------------------ colorizers

    /**
     * A colorizer whose model is the same for every block state.
     */
    private Identifier colorizer(BlockModelGenerators blockModels, Block b, Identifier parent) {
        Identifier model = colorizerModel(blockModels, "block/" + name(b), parent, builder -> {});
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, colorizerVariant(model)));
        colorizerItem(blockModels, b, model);
        return model;
    }

    /**
     * A colorizer placed against a face, rotated by {@code HALF} and its horizontal facing - the chair
     * and every OBJ slope shape.
     */
    private Identifier colorizerRotate(BlockModelGenerators blockModels, Block b, Identifier parent) {
        return colorizerRotateState(blockModels, b, colorizerModel(blockModels, "block/" + name(b), parent, builder -> {}));
    }

    private Identifier colorizerRotateState(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BlockStateProperties.HORIZONTAL_FACING, BlockStateProperties.HALF).generate((facing, half) -> {
                    int yRot = ((int) facing.getClockWise().toYRot() + (half == Half.TOP ? 270 : 90)) % 360;
                    boolean uvLock = yRot != 0 || half == Half.TOP;
                    return variant(model, half == Half.TOP ? 180 : 0, yRot, uvLock);
                })));
        colorizerItem(blockModels, b, model);
        return model;
    }

    /**
     * A colorizer attached to any of the six faces - the counter, the pyramids and the sloped post.
     */
    private Identifier colorizerSide(BlockModelGenerators blockModels, Block b, Identifier parent) {
        return colorizerSideState(blockModels, b, colorizerModel(blockModels, "block/" + name(b), parent,
                builder -> {}, ColorizerBlockstateProvider::defaultPerspective));
    }

    private Identifier colorizerSideState(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                        .generate((face, facing) -> variant(model, face.ordinal() * 90, sideYRot(face, facing), false))));
        colorizerItem(blockModels, b, model);
        return model;
    }

    private void colorizerObj(BlockModelGenerators blockModels, Block b, Identifier objModel) {
        colorizerRotateState(blockModels, b, colorizerObjModel(blockModels, "block/" + name(b), objModel));
    }

    private void colorizerObjSide(BlockModelGenerators blockModels, Block b, Identifier objModel) {
        colorizerSideState(blockModels, b, colorizerObjModel(blockModels, "block/" + name(b), objModel));
    }

    private void colorizerChimney(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_CHIMNEY.get();
        Identifier model = colorizerModel(blockModels, "block/" + name(b), resource("block/chimney"),
                builder -> builder.addTexture("top", resource("block/chimney_top")));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(b, colorizerVariant(model)));
        colorizerItem(blockModels, b, model);
    }

    private void colorizerStool(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_STOOL.get();
        Identifier stool = colorizerModel(blockModels, "block/colorizer_stool", resource("block/stool"), builder -> {});
        Identifier stoolUp = colorizerModel(blockModels, "block/colorizer_stool_up", resource("block/stool_up"), builder -> {});

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerStoolBlock.UP, BlockStateProperties.ATTACH_FACE, BlockStateProperties.HORIZONTAL_FACING)
                        .generate((up, face, facing) -> variant(up ? stoolUp : stool, face.ordinal() * 90, sideYRot(face, facing), false))));

        colorizerItem(blockModels, b, stool);
    }

    /**
     * The table, chosen from six connection flags plus its attached face and facing: eight
     * properties, more than a {@link PropertyDispatch} can carry, so it goes through {@link
     * #forEachState}. Y rotations are normalised to a {@link com.mojang.math.Quadrant}, since some
     * branches sum past 360.
     */
    private void colorizerTable(BlockModelGenerators blockModels, Identifier counterModel) {
        Block b = ColorizerBlocks.COLORIZER_TABLE.get();
        Identifier tableN = colorizerModel(blockModels, "block/colorizer_table_n", resource("block/table_n"), builder -> {});
        Identifier tableSE = colorizerModel(blockModels, "block/colorizer_table_se", resource("block/table_se"), builder -> {});
        Identifier tableNWall = colorizerModel(blockModels, "block/colorizer_table_n_wall", resource("block/table_n_wall"), builder -> {});
        Identifier tableSEWall = colorizerModel(blockModels, "block/colorizer_table_se_wall", resource("block/table_se_wall"), builder -> {});
        Identifier table = colorizerModel(blockModels, "block/colorizer_table", resource("block/table"), builder -> {});

        blockModels.blockStateOutput.accept(forEachState(b, state -> {
            boolean east = state.getValue(ColorizerTableBlock.EAST);
            boolean north = state.getValue(ColorizerTableBlock.NORTH);
            boolean south = state.getValue(ColorizerTableBlock.SOUTH);
            boolean west = state.getValue(ColorizerTableBlock.WEST);
            boolean up = state.getValue(ColorizerTableBlock.UP);
            boolean down = state.getValue(ColorizerTableBlock.DOWN);

            AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
            Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

            int numConnections = countConnections(east, north, south, west, up, down);

            if (numConnections >= 3) {
                return variant(counterModel, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
            } else if (numConnections == 2) {
                boolean oppositeEnds = (north && south) || (west && east) || (up && down);

                if (oppositeEnds) {
                    return variant(counterModel, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
                }

                int rotY = west && south ? 90 : north && west ? 180 : north && east ? 270 : 0;
                int rotX = 0;

                if (face == AttachFace.CEILING) {
                    rotY += 90;
                } else if (face == AttachFace.WALL) {
                    rotY = ((((int) facing.toYRot() + 180)) % 360) + 270;

                    if (facing == Direction.NORTH)
                        rotX = down && east ? 0 : down && west ? 270 : up && west ? 180 : 90;
                    else if (facing == Direction.SOUTH)
                        rotX = down && east ? 270 : down && west ? 0 : up && west ? 90 : 180;
                    else if (facing == Direction.EAST)
                        rotX = south && up ? 90 : north && up ? 180 : north && down ? 270 : 0;
                    else if (facing == Direction.WEST)
                        rotX = south && up ? 180 : north && up ? 90 : north && down ? 0 : 270;

                    return variant(tableSEWall, rotX, rotY, true);
                }

                return variant(tableSE, (face.ordinal() * 90) + rotX, rotY, true);
            } else if (numConnections == 1) {
                int rotY = west ? 270 : east ? 90 : south ? 180 : 0;
                int rotX = 0;

                if (face == AttachFace.CEILING) {
                    rotY += 180;
                } else if (face == AttachFace.WALL) {
                    rotY = (((int) facing.toYRot() + 180)) % 360;
                    rotY += up ? 180 : down ? 0 : 270;
                    boolean flag = (south && facing == Direction.EAST) || (east && facing == Direction.NORTH) || (north && facing == Direction.WEST) || (west && facing == Direction.SOUTH);

                    rotX = up ? 180 : flag ? 180 : 0;

                    if (!(up || down)) {
                        return variant(tableNWall, rotX, rotY, true);
                    }
                }

                return variant(tableN, (face.ordinal() * 90) + rotX, rotY, true);
            }

            return variant(table, face.ordinal() * 90, ((int) facing.toYRot() + 180) % 360, true);
        }, ColorizerTableBlock.WATERLOGGED));

        colorizerItem(blockModels, b, table);
    }

    private void colorizerFence(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_FENCE.get();
        MultiVariant post = colorizerVariant(colorizerModel(blockModels, "block/colorizer_fence_post", resource("block/fence_post"), builder -> {}));
        MultiVariant side = colorizerVariant(colorizerModel(blockModels, "block/colorizer_fence_side", resource("block/fence_side"), builder -> {}));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createFence(b, post, side));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_fence", resource("item/fence_inventory"), builder -> {}));
    }

    private void colorizerFenceGate(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_FENCE_GATE.get();
        Identifier closed = colorizerModel(blockModels, "block/colorizer_fence_gate", resource("block/fence_gate"), builder -> {});
        Identifier open = colorizerModel(blockModels, "block/colorizer_fence_gate_open", resource("block/fence_gate_open"), builder -> {});
        Identifier closedWall = colorizerModel(blockModels, "block/colorizer_fence_gate_wall", resource("block/fence_gate_wall"), builder -> {});
        Identifier openWall = colorizerModel(blockModels, "block/colorizer_fence_gate_wall_open", resource("block/fence_gate_wall_open"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createFenceGate(b,
                colorizerVariant(open), colorizerVariant(closed),
                colorizerVariant(openWall), colorizerVariant(closedWall), true));

        colorizerItem(blockModels, b, closed);
    }

    private void colorizerWall(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_WALL.get();
        MultiVariant post = colorizerVariant(colorizerModel(blockModels, "block/colorizer_wall_post", resource("block/wall_post"), builder -> {}));
        MultiVariant lowSide = colorizerVariant(colorizerModel(blockModels, "block/colorizer_wall_side", resource("block/wall_side"), builder -> {}));
        MultiVariant tallSide = colorizerVariant(colorizerModel(blockModels, "block/colorizer_wall_side_tall", resource("block/wall_side_tall"), builder -> {}));

        blockModels.blockStateOutput.accept(BlockModelGenerators.createWall(b, post, lowSide, tallSide));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_wall", resource("item/wall_inventory"), builder -> {}));
    }

    private void colorizerTrapDoor(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_TRAP_DOOR.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_trapdoor_bottom", resource("block/trapdoor_bottom"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_trapdoor_top", resource("block/trapdoor_top"), builder -> {});
        Identifier open = colorizerModel(blockModels, "block/colorizer_trapdoor_open", resource("block/trapdoor_open"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createOrientableTrapdoor(b,
                colorizerVariant(top), colorizerVariant(bottom), colorizerVariant(open)));

        colorizerItem(blockModels, b, bottom);
    }

    private void colorizerDoor(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_DOOR.get();
        MultiVariant bottomLeft = colorizerDoorPart(blockModels, "door_bottom_left");
        MultiVariant bottomLeftOpen = colorizerDoorPart(blockModels, "door_bottom_left_open");
        MultiVariant bottomRight = colorizerDoorPart(blockModels, "door_bottom_right");
        MultiVariant bottomRightOpen = colorizerDoorPart(blockModels, "door_bottom_right_open");
        MultiVariant topLeft = colorizerDoorPart(blockModels, "door_top_left");
        MultiVariant topLeftOpen = colorizerDoorPart(blockModels, "door_top_left_open");
        MultiVariant topRight = colorizerDoorPart(blockModels, "door_top_right");
        MultiVariant topRightOpen = colorizerDoorPart(blockModels, "door_top_right_open");

        blockModels.blockStateOutput.accept(BlockModelGenerators.createDoor(b,
                bottomLeft, bottomLeftOpen, bottomRight, bottomRightOpen, topLeft, topLeftOpen, topRight, topRightOpen));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_door", resource("item/door"), builder -> {}));
    }

    private MultiVariant colorizerDoorPart(BlockModelGenerators blockModels, String part) {
        return colorizerVariant(colorizerModel(blockModels, "block/colorizer_" + part, resource("block/" + part), builder -> {}));
    }

    private void colorizerStairs(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_STAIRS.get();
        Identifier straight = colorizerModel(blockModels, "block/colorizer_stairs", resource("block/stairs"), builder -> {});
        Identifier inner = colorizerModel(blockModels, "block/colorizer_inner_stairs", resource("block/inner_stairs"), builder -> {});
        Identifier outer = colorizerModel(blockModels, "block/colorizer_outer_stairs", resource("block/outer_stairs"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createStairs(b,
                colorizerVariant(inner), colorizerVariant(straight), colorizerVariant(outer)));

        colorizerItem(blockModels, b, straight);
    }

    private void colorizerSlab(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_SLAB.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_slab", resource("block/slab"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_slab_top", resource("block/slab_top"), builder -> {});

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSlab(b,
                colorizerVariant(bottom), colorizerVariant(top),
                colorizerVariant(resource("block/colorizer"))));

        colorizerItem(blockModels, b, bottom);
    }

    /**
     * The vertical slab. In 1.20.1 the four facings were four identical model files that differed only
     * in the Y rotation the blockstate applied to them; one model plus the rotation says the same
     * thing, so only {@code block/colorizer_vertical_slab} is generated now.
     */
    private void colorizerVerticalSlab(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_VERTICAL_SLAB.get();
        Identifier slab = colorizerModel(blockModels, "block/colorizer_vertical_slab", resource("block/vertical_slab"), builder -> {});
        MultiVariant north = colorizerVariant(slab);

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerVerticalSlabBlock.TYPE).generate(type -> switch (type) {
                    case NORTH -> north;
                    case SOUTH -> north.with(BlockModelGenerators.Y_ROT_180);
                    case WEST -> north.with(BlockModelGenerators.Y_ROT_270);
                    case EAST -> north.with(BlockModelGenerators.Y_ROT_90);
                    case VerticalSlabType.DOUBLE -> colorizerVariant(resource("block/colorizer"));
                })));

        colorizerItem(blockModels, b, slab);
    }

    private void colorizerLampPost(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_LAMP_POST.get();
        Identifier bottom = colorizerModel(blockModels, "block/colorizer_lamp_post_bottom", resource("block/lamp_post_bottom"), builder -> {});
        Identifier middle = colorizerModel(blockModels, "block/colorizer_lamp_post_middle", resource("block/lamp_post_middle"), builder -> {});
        Identifier top = colorizerModel(blockModels, "block/colorizer_lamp_post_top", resource("block/lamp_post_top"),
                builder -> builder.addTexture("lamp", Identifier.withDefaultNamespace("block/glowstone")));

        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(ColorizerLampPost.PART).generate(part -> colorizerVariant(switch (part) {
                    case LampPart.BOTTOM -> bottom;
                    case LampPart.MIDDLE -> middle;
                    case LampPart.TOP -> top;
                }))));

        colorizerItem(blockModels, b, colorizerModel(blockModels, "item/colorizer_lamp_post", resource("item/lamp_post_inventory"),
                builder -> builder.addTexture("lamp", Identifier.withDefaultNamespace("block/glowstone"))));
    }

    private void colorizerFireplace(BlockModelGenerators blockModels) {
        Block b = ColorizerBlocks.COLORIZER_FIREPLACE.get();
        MultiVariant plain = fireplacePart(blockModels, "colorizer_fireplace", "fireplace");
        MultiVariant n = fireplacePart(blockModels, "colorizer_fireplace_n", "fireplace_n");
        MultiVariant ne = fireplacePart(blockModels, "colorizer_fireplace_ne", "fireplace_ne");
        MultiVariant ns = fireplacePart(blockModels, "colorizer_fireplace_ns", "fireplace_ns");
        MultiVariant fire = BlockModelGenerators.plainVariant(resource("block/fire"));

        MultiPartGenerator generator = MultiPartGenerator.multiPart(b)
                .with(fireplaceCondition(false, false, false, false), plain)

                .with(fireplaceCondition(true, false, false, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))
                .with(fireplaceCondition(false, true, false, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                .with(fireplaceCondition(false, false, true, false), n.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                .with(fireplaceCondition(false, false, false, true), n.with(BlockModelGenerators.UV_LOCK))

                .with(fireplaceCondition(true, false, false, true), ne.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, true, true, false), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_180))
                .with(fireplaceCondition(false, true, false, true), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_270))
                .with(fireplaceCondition(true, false, true, false), ne.with(BlockModelGenerators.UV_LOCK).with(BlockModelGenerators.Y_ROT_90))

                .with(fireplaceCondition(true, true, false, false), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, false, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(false, true, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, false, true, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, false, true), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, true, false), ns.with(BlockModelGenerators.UV_LOCK))
                .with(fireplaceCondition(true, true, true, true), ns.with(BlockModelGenerators.UV_LOCK))

                .with(BlockModelGenerators.condition().term(ColorizerFireplaceBaseBlock.ACTIVE, true), fire);

        blockModels.blockStateOutput.accept(generator);
        colorizerItem(blockModels, b, resource("block/colorizer_fireplace"));
    }

    private MultiVariant fireplacePart(BlockModelGenerators blockModels, String name, String parent) {
        return colorizerVariant(colorizerModel(blockModels, "block/" + name, resource("block/" + parent),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks"))));
    }

    private static net.minecraft.client.data.models.blockstates.ConditionBuilder fireplaceCondition(boolean east, boolean west, boolean south, boolean north) {
        return BlockModelGenerators.condition()
                .term(ColorizerFireplaceBlock.EAST, east)
                .term(ColorizerFireplaceBlock.WEST, west)
                .term(ColorizerFireplaceBlock.SOUTH, south)
                .term(ColorizerFireplaceBlock.NORTH, north);
    }

    private void colorizerFirepit(BlockModelGenerators blockModels) {
        Identifier firepit = colorizerModel(blockModels, "block/colorizer_firepit", resource("block/firepit"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")));
        Identifier covered = colorizerModel(blockModels, "block/colorizer_firepit_covered", resource("block/firepit_covered"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")).addTexture("net", resource("block/net")));

        fireplaceBase(blockModels, ColorizerBlocks.COLORIZER_FIREPIT.get(), firepit, resource("block/fire_high"));
        fireplaceBase(blockModels, ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get(), covered, resource("block/fire_high"));
    }

    private void colorizerFireringStove(BlockModelGenerators blockModels) {
        Identifier firering = colorizerModel(blockModels, "block/colorizer_firering", resource("block/firering"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")));
        Identifier stove = colorizerModel(blockModels, "block/colorizer_stove", resource("block/stove"),
                builder -> builder.addTexture("wood", Identifier.withDefaultNamespace("block/oak_planks")).addTexture("net", resource("block/net")));

        fireplaceBase(blockModels, ColorizerBlocks.COLORIZER_FIRERING.get(), firering, resource("block/fire"));
        fireplaceBase(blockModels, ColorizerBlocks.COLORIZER_STOVE.get(), stove, resource("block/fire_high"));
    }

    /**
     * A fire pit, fire ring or stove: the body always draws, and the flame is a second part shown
     * while the block is active.
     */
    private void fireplaceBase(BlockModelGenerators blockModels, Block b, Identifier body, Identifier fire) {
        blockModels.blockStateOutput.accept(MultiPartGenerator.multiPart(b)
                .with(BlockModelGenerators.plainVariant(body))
                .with(BlockModelGenerators.condition().term(ColorizerFireplaceBaseBlock.ACTIVE, true), BlockModelGenerators.plainVariant(fire)));

        colorizerItem(blockModels, b, body);
    }

    // ------------------------------------------------------------------ colorizer plumbing

    /**
     * The tint source is what replaced the deleted {@code registerItemColor} handler: an item's tints
     * are a list in its item model json now, and a source's position in that list is the tint index it
     * answers for. Index 0 is the index every colorizer shape stamps on its faces.
     */
    /**
     * A colorizer's {@link MultiVariant}, written through {@link
     * SpecificationBlockStateModelBuilder} so the colorizer's own {@code BlockStateModel} reaches
     * the blockstate layer, the only one that sees the block entity. A plain variant bakes once,
     * and every colorizer would draw its empty state.
     */
    static MultiVariant colorizerVariant(Identifier model) {
        return SpecificationBlockStateModelBuilder.specificationVariant(model);
    }

    /**
     * The item model for a colorizer block: a {@link ColorizerItemModel}, which reads the stack's
     * stored block where a {@code minecraft:model} would always draw the empty state. The tint
     * colours a stored grass or leaf block.
     */
    static void colorizerItem(BlockModelGenerators blockModels, Block b, Identifier model) {
        blockModels.itemModelOutput.accept(b.asItem(),
                new ColorizerItemModel.Unbaked(model, List.of(new ColorizerItemTintSource())));
    }

    static Identifier colorizerModel(BlockModelGenerators blockModels, String path, Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return colorizerModel(blockModels, path, parent, extra, Function.identity());
    }

    static Identifier colorizerModel(BlockModelGenerators blockModels, String path, Identifier parent, Consumer<ColorizerModelBuilder> extra,
                                      Function<ExtendedModelTemplateBuilder, ExtendedModelTemplateBuilder> perspective) {
        return perspective.apply(colorizerBuilder(parent, extra)).build().create(resource(path), colorizerParticle(), blockModels.modelOutput);
    }

    private Identifier colorizerObjModel(BlockModelGenerators blockModels, String path, Identifier objModel) {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(ColorizerObjModelBuilder::begin, loader -> loader.objModel(objModel));

        return defaultPerspectiveFlipped(builder).build().create(resource(path), colorizerParticle(), blockModels.modelOutput);
    }

    /**
     * The shape template is named twice: inside the {@code colorizer} object, which the loader
     * bakes, and as the json's own {@code parent}, the only way the item inherits the template's
     * display transforms. The loader's geometry still wins over the parent's.
     */
    private static ExtendedModelTemplateBuilder colorizerBuilder(Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return ExtendedModelTemplateBuilder.builder()
                .parent(parent)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .customLoader(ColorizerModelBuilder::begin, loader -> extra.accept(loader.colorizer(parent)));
    }

    /**
     * Builds the colorizer loader template {@link ColorizerItemModelProvider} needs for the brush, which is
     * the one colorizer model that belongs to an item rather than a block.
     */
    static ModelTemplate colorizerTemplate(Identifier parent, Consumer<ColorizerModelBuilder> extra) {
        return colorizerBuilder(parent, extra).build();
    }

    static TextureMapping colorizerParticle() {
        return new TextureMapping().put(TextureSlot.PARTICLE, COLORIZER_PARTICLE);
    }

    /**
     * The block sized item perspectives the 1.20.1 provider stamped on {@code tinted_cube} and on the
     * counter. They were an inline {@code transforms()} block on the old builder and are template
     * transforms now; the values are copied across unchanged.
     */
    private static ExtendedModelTemplateBuilder defaultPerspective(ExtendedModelTemplateBuilder builder) {
        return perspectives(builder, 225.0F);
    }

    /**
     * The same perspectives with the GUI view turned the other way, which is what every OBJ colorizer
     * used.
     */
    private static ExtendedModelTemplateBuilder defaultPerspectiveFlipped(ExtendedModelTemplateBuilder builder) {
        return perspectives(builder, 30.0F);
    }

    private static ExtendedModelTemplateBuilder perspectives(ExtendedModelTemplateBuilder builder, float guiYRot) {
        return builder
                .transform(ItemDisplayContext.GUI, t -> t.rotation(30.0F, guiYRot, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.625F))
                .transform(ItemDisplayContext.GROUND, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 3.0F, 0.0F).scale(0.25F))
                .transform(ItemDisplayContext.FIXED, t -> t.rotation(0.0F, 0.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.5F))
                .transform(ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, t -> t.rotation(75.0F, 45.0F, 0.0F).translation(0.0F, 2.5F, 0.0F).scale(0.375F))
                .transform(ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, t -> t.rotation(0.0F, 45.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F))
                .transform(ItemDisplayContext.FIRST_PERSON_LEFT_HAND, t -> t.rotation(0.0F, 225.0F, 0.0F).translation(0.0F, 0.0F, 0.0F).scale(0.4F));
    }

    // ------------------------------------------------------------------ helpers

    private static int countConnections(boolean... connections) {
        int numTrue = 0;
        for (boolean connection : connections) {
            numTrue += connection ? 1 : 0;
        }
        return numTrue;
    }

    private static int sideYRot(AttachFace face, Direction facing) {
        return (((int) facing.toYRot() + 180) + (face == AttachFace.CEILING ? 180 : 0)) % 360;
    }

    /**
     * A model with an explicit rotation, the shape {@code ConfiguredModel.builder().rotationX(..)
     * .rotationY(..).uvLock(..)} used to take. Angles are reduced modulo 360 because a
     * {@link com.mojang.math.Quadrant} only has four values.
     */
    private static MultiVariant variant(Identifier model, int xRot, int yRot, boolean uvLock) {
        MultiVariant variant = colorizerVariant(model).with(xRot(xRot)).with(yRot(yRot));
        return uvLock ? variant.with(BlockModelGenerators.UV_LOCK) : variant;
    }

    private static VariantMutator xRot(int degrees) {
        return switch (Math.floorMod(degrees, 360) / 90) {
            case 1 -> BlockModelGenerators.X_ROT_90;
            case 2 -> BlockModelGenerators.X_ROT_180;
            case 3 -> BlockModelGenerators.X_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
    }

    private static VariantMutator yRot(int degrees) {
        return switch (Math.floorMod(degrees, 360) / 90) {
            case 1 -> BlockModelGenerators.Y_ROT_90;
            case 2 -> BlockModelGenerators.Y_ROT_180;
            case 3 -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
    }

    /**
     * Forge's {@code forAllStatesExcept}: one variant per block state, keyed by every property except
     * the ignored ones. {@link PropertyDispatch} carries at most five properties, and the table needs
     * eight, so this is the only shape that can express it.
     */
    private static BlockModelDefinitionGenerator forEachState(Block block, Function<BlockState, MultiVariant> factory, Property<?>... ignored) {
        List<Property<?>> ignoredList = List.of(ignored);
        return new BlockModelDefinitionGenerator() {
            @Override
            public Block block() {
                return block;
            }

            @Override
            public BlockStateModelDispatcher create() {
                Map<String, BlockStateModel.Unbaked> variants = new HashMap<>();
                for (BlockState state : block.getStateDefinition().getPossibleStates()) {
                    PropertyValueList key = PropertyValueList.EMPTY;
                    for (Property<?> property : state.getProperties()) {
                        if (!ignoredList.contains(property)) {
                            key = key.extend(property.value(state));
                        }
                    }
                    variants.putIfAbsent(key.getKey(), factory.apply(state).toUnbaked());
                }
                return new BlockStateModelDispatcher(Optional.of(new BlockStateModelDispatcher.SimpleModelSelectors(variants)), Optional.empty());
            }
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
