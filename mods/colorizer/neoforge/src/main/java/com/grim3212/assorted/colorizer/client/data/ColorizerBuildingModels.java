package com.grim3212.assorted.colorizer.client.data;

import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.building.BeamBlock;
import com.grim3212.assorted.colorizer.common.blocks.building.ColumnBlock;
import com.grim3212.assorted.colorizer.common.blocks.building.ColumnPart;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.ConditionBuilder;
import net.minecraft.client.data.models.blockstates.MultiPartGenerator;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.VariantMutator;
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
 * Models for the colorizer panel, beam and column, the building block shapes cut from a colorizer.
 * UVs are left to follow the boxes, as the 1.2.4 renderer drew them.
 */
final class ColorizerBuildingModels {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");
    /** The broad face of a column's base and capital. */
    static final TextureSlot PLINTH = TextureSlot.create("plinth");

    private final BlockModelGenerators blockModels;

    ColorizerBuildingModels(BlockModelGenerators blockModels) {
        this.blockModels = blockModels;
    }

    void run() {
        this.panel(ColorizerBlocks.COLORIZER_PANEL.get(), new Colorized(this.blockModels));
        this.beam(ColorizerBlocks.COLORIZER_BEAM.get(), new Colorized(this.blockModels));
        this.column(ColorizerBlocks.COLORIZER_COLUMN.get(), new Colorized(this.blockModels));
    }

    /** One model per part, upright; the blockstate lays it along X or Z with its capital towards {@link ColumnBlock#top}. */
    private void column(Block column, Colorized models) {
        Map<ColumnPart, Identifier> parts = new EnumMap<>(ColumnPart.class);
        for (ColumnPart part : ColumnPart.values()) {
            ExtendedModelTemplateBuilder builder = template(TextureSlot.SIDE, TextureSlot.END, PLINTH);
            switch (part) {
                case SHAFT -> builder.element(e -> box(e, 2, 0, 2, 14, 16, 14));
                case BASE -> builder.element(e -> box(e, 0, 0, 0, 16, 6, 16))
                        .element(e -> box(e, 2, 6, 2, 14, 16, 14, Direction.DOWN));
                case CAPITAL -> builder.element(e -> box(e, 2, 0, 2, 14, 10, 14, Direction.UP))
                        .element(e -> box(e, 0, 10, 0, 16, 16, 16));
                case SINGLE -> builder.element(e -> box(e, 0, 0, 0, 16, 6, 16))
                        .element(e -> box(e, 2, 6, 2, 14, 10, 14, Direction.UP, Direction.DOWN))
                        .element(e -> box(e, 0, 10, 0, 16, 16, 16));
            }
            parts.put(part, models.model(column, builder, "_" + part.getSerializedName()));
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
    private void panel(Block panel, Colorized models) {
        Identifier up = models.model(panel, template(TextureSlot.TEXTURE).element(e -> box(e, 0, 15, 0, 16, 16, 16)), "_up");
        Identifier down = models.model(panel, template(TextureSlot.TEXTURE).element(e -> box(e, 0, 0, 0, 16, 1, 16)), "_down");
        MultiPartGenerator generator = MultiPartGenerator.multiPart(panel)
                .with(BlockModelGenerators.condition().term(MultifaceBlock.getFaceProperty(Direction.UP), true), models.variant(up))
                .with(BlockModelGenerators.condition().term(MultifaceBlock.getFaceProperty(Direction.DOWN), true), models.variant(down));

        for (int mask = 0; mask < 8; mask++) {
            boolean yields = (mask & 1) != 0, ceiling = (mask & 2) != 0, floor = (mask & 4) != 0;
            ExtendedModelTemplateBuilder wall = template(TextureSlot.TEXTURE)
                    .element(e -> box(e, 0, floor ? 1 : 0, 0, yields ? 15 : 16, ceiling ? 15 : 16, 1));
            Identifier model = models.model(panel, wall, "_wall" + (yields ? "_short" : "") + (ceiling ? "_under_ceiling" : "") + (floor ? "_over_floor" : ""));
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

    private void beam(Block beam, Colorized models) {
        TextureSlot t = TextureSlot.TEXTURE;
        Map<Half, Identifier> cores = Map.of(
                Half.TOP, models.model(beam, template(t).element(e -> box(e, 5, 10, 0, 11, 16, 16)), "_top"),
                Half.BOTTOM, models.model(beam, template(t).element(e -> box(e, 5, 0, 0, 11, 6, 16)), "_bottom"));
        // An arm stops at the core, so its inner face is buried and left out.
        Map<Half, Identifier> arms = Map.of(
                Half.TOP, models.model(beam, template(t).element(e -> box(e, 5, 10, 0, 11, 16, 5, Direction.SOUTH)), "_arm_top"),
                Half.BOTTOM, models.model(beam, template(t).element(e -> box(e, 5, 0, 0, 11, 6, 5, Direction.SOUTH)), "_arm_bottom"));

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

    /**
     * A box whose faces on the edge of the block cull against it, less any faces buried in another box.
     * Every face draws {@code #stored}, tinted as the stored block is.
     */
    private static ElementBuilder box(ElementBuilder e, float x1, float y1, float z1, float x2, float y2, float z2, Direction... hidden) {
        return e.from(x1, y1, z1).to(x2, y2, z2).allFacesExcept((dir, face) -> {
            face.texture(ColorizerBlockstateProvider.STORED).tintindex(0);
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

    static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }
}
