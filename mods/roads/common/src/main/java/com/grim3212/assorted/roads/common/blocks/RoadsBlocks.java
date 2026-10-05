package com.grim3212.assorted.roads.common.blocks;

import com.google.common.collect.Maps;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.roads.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RoadsBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<Block> STONE_PATH = register("stone_path", props -> new Block(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));

    /**
     * What makes the sidewalk the quicker surface. Not {@code friction}: below vanilla's 0.6
     * {@code LivingEntity#getFrictionInfluencedSpeed} no longer pays the acceleration back, so a low
     * friction only shortens the momentum carried between ticks. The factor multiplies horizontal
     * velocity every tick, settling a walk at {@code s / (1 - 0.546 * s)} times the base speed.
     */
    public static final float SIDEWALK_SPEED_FACTOR = 1.35F;

    public static final IRegistryObject<Block> SIDEWALK = register("sidewalk", props -> new Block(props.mapColor(MapColor.COLOR_LIGHT_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().speedFactor(SIDEWALK_SPEED_FACTOR)));

    public static final IRegistryObject<RoadwayBlock> ROADWAY = register("roadway", props -> new RoadwayBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<RoadwayManholeBlock> ROADWAY_MANHOLE = register("roadway_manhole", props -> new RoadwayManholeBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.METAL).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<RoadwayLightBlock> ROADWAY_LIGHT = register("roadway_light", props -> new RoadwayLightBlock(props.mapColor(MapColor.COLOR_GRAY).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops().lightLevel((b) -> b.getValue(RoadwayLightBlock.ACTIVE) ? 15 : 0)));

    public static final Map<DyeColor, IRegistryObject<RoadwayColorBlock>> ROADWAY_COLORS = Maps.newEnumMap(DyeColor.class);

    static {
        ROADWAY_COLORS.put(DyeColor.WHITE, register("roadway_white", props -> new RoadwayWhiteBlock(props.mapColor(MapColor.SNOW).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops())));
        Arrays.stream(DyeColor.values()).filter((c) -> c != DyeColor.WHITE).forEach((color) -> ROADWAY_COLORS.put(color, register("roadway_" + color.getName(), props -> new RoadwayColorBlock(color, props.mapColor(color.getMapColor()).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(1.0F, 15.0F).requiresCorrectToolForDrops()))));
    }

    private static <T extends Block> IRegistryObject<T> register(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        IRegistryObject<T> ret = registerNoItem(name, factory);
        ITEMS.register(name, item(name, ret));
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

    public static void init() {
    }
}
