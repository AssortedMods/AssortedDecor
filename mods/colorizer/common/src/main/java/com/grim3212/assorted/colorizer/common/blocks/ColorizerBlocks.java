package com.grim3212.assorted.colorizer.common.blocks;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.Family;
import com.grim3212.assorted.colorizer.api.colorizer.SlopeType;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.*;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ColorizerBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Family.ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<ColorizerBlock> COLORIZER = register("colorizer", props -> new ColorizerFullCubeBlock(colorizer(props).lightLevel(BlockState::getLightEmission)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_CHAIR = register("colorizer_chair", props -> new ColorizerChairBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_TABLE = register("colorizer_table", props -> new ColorizerTableBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_COUNTER = register("colorizer_counter", props -> new ColorizerCounterBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_STOOL = register("colorizer_stool", props -> new ColorizerStoolBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerFenceBlock> COLORIZER_FENCE = register("colorizer_fence", props -> new ColorizerFenceBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerFenceGateBlock> COLORIZER_FENCE_GATE = register("colorizer_fence_gate", props -> new ColorizerFenceGateBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerWallBlock> COLORIZER_WALL = register("colorizer_wall", props -> new ColorizerWallBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerTrapDoorBlock> COLORIZER_TRAP_DOOR = register("colorizer_trap_door", props -> new ColorizerTrapDoorBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerDoorBlock> COLORIZER_DOOR = register("colorizer_door", props -> new ColorizerDoorBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerSlabBlock> COLORIZER_SLAB = register("colorizer_slab", props -> new ColorizerSlabBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerVerticalSlabBlock> COLORIZER_VERTICAL_SLAB = register("colorizer_vertical_slab", props -> new ColorizerVerticalSlabBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerStairsBlock> COLORIZER_STAIRS = register("colorizer_stairs", props -> new ColorizerStairsBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_LAMP_POST = register("colorizer_lamp_post", props -> new ColorizerLampPost(colorizer(props)));

    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPE = register("colorizer_slope", props -> new ColorizerSlopeBlock(SlopeType.SLOPE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_ANGLE = register("colorizer_sloped_angle", props -> new ColorizerSlopeBlock(SlopeType.SLOPED_ANGLE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_INTERSECTION = register("colorizer_sloped_intersection", props -> new ColorizerSlopeBlock(SlopeType.SLOPED_INTERSECTION, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_OBLIQUE_SLOPE = register("colorizer_oblique_slope", props -> new ColorizerSlopeBlock(SlopeType.OBLIQUE_SLOPE, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_CORNER = register("colorizer_corner", props -> new ColorizerSlopeBlock(SlopeType.CORNER, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLANTED_CORNER = register("colorizer_slanted_corner", props -> new ColorizerSlopeBlock(SlopeType.SLANTED_CORNER, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_PYRAMID = register("colorizer_pyramid", props -> new ColorizerSlopeSideBlock(SlopeType.PYRAMID, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FULL_PYRAMID = register("colorizer_full_pyramid", props -> new ColorizerSlopeSideBlock(SlopeType.FULL_PYRAMID, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_SLOPED_POST = register("colorizer_sloped_post", props -> new ColorizerSlopeSideBlock(SlopeType.SLOPED_POST, colorizer(props)));

    public static final IRegistryObject<ColorizerPanelBlock> COLORIZER_PANEL = register("colorizer_panel", props -> new ColorizerPanelBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBeamBlock> COLORIZER_BEAM = register("colorizer_beam", props -> new ColorizerBeamBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerColumnBlock> COLORIZER_COLUMN = register("colorizer_column", props -> new ColorizerColumnBlock(colorizer(props)));

    public static final IRegistryObject<ColorizerBlock> COLORIZER_CHIMNEY = register("colorizer_chimney", props -> new ColorizerChimneyBlock(colorizer(props).lightLevel(BlockState::getLightEmission)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPLACE = register("colorizer_fireplace", props -> new ColorizerFireplaceBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIRERING = register("colorizer_firering", props -> new ColorizerFireringBlock(colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPIT = register("colorizer_firepit", props -> new ColorizerFirepitBlock(false, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_FIREPIT_COVERED = register("colorizer_firepit_covered", props -> new ColorizerFirepitBlock(true, colorizer(props)));
    public static final IRegistryObject<ColorizerBlock> COLORIZER_STOVE = register("colorizer_stove", props -> new ColorizerStoveBlock(colorizer(props)));

    /**
     * Every colorizer shares the same base properties, they only differ in the shape they take
     */
    private static BlockBehaviour.Properties colorizer(BlockBehaviour.Properties props) {
        return props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).strength(1.5f, 12.0f).sound(SoundType.STONE).dynamicShape().noOcclusion();
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
        final ResourceKey<Item> key = itemKey(name);
        return () -> new BlockItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    public static List<IRegistryObject<? extends Block>> colorizerBlocks() {
        return Arrays.asList(COLORIZER, COLORIZER_CHAIR, COLORIZER_TABLE, COLORIZER_COUNTER, COLORIZER_STOOL, COLORIZER_FENCE, COLORIZER_FENCE_GATE, COLORIZER_WALL, COLORIZER_TRAP_DOOR, COLORIZER_DOOR, COLORIZER_SLAB, COLORIZER_VERTICAL_SLAB, COLORIZER_STAIRS, COLORIZER_LAMP_POST, COLORIZER_SLOPE, COLORIZER_SLOPED_ANGLE, COLORIZER_SLOPED_INTERSECTION, COLORIZER_SLOPED_POST,
                COLORIZER_OBLIQUE_SLOPE, COLORIZER_CORNER, COLORIZER_SLANTED_CORNER, COLORIZER_PYRAMID, COLORIZER_FULL_PYRAMID, COLORIZER_FIREPLACE, COLORIZER_CHIMNEY, COLORIZER_FIRERING, COLORIZER_FIREPIT, COLORIZER_FIREPIT_COVERED, COLORIZER_STOVE,
                COLORIZER_PANEL, COLORIZER_BEAM, COLORIZER_COLUMN
        );
    }

    public static void init() {
    }
}
