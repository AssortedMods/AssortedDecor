package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BeamBlock;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.ColumnBlock;
import com.grim3212.assorted.buildingblocks.common.blocks.building.ColumnPart;
import com.grim3212.assorted.buildingblocks.common.blocks.building.CutShapes;
import com.grim3212.assorted.buildingblocks.common.blocks.building.HorizontalAxisBlock;
import com.grim3212.assorted.buildingblocks.common.blocks.building.StoneFamily;
import com.grim3212.assorted.buildingblocks.common.blocks.building.WoodSet;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * Models for {@link BuildingBlocks} and the lumber mill. UVs are left to follow the boxes, as the
 * 1.2.4 renderer drew them.
 */
final class BuildingBlockModels {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");
    /** The broad face of a column's base and capital. */
    private static final TextureSlot PLINTH = TextureSlot.create("plinth");
    private static final TextureSlot SAW = TextureSlot.create("saw");

    /** Vanilla's stonecutter, retextured: its saw is the same animated blade. */
    private static final ModelTemplate LUMBER_MILL = ExtendedModelTemplateBuilder.builder().parent(Identifier.withDefaultNamespace("block/stonecutter"))
            .requiredTextureSlot(TextureSlot.PARTICLE).requiredTextureSlot(TextureSlot.TOP).requiredTextureSlot(TextureSlot.SIDE)
            .requiredTextureSlot(TextureSlot.BOTTOM).requiredTextureSlot(SAW).build();

    /** Along X, the bone poking a pixel out of either cut end. */
    private static final ModelTemplate MEAT = template(TextureSlot.SIDE, TextureSlot.END)
            .element(e -> e.from(1, 0, 0).to(15, 16, 16).allFaces((dir, face) -> face
                    .texture(dir.getAxis() == Direction.Axis.X ? TextureSlot.END : TextureSlot.SIDE)
                    .cullface(dir.getAxis() == Direction.Axis.X ? null : dir)))
            .element(e -> e.from(0, 6, 6).to(16, 10, 10).allFaces((dir, face) -> face
                    .texture(dir.getAxis() == Direction.Axis.X ? TextureSlot.END : TextureSlot.SIDE)
                    .cullface(dir.getAxis() == Direction.Axis.X ? dir : null)))
            .build();

    private static final PropertyDispatch<VariantMutator> MEAT_AXIS = PropertyDispatch.modify(HorizontalAxisBlock.AXIS)
            .select(Direction.Axis.X, BlockModelGenerators.NOP)
            .select(Direction.Axis.Z, BlockModelGenerators.Y_ROT_90);

    private final BlockModelGenerators blockModels;

    BuildingBlockModels(BlockModelGenerators blockModels) {
        this.blockModels = blockModels;
    }

    void run() {
        Block lumberMill = BuildingBlocksBlocks.LUMBER_MILL.get();
        Identifier lumberMillModel = LUMBER_MILL.create(lumberMill, new TextureMapping().put(TextureSlot.PARTICLE, own("lumber_mill_bottom"))
                .put(TextureSlot.TOP, own("lumber_mill_top")).put(TextureSlot.SIDE, own("lumber_mill_side")).put(TextureSlot.BOTTOM, own("lumber_mill_bottom"))
                .put(SAW, new Material(Identifier.withDefaultNamespace("block/stonecutter_saw"))), this.blockModels.modelOutput);
        this.blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(lumberMill, BlockModelGenerators.plainVariant(lumberMillModel)).with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

        for (IRegistryObject<Block> cube : BuildingBlocks.cubes()) {
            BlockModelGenerators.BlockFamilyProvider family = this.blockModels.new BlockFamilyProvider(TextureMapping.cube(texture(cube.get())))
                    .fullBlock(cube.get(), ModelTemplates.CUBE_ALL);
            CutShapes cuts = BuildingBlocks.cuts().get(cube);
            if (cuts != null) {
                family.slab(cuts.slab().get()).stairs(cuts.stairs().get());
                if (cuts.wall() != null) {
                    family.wall(cuts.wall().get());
                }
            }
        }

        for (StoneFamily stone : BuildingBlocks.stones()) {
            Block fluted = stone.fluted().get();
            Identifier flutedModel = ModelTemplates.CUBE_COLUMN.create(fluted, TextureMapping.column(texture(fluted), texture(name(fluted) + "_top")), this.blockModels.modelOutput);
            this.blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(fluted, BlockModelGenerators.plainVariant(flutedModel)).with(BlockModelGenerators.createRotatedPillar()));
            String column = name(stone.column().get());
            this.column(stone.column().get(), new Textured(this.blockModels, part -> new TextureMapping().put(TextureSlot.PARTICLE, texture(column))
                    .put(TextureSlot.SIDE, texture(part == ColumnPart.SHAFT ? column : column + "_" + part.getSerializedName()))
                    .put(TextureSlot.END, texture(column + "_top")).put(PLINTH, texture(column + "_plinth"))));
        }

        for (WoodSet wood : BuildingBlocks.woods()) {
            Material planks = new Material(Identifier.withDefaultNamespace("block/" + wood.name() + "_planks"));
            this.panel(wood.panel().get(), new Textured(this.blockModels, part -> single(planks)));
            this.beam(wood.beam().get(), new Textured(this.blockModels, part -> single(planks)));
        }
        this.beam(BuildingBlocks.IRON_BEAM.get(), new Textured(this.blockModels, part -> single(texture(BuildingBlocks.IRON_BRICKS.get()))));

        Identifier meat = MEAT.create(BuildingBlocks.MEAT_BLOCK.get(), new TextureMapping()
                .put(TextureSlot.PARTICLE, texture("meat_block"))
                .put(TextureSlot.SIDE, texture("meat_block"))
                .put(TextureSlot.END, texture("meat_block_end")), this.blockModels.modelOutput);
        this.blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(BuildingBlocks.MEAT_BLOCK.get(), BlockModelGenerators.plainVariant(meat)).with(MEAT_AXIS));
    }

    /** One model per part, upright; the blockstate lays it along X or Z with its capital towards {@link ColumnBlock#top}. */
    private void column(Block column, ShapeModels models) {
        Map<ColumnPart, Identifier> parts = new EnumMap<>(ColumnPart.class);
        for (ColumnPart part : ColumnPart.values()) {
            ExtendedModelTemplateBuilder builder = template(TextureSlot.SIDE, TextureSlot.END, PLINTH);
            switch (part) {
                case SHAFT -> builder.element(e -> box(e, 2, 0, 2, 14, 16, 14, TextureSlot.SIDE, TextureSlot.END, TextureSlot.END));
                case BASE -> builder.element(e -> box(e, 0, 0, 0, 16, 6, 16, TextureSlot.SIDE, PLINTH, PLINTH))
                        .element(e -> box(e, 2, 6, 2, 14, 16, 14, TextureSlot.SIDE, TextureSlot.END, TextureSlot.END, Direction.DOWN));
                case CAPITAL -> builder.element(e -> box(e, 2, 0, 2, 14, 10, 14, TextureSlot.SIDE, TextureSlot.END, TextureSlot.END, Direction.UP))
                        .element(e -> box(e, 0, 10, 0, 16, 16, 16, TextureSlot.SIDE, PLINTH, PLINTH));
                case SINGLE -> builder.element(e -> box(e, 0, 0, 0, 16, 6, 16, TextureSlot.SIDE, PLINTH, PLINTH))
                        .element(e -> box(e, 2, 6, 2, 14, 10, 14, TextureSlot.SIDE, TextureSlot.END, TextureSlot.END, Direction.UP, Direction.DOWN))
                        .element(e -> box(e, 0, 10, 0, 16, 16, 16, TextureSlot.SIDE, PLINTH, PLINTH));
            }
            parts.put(part, models.model(column, builder, "_" + part.getSerializedName(), part));
        }
        this.blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(column).with(PropertyDispatch.initial(ColumnBlock.AXIS, ColumnBlock.PART).generate((axis, part) -> {
            MultiVariant variant = models.variant(parts.get(part));
            return switch (axis) {
                case Y -> variant;
                case Z -> variant.with(BlockModelGenerators.X_ROT_90);
                case X -> variant.with(BlockModelGenerators.X_ROT_90).with(BlockModelGenerators.Y_ROT_90);
            };
        })));
        models.item(column, parts.get(ColumnPart.SINGLE));
    }

    /**
     * A board per face. Where two walls meet, the one clockwise keeps its full length, and walls step in
     * a pixel for a floor or ceiling, so no two boards overlap.
     */
    private void panel(Block panel, ShapeModels models) {
        Identifier up = models.model(panel, template(TextureSlot.TEXTURE).element(e -> box(e, 0, 15, 0, 16, 16, 16, TextureSlot.TEXTURE, TextureSlot.TEXTURE, TextureSlot.TEXTURE)), "_up", null);
        Identifier down = models.model(panel, template(TextureSlot.TEXTURE).element(e -> box(e, 0, 0, 0, 16, 1, 16, TextureSlot.TEXTURE, TextureSlot.TEXTURE, TextureSlot.TEXTURE)), "_down", null);
        MultiPartGenerator generator = MultiPartGenerator.multiPart(panel)
                .with(BlockModelGenerators.condition().term(MultifaceBlock.getFaceProperty(Direction.UP), true), models.variant(up))
                .with(BlockModelGenerators.condition().term(MultifaceBlock.getFaceProperty(Direction.DOWN), true), models.variant(down));

        for (int mask = 0; mask < 8; mask++) {
            boolean yields = (mask & 1) != 0, ceiling = (mask & 2) != 0, floor = (mask & 4) != 0;
            ExtendedModelTemplateBuilder wall = template(TextureSlot.TEXTURE)
                    .element(e -> box(e, 0, floor ? 1 : 0, 0, yields ? 15 : 16, ceiling ? 15 : 16, 1, TextureSlot.TEXTURE, TextureSlot.TEXTURE, TextureSlot.TEXTURE));
            Identifier model = models.model(panel, wall, "_wall" + (yields ? "_short" : "") + (ceiling ? "_under_ceiling" : "") + (floor ? "_over_floor" : ""), null);
            for (Direction face : Direction.Plane.HORIZONTAL) {
                ConditionBuilder condition = BlockModelGenerators.condition()
                        .term(MultifaceBlock.getFaceProperty(face), true)
                        .term(MultifaceBlock.getFaceProperty(face.getClockWise()), yields)
                        .term(MultifaceBlock.getFaceProperty(Direction.UP), ceiling)
                        .term(MultifaceBlock.getFaceProperty(Direction.DOWN), floor);
                generator.with(condition, models.variant(model).with(yRot(face)));
            }
        }
        this.blockModels.blockStateOutput.accept(generator);
        models.item(panel, down);
    }

    private void beam(Block beam, ShapeModels models) {
        TextureSlot t = TextureSlot.TEXTURE;
        Map<Half, Identifier> cores = Map.of(
                Half.TOP, models.model(beam, template(t).element(e -> box(e, 5, 10, 0, 11, 16, 16, t, t, t)), "_top", null),
                Half.BOTTOM, models.model(beam, template(t).element(e -> box(e, 5, 0, 0, 11, 6, 16, t, t, t)), "_bottom", null));
        // An arm stops at the core, so its inner face is buried and left out.
        Map<Half, Identifier> arms = Map.of(
                Half.TOP, models.model(beam, template(t).element(e -> box(e, 5, 10, 0, 11, 16, 5, t, t, t, Direction.SOUTH)), "_arm_top", null),
                Half.BOTTOM, models.model(beam, template(t).element(e -> box(e, 5, 0, 0, 11, 6, 5, t, t, t, Direction.SOUTH)), "_arm_bottom", null));

        MultiPartGenerator generator = MultiPartGenerator.multiPart(beam);
        for (Half half : Half.values()) {
            generator.with(BlockModelGenerators.condition().term(BeamBlock.HALF, half).term(BeamBlock.AXIS, Direction.Axis.Z), models.variant(cores.get(half)))
                    .with(BlockModelGenerators.condition().term(BeamBlock.HALF, half).term(BeamBlock.AXIS, Direction.Axis.X), models.variant(cores.get(half)).with(BlockModelGenerators.Y_ROT_90));
            for (Direction side : Direction.Plane.HORIZONTAL) {
                generator.with(BlockModelGenerators.condition().term(BeamBlock.HALF, half).term(BeamBlock.arm(side), true), models.variant(arms.get(half)).with(yRot(side)));
            }
        }
        this.blockModels.blockStateOutput.accept(generator);
        models.item(beam, cores.get(Half.BOTTOM));
    }

    private static VariantMutator yRot(Direction facing) {
        return switch (facing) {
            case EAST -> BlockModelGenerators.Y_ROT_90;
            case SOUTH -> BlockModelGenerators.Y_ROT_180;
            case WEST -> BlockModelGenerators.Y_ROT_270;
            default -> BlockModelGenerators.NOP;
        };
    }

    private static ExtendedModelTemplateBuilder template(TextureSlot... slots) {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder().parent(MC_BLOCK).requiredTextureSlot(TextureSlot.PARTICLE);
        for (TextureSlot slot : slots) {
            builder.requiredTextureSlot(slot);
        }
        return builder;
    }

    /** A box whose faces on the edge of the block cull against it, less any faces buried in another box. */
    private static ElementBuilder box(ElementBuilder e, float x1, float y1, float z1, float x2, float y2, float z2,
                                      TextureSlot side, TextureSlot up, TextureSlot down, Direction... hidden) {
        return e.from(x1, y1, z1).to(x2, y2, z2).allFacesExcept((dir, face) -> {
            face.texture(switch (dir) {
                case UP -> up;
                case DOWN -> down;
                default -> side;
            });
            if (onEdge(dir, x1, y1, z1, x2, y2, z2)) {
                face.cullface(dir);
            }
        }, Set.of(hidden));
    }

    private static boolean onEdge(Direction dir, float x1, float y1, float z1, float x2, float y2, float z2) {
        return switch (dir) {
            case DOWN -> y1 == 0;
            case UP -> y2 == 16;
            case NORTH -> z1 == 0;
            case SOUTH -> z2 == 16;
            case WEST -> x1 == 0;
            case EAST -> x2 == 16;
        };
    }

    private static TextureMapping single(Material texture) {
        return new TextureMapping().put(TextureSlot.PARTICLE, texture).put(TextureSlot.TEXTURE, texture);
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    private static Material texture(Block block) {
        return texture(name(block));
    }

    private static Material texture(String name) {
        return new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/building/" + name));
    }

    private static Material own(String name) {
        return new Material(Identifier.fromNamespaceAndPath(Constants.MOD_ID, "block/" + name));
    }
}
