package com.grim3212.assorted.hangeables.client.data;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.WallClockBlock;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
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
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link HangeablesItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class HangeablesBlockstateProvider extends ModelProvider {

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    private static final ModelTemplate CALENDAR = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.ALL)
            .element(e -> e.from(4, 2, 0).to(12, 15, 1).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.ALL).uvs(14, 1, 16, 15);
                    case NORTH -> face.texture(TextureSlot.ALL).uvs(0, 0, 4, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.ALL).uvs(2.5F, 0F, 13.5F, 16F);
                    case WEST -> face.texture(TextureSlot.ALL).uvs(0, 1, 2, 15);
                    case DOWN -> face.texture(TextureSlot.ALL).uvs(12, 2, 4, 0);
                    case UP -> face.texture(TextureSlot.ALL).uvs(4, 0, 12, 2);
                }
            }))
            .build();

    /**
     * The wall clock's body. {@code front} is deliberately not a required slot: the base model never
     * fills it in, the sixty four dial models below do.
     */
    private static final ModelTemplate WALL_CLOCK_BODY = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.BACK)
            .requiredTextureSlot(TextureSlot.SIDE)
            .element(e -> e.from(0, 0, 0).to(2, 16, 16).allFaces((dir, face) -> {
                switch (dir) {
                    case EAST -> face.texture(TextureSlot.FRONT).uvs(0, 0, 16, 16);
                    case NORTH -> face.texture(TextureSlot.SIDE).uvs(14, 0, 16, 16).cullface(Direction.NORTH);
                    case SOUTH -> face.texture(TextureSlot.SIDE).uvs(0, 0, 2, 16).cullface(Direction.SOUTH);
                    case WEST -> face.texture(TextureSlot.BACK).uvs(0, 0, 16, 16).cullface(Direction.WEST);
                    case DOWN -> face.texture(TextureSlot.SIDE).uvs(16, 16, 14, 0).cullface(Direction.DOWN);
                    case UP -> face.texture(TextureSlot.SIDE).uvs(0, 0, 2, 16).cullface(Direction.UP);
                }
            }))
            .build();

    private static final ModelTemplate WALL_CLOCK_DIAL = ExtendedModelTemplateBuilder.builder()
            .parent(resource("block/wall_clock"))
            .requiredTextureSlot(TextureSlot.FRONT)
            .build();

    public HangeablesBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Hangeables block states";
    }

    /**
     * Only the block items belong here; every other item is {@link HangeablesItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        neonSigns(blockModels);
        calendar(blockModels);
        wallClock(blockModels);
    }

    /**
     * Both neon signs draw only a particle; {@code NeonSignBlockEntityRenderer} submits the board.
     * The item, {@code HangeablesItems.NEON_SIGN}, is a {@link BlockItem}, so its flat sprite is written
     * here and not by {@link HangeablesItemModelProvider}.
     */
    private void neonSigns(BlockModelGenerators blockModels) {
        Identifier model = ModelTemplates.PARTICLE_ONLY.create(resource("block/" + name(HangeablesBlocks.NEON_SIGN.get())),
                TextureMapping.particle(new Material(Identifier.withDefaultNamespace("block/obsidian"))), blockModels.modelOutput);

        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(HangeablesBlocks.NEON_SIGN.get(), BlockModelGenerators.plainVariant(model)));
        blockModels.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(HangeablesBlocks.NEON_SIGN_WALL.get(), BlockModelGenerators.plainVariant(model)));

        Item sign = HangeablesItems.NEON_SIGN.get();
        Identifier itemModel = ModelTemplates.FLAT_ITEM.create(resource("item/neon_sign"),
                TextureMapping.layer0(texture("item/neon_sign")), blockModels.modelOutput);
        blockModels.registerSimpleItemModel(sign, itemModel);
    }

    private void calendar(BlockModelGenerators blockModels) {
        Block b = HangeablesBlocks.CALENDAR.get();
        Material calendar = texture("block/calendar");
        Identifier model = CALENDAR.create(resource("block/calendar"), new TextureMapping()
                .put(TextureSlot.PARTICLE, calendar).put(TextureSlot.ALL, calendar), blockModels.modelOutput);

        // rotationY(toYRot()) - south 0, west 90, north 180, east 270 - is ROTATION_HORIZONTAL_FACING_ALT.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b, BlockModelGenerators.plainVariant(model))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING_ALT));

        blockModels.registerSimpleItemModel(b, ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)),
                TextureMapping.layer0(texture("item/" + name(b))), blockModels.modelOutput));
    }

    private void wallClock(BlockModelGenerators blockModels) {
        Block b = HangeablesBlocks.WALL_CLOCK.get();
        Material planks = new Material(Identifier.withDefaultNamespace("block/oak_planks"));
        WALL_CLOCK_BODY.create(resource("block/wall_clock"), new TextureMapping()
                .put(TextureSlot.PARTICLE, planks).put(TextureSlot.BACK, planks).put(TextureSlot.SIDE, planks), blockModels.modelOutput);

        List<Identifier> dials = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            Identifier dial = resource("block/wall_clock/wall_clock_" + (i + 1));
            dials.add(WALL_CLOCK_DIAL.create(dial, new TextureMapping().put(TextureSlot.FRONT, new Material(dial)), blockModels.modelOutput));
        }

        // rotationY((toYRot() + 90) % 360) - east 0, south 90, west 180, north 270 - is ROTATION_TORCH.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(WallClockBlock.TIME).generate(time -> BlockModelGenerators.plainVariant(dials.get(time))))
                .with(BlockModelGenerators.ROTATION_TORCH));

        blockModels.registerSimpleItemModel(b, ModelTemplates.FLAT_ITEM.create(resource("item/" + name(b)),
                TextureMapping.layer0(texture("item/" + name(b))), blockModels.modelOutput));
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
