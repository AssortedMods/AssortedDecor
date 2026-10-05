package com.grim3212.assorted.buildingblocks.common.blocks;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.common.items.ColorChangingItem;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.Supplier;

public class BuildingBlocksBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<BuildingBlocksDoorBlock> QUARTZ_DOOR = register("quartz_door", props -> new BuildingBlocksDoorBlock(props.mapColor(MapColor.QUARTZ).requiresCorrectToolForDrops().strength(5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<BuildingBlocksDoorBlock> GLASS_DOOR = register("glass_door", props -> new BuildingBlocksDoorBlock(props.mapColor(Blocks.GLASS.defaultMapColor()).instrument(NoteBlockInstrument.HAT).strength(0.75F, 7.5F).sound(SoundType.GLASS).noOcclusion()));
    public static final IRegistryObject<BuildingBlocksDoorBlock> STEEL_DOOR = register("steel_door", props -> new BuildingBlocksDoorBlock(props.mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.METAL).requiresCorrectToolForDrops().noOcclusion()));
    public static final IRegistryObject<BuildingBlocksDoorBlock> CHAIN_LINK_DOOR = register("chain_link_door", props -> new BuildingBlocksDoorBlock(props.mapColor(MapColor.METAL).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<BuildingBlocksBarsBlock> CHAIN_LINK_FENCE = register("chain_link_fence", props -> new BuildingBlocksBarsBlock(props.mapColor(MapColor.METAL).strength(0.5F, 5.0F).sound(SoundType.METAL).noOcclusion()));
    public static final IRegistryObject<Block> DECORATIVE_STONE = register("decorative_stone", props -> new Block(props.mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).sound(SoundType.STONE).strength(0.5F, 10.0F).requiresCorrectToolForDrops()));

    public static final IRegistryObject<LumberMillBlock> LUMBER_MILL = register("lumber_mill", props -> new LumberMillBlock(props.mapColor(MapColor.WOOD).instrument(NoteBlockInstrument.BASS).strength(2.5F).sound(SoundType.WOOD).ignitedByLava()));

    public static final IRegistryObject<ColorChangingBlock> SIDING_VERTICAL = registerColorChanging("siding_vertical", props -> new ColorChangingBlock(props.mapColor(MapColor.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));
    public static final IRegistryObject<ColorChangingBlock> SIDING_HORIZONTAL = registerColorChanging("siding_horizontal", props -> new ColorChangingBlock(props.mapColor(MapColor.METAL).sound(SoundType.STONE).strength(1.0F, 10.0F).requiresCorrectToolForDrops()));

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

    private static <T extends Block> IRegistryObject<T> registerColorChanging(String name, Function<BlockBehaviour.Properties, ? extends T> factory) {
        return register(name, factory, block -> colorChangingItem(name, block));
    }

    private static Supplier<BlockItem> colorChangingItem(final String name, final IRegistryObject<? extends Block> block) {
        final ResourceKey<Item> key = itemKey(name);
        return () -> new ColorChangingItem(block.get(), new Item.Properties().useBlockDescriptionPrefix().setId(key));
    }

    private static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
    }

    public static void init() {
    }
}
