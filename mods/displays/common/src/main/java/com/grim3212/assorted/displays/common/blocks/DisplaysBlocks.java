package com.grim3212.assorted.displays.common.blocks;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.displays.Family;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class DisplaysBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Family.ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<CageBlock> CAGE = register("cage", props -> new CageBlock(props.mapColor(MapColor.METAL).sound(SoundType.METAL).strength(0.8F, 5.0F).requiresCorrectToolForDrops().noOcclusion().isValidSpawn(DisplaysBlocks::never).isRedstoneConductor(DisplaysBlocks::never).isSuffocating(DisplaysBlocks::never).isViewBlocking(DisplaysBlocks::never)));

    public static final IRegistryObject<DisplayCaseBlock> WOODEN_DISPLAY_CASE = register("wooden_display_case", props -> new DisplayCaseBlock(displayCase(props).mapColor(MapColor.WOOD)));
    public static final IRegistryObject<DisplayCaseBlock> STONE_DISPLAY_CASE = register("stone_display_case", props -> new DisplayCaseBlock(displayCase(props).mapColor(MapColor.STONE)));
    public static final IRegistryObject<DisplayCaseBlock> IRON_DISPLAY_CASE = register("iron_display_case", props -> new DisplayCaseBlock(displayCase(props).mapColor(MapColor.METAL)));
    public static final IRegistryObject<DisplayCaseBlock> GOLD_DISPLAY_CASE = register("gold_display_case", props -> new DisplayCaseBlock(displayCase(props).mapColor(MapColor.GOLD)));
    public static final IRegistryObject<DisplayCaseBlock> DIAMOND_DISPLAY_CASE = register("diamond_display_case", props -> new DisplayCaseBlock(displayCase(props).mapColor(MapColor.DIAMOND)));
    /**
     * Copper comes in eight, as it does everywhere in vanilla: four oxidation stages, waxed and
     * unwaxed. Only the four unwaxed ones tick; a waxed case never changes, so it is a plain
     * display case and is not given {@code randomTicks}.
     */
    public static final WeatheringCopperCollection<IRegistryObject<DisplayCaseBlock>> COPPER_DISPLAY_CASES = registerCopperDisplayCases();

    public static final IRegistryObject<MuseumDisplayCaseBlock> MUSEUM_DISPLAY_CASE = register("museum_display_case", props -> new MuseumDisplayCaseBlock(displayCase(props).mapColor(MapColor.WOOD).sound(SoundType.WOOD).strength(1.0F, 5.0F)));

    private static WeatheringCopperCollection<IRegistryObject<DisplayCaseBlock>> registerCopperDisplayCases() {
        WeatheringCopperCollection<String> names = WeatheringCopperCollection.prefixWithState(WeatheringCopperCollection.create("copper_display_case"));
        return names.apply(
                weathering -> WeatheringCopperCollection.zipMap(WeatheringCopperCollection.STATES, weathering,
                        (age, name) -> register(name, props -> new WeatheringDisplayCaseBlock(age, copperDisplayCase(props, age, age != WeatherState.OXIDIZED)))),
                waxed -> WeatheringCopperCollection.zipMap(WeatheringCopperCollection.STATES, waxed,
                        (age, name) -> register(name, props -> new DisplayCaseBlock(copperDisplayCase(props, age, false)))));
    }

    /**
     * A copper case sounds and colours like vanilla's copper of the same stage. Only a case with a
     * stage left to reach is randomly ticked; ticking an oxidized one would roll a change that can
     * never happen.
     */
    private static BlockBehaviour.Properties copperDisplayCase(BlockBehaviour.Properties props, WeatherState age, boolean weathers) {
        if (weathers) {
            props.randomTicks();
        }

        return displayCase(props).sound(SoundType.COPPER).mapColor(switch (age) {
            case UNAFFECTED -> MapColor.COLOR_ORANGE;
            case EXPOSED -> MapColor.TERRACOTTA_LIGHT_GRAY;
            case WEATHERED -> MapColor.WARPED_STEM;
            case OXIDIZED -> MapColor.WARPED_NYLIUM;
        });
    }

    /**
     * Display cases are glass boxes: they hold their own light and their own spawns out, so what is
     * behind one stays lit and nothing wanders into the shelf.
     */
    private static BlockBehaviour.Properties displayCase(BlockBehaviour.Properties props) {
        return props.instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.5F, 3.0F).noOcclusion().isValidSpawn(DisplaysBlocks::never).isRedstoneConductor(DisplaysBlocks::never).isSuffocating(DisplaysBlocks::never).isViewBlocking(DisplaysBlocks::never);
    }

    public static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, block -> item(name, block));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory, Function<IRegistryObject<T>, Supplier<? extends Item>> itemCreator) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        ITEMS.register(name, itemCreator.apply(ret));
        return ret;
    }

    private static <T extends Block> IRegistryObject<T> registerNoItem(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        // Since 1.21.2 every block has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known.
        final ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return BLOCKS.register(name, () -> factory.apply(BlockBehaviour.Properties.of().setId(key)));
    }

    private static Supplier<BlockItem> item(final String name, final IRegistryObject<? extends Block> block) {
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return () -> new BlockItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    /** Every display case, the museum one last; the rest differ only in their frame. */
    public static List<IRegistryObject<? extends DisplayCaseBlock>> displayCaseBlocks() {
        List<IRegistryObject<? extends DisplayCaseBlock>> cases = new ArrayList<>(List.of(WOODEN_DISPLAY_CASE, STONE_DISPLAY_CASE));
        COPPER_DISPLAY_CASES.forEach(cases::add);
        cases.addAll(List.of(IRON_DISPLAY_CASE, GOLD_DISPLAY_CASE, DIAMOND_DISPLAY_CASE, MUSEUM_DISPLAY_CASE));
        return cases;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return false;
    }

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    public static void init() {
    }
}
