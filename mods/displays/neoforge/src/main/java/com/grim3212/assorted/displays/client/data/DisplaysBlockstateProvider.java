package com.grim3212.assorted.displays.client.data;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.blocks.DisplayCaseBlock;
import com.grim3212.assorted.displays.common.blocks.MuseumDisplayCaseBlock;
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
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.neoforged.neoforge.client.model.generators.template.ElementBuilder;
import net.neoforged.neoforge.client.model.generators.template.ExtendedModelTemplateBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Block states and block models. This owns every block and block item; {@link DisplaysItemModelProvider}
 * owns the rest, so the two never write the same file.
 */
public class DisplaysBlockstateProvider extends ModelProvider {

    /** The museum plinth's name plate, drawn a hair in front of its front face. */
    private static final TextureSlot PLACARD = TextureSlot.create("placard");

    /** What a display case's stepped riser is built out of; the frame's own material. */
    private static final TextureSlot SHELF = TextureSlot.create("shelf");

    private static final Identifier MC_BLOCK = Identifier.withDefaultNamespace("block/block");

    /**
     * The museum case's plinth: a carpeted wooden block with its placard hung off the north face.
     * The blockstate turns it to the case's facing, so the placard is modelled facing north.
     */
    private static final ModelTemplate MUSEUM_PLINTH = ExtendedModelTemplateBuilder.builder()
            .parent(MC_BLOCK)
            .requiredTextureSlot(TextureSlot.PARTICLE)
            .requiredTextureSlot(TextureSlot.SIDE)
            .requiredTextureSlot(TextureSlot.TOP)
            .requiredTextureSlot(PLACARD)
            .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(dir == Direction.UP ? TextureSlot.TOP : TextureSlot.SIDE).cullface(dir)))
            .element(e -> e.from(1, 4, -0.01F).to(15, 12, -0.01F).shade(false)
                    .face(Direction.NORTH, face -> face.texture(PLACARD).uvs(1, 4, 15, 12)))
            .build();

    /**
     * A display case: the glass shell, and inside it a riser of three steps, each one a third of the
     * case deep and four pixels taller than the one in front of it. A row of items stands on each
     * step, which is both what stops them floating and what the block's hit test measures its slot
     * boxes from - {@code DisplayCaseBlock.shelfY} holds the same three heights.
     * <p>
     * The riser is inset a pixel all round so none of its faces land on the shell's, which would
     * leave the two fighting over the same plane.
     */
    /** A riser for each of the three sizes a case can be grown to. */
    private static final Map<Integer, ModelTemplate> DISPLAY_CASES = IntStream.rangeClosed(1, DisplayCaseBlock.MAX_SIZE).boxed()
            .collect(Collectors.toMap(Function.identity(), DisplaysBlockstateProvider::displayCaseTemplate));

    private static ModelTemplate displayCaseTemplate(int size) {
        ExtendedModelTemplateBuilder builder = ExtendedModelTemplateBuilder.builder()
                .parent(MC_BLOCK)
                .requiredTextureSlot(TextureSlot.PARTICLE)
                .requiredTextureSlot(TextureSlot.ALL)
                .requiredTextureSlot(SHELF)
                .element(e -> e.from(0, 0, 0).to(16, 16, 16).allFaces((dir, face) -> face.texture(TextureSlot.ALL).cullface(dir)));

        for (int row = 0; row < size; row++) {
            builder.element(step(size, row));
        }

        return builder.build();
    }

    /**
     * One step of the riser, at the height {@link DisplayCaseBlock#shelfY} puts that row's shelf.
     * Its underside is never seen through the floor it stands on.
     */
    private static Consumer<ElementBuilder> step(int size, int row) {
        float front = 1.0F + 14.0F * row / size;
        float back = 1.0F + 14.0F * (row + 1) / size;
        float top = (float) (DisplayCaseBlock.shelfY(size, row) * 16.0D);
        return e -> e.from(1, 0, front).to(15, top, back)
                .allFacesExcept((dir, face) -> face.texture(SHELF), Set.of(Direction.DOWN));
    }

    public DisplaysBlockstateProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    public String getName() {
        return "Assorted Displays block states";
    }

    /**
     * Only the block items belong here; every other item is {@link DisplaysItemModelProvider}'s, so the
     * two providers never write the same file.
     */
    @Override
    protected Stream<? extends Holder<Item>> getKnownItems() {
        return super.getKnownItems().filter(holder -> holder.value() instanceof BlockItem);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        blockModels.createTrivialCube(DisplaysBlocks.CAGE.get());

        displayCases(blockModels);
    }

    /** Every case's riser is made of whatever its frame is. */
    private void displayCases(BlockModelGenerators blockModels) {
        displayCase(blockModels, DisplaysBlocks.WOODEN_DISPLAY_CASE.get(), vanilla("block/oak_planks"));
        displayCase(blockModels, DisplaysBlocks.STONE_DISPLAY_CASE.get(), vanilla("block/stone"));
        displayCase(blockModels, DisplaysBlocks.IRON_DISPLAY_CASE.get(), vanilla("block/iron_block"));
        displayCase(blockModels, DisplaysBlocks.GOLD_DISPLAY_CASE.get(), vanilla("block/gold_block"));
        displayCase(blockModels, DisplaysBlocks.DIAMOND_DISPLAY_CASE.get(), vanilla("block/diamond_block"));

        copperDisplayCases(blockModels);
        museumDisplayCase(blockModels, DisplaysBlocks.MUSEUM_DISPLAY_CASE.get());
    }

    /**
     * The three models one case needs, keyed by size. The unsuffixed name is the case as it is
     * placed, showing a single item, which is also what its item form draws.
     */
    private Map<Integer, Identifier> caseModels(BlockModelGenerators blockModels, String name, Material frame, Material shelf) {
        Map<Integer, Identifier> models = new HashMap<>();
        TextureMapping textures = new TextureMapping()
                .put(TextureSlot.PARTICLE, frame).put(TextureSlot.ALL, frame).put(SHELF, shelf);

        DISPLAY_CASES.forEach((size, template) ->
                models.put(size, template.create(resource("block/" + name + (size == 1 ? "" : "_" + size + "x" + size)), textures, blockModels.modelOutput)));
        return models;
    }

    /**
     * The dispatch every case shares: a riser per size, turned to the case's facing. The riser is
     * drawn facing north, so leaving off the rotation leaves every case but a north facing one with
     * its steps across the grain of the items standing on them.
     */
    private void displayCaseStates(BlockModelGenerators blockModels, Block b, Map<Integer, Identifier> models) {
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(DisplayCaseBlock.SIZE).generate(size -> BlockModelGenerators.plainVariant(models.get(size))))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));
    }

    private void displayCase(BlockModelGenerators blockModels, Block b, Material shelf) {
        Map<Integer, Identifier> models = caseModels(blockModels, name(b), texture("block/" + name(b)), shelf);
        displayCaseStates(blockModels, b, models);
        blockModels.registerSimpleItemModel(b, models.get(1));
    }

    /**
     * The four oxidation stages, each drawn by one model that its waxed twin points at too - waxed
     * copper is the same block to look at, and vanilla shares its model the same way. Each stage's
     * riser is copper of that same stage.
     */
    private void copperDisplayCases(BlockModelGenerators blockModels) {
        WeatheringCopperCollection.ByState<Material> shelves = new WeatheringCopperCollection.ByState<>(
                vanilla("block/copper_block"), vanilla("block/exposed_copper"), vanilla("block/weathered_copper"), vanilla("block/oxidized_copper"));

        WeatheringCopperCollection.ByState<Map<Integer, Identifier>> models = WeatheringCopperCollection.zipMap(
                DisplaysBlocks.COPPER_DISPLAY_CASES.weathering(), shelves,
                (displayCase, shelf) -> caseModels(blockModels, name(displayCase.get()), texture("block/" + name(displayCase.get())), shelf));

        WeatheringCopperCollection.zipApply(DisplaysBlocks.COPPER_DISPLAY_CASES, new WeatheringCopperCollection<>(models, models), (displayCase, sized) -> {
            displayCaseStates(blockModels, displayCase.get(), sized);
            blockModels.registerSimpleItemModel(displayCase.get(), sized.get(1));
        });
    }

    private void museumDisplayCase(BlockModelGenerators blockModels, Block b) {
        Material base = texture("block/museum_display_case_base");
        Material glass = texture("block/museum_display_case");

        Identifier plinth = MUSEUM_PLINTH.create(resource("block/museum_display_case_plinth"), new TextureMapping()
                .put(TextureSlot.PARTICLE, base)
                .put(TextureSlot.SIDE, base)
                .put(TextureSlot.TOP, texture("block/museum_display_case_base_top"))
                .put(PLACARD, texture("block/museum_display_case_placard")), blockModels.modelOutput);
        // The museum riser is the plinth's own wood, so the case reads as one piece of furniture.
        Map<Integer, Identifier> glassCases = caseModels(blockModels, "museum_display_case_glass", glass, base);

        // Only the glass half has a riser, but both halves carry the size, so the plinth answers
        // every size with the one model it has.
        blockModels.blockStateOutput.accept(MultiVariantGenerator.dispatch(b)
                .with(PropertyDispatch.initial(MuseumDisplayCaseBlock.HALF, DisplayCaseBlock.SIZE).generate((half, size) ->
                        BlockModelGenerators.plainVariant(half == DoubleBlockHalf.LOWER ? plinth : glassCases.get(size))))
                .with(BlockModelGenerators.ROTATION_HORIZONTAL_FACING));

        // The 1.4.6 icon, which shows both halves at once - neither block model does.
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

    private static Material vanilla(String path) {
        return new Material(Identifier.withDefaultNamespace(path));
    }
}
