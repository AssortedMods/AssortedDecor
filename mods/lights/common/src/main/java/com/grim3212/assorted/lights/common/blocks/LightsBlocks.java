package com.grim3212.assorted.lights.common.blocks;

import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import com.grim3212.assorted.lights.Constants;
import com.grim3212.assorted.lights.Family;
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
import net.minecraft.world.level.material.PushReaction;

import java.util.function.Function;
import java.util.function.Supplier;

public class LightsBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Family.ID);
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<IlluminationTubeBlock> ILLUMINATION_TUBE = register("illumination_tube", props -> new IlluminationTubeBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().instabreak().lightLevel(state -> 15).sound(SoundType.GLASS)));
    public static final IRegistryObject<IlluminationPlateBlock> ILLUMINATION_PLATE = register("illumination_plate", props -> new IlluminationPlateBlock(props.pushReaction(PushReaction.DESTROY).isRedstoneConductor((state, getter, pos) -> false).noCollision().strength(0.5F).lightLevel(state -> 15).sound(SoundType.GLASS)));

    public static final IRegistryObject<FluroBlock> FLURO_WHITE = register("fluro_white", props -> new FluroBlock(DyeColor.WHITE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_ORANGE = register("fluro_orange", props -> new FluroBlock(DyeColor.ORANGE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_MAGENTA = register("fluro_magenta", props -> new FluroBlock(DyeColor.MAGENTA, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIGHT_BLUE = register("fluro_light_blue", props -> new FluroBlock(DyeColor.LIGHT_BLUE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_YELLOW = register("fluro_yellow", props -> new FluroBlock(DyeColor.YELLOW, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIME = register("fluro_lime", props -> new FluroBlock(DyeColor.LIME, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_PINK = register("fluro_pink", props -> new FluroBlock(DyeColor.PINK, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_GRAY = register("fluro_gray", props -> new FluroBlock(DyeColor.GRAY, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_LIGHT_GRAY = register("fluro_light_gray", props -> new FluroBlock(DyeColor.LIGHT_GRAY, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_CYAN = register("fluro_cyan", props -> new FluroBlock(DyeColor.CYAN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_PURPLE = register("fluro_purple", props -> new FluroBlock(DyeColor.PURPLE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BLUE = register("fluro_blue", props -> new FluroBlock(DyeColor.BLUE, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BROWN = register("fluro_brown", props -> new FluroBlock(DyeColor.BROWN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_GREEN = register("fluro_green", props -> new FluroBlock(DyeColor.GREEN, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_RED = register("fluro_red", props -> new FluroBlock(DyeColor.RED, fluro(props)));
    public static final IRegistryObject<FluroBlock> FLURO_BLACK = register("fluro_black", props -> new FluroBlock(DyeColor.BLACK, fluro(props)));

    public static final IRegistryObject<LanternBlock> PAPER_LANTERN = register("paper_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.WOOL).noCollision().strength(0.1F)));
    public static final IRegistryObject<LanternBlock> BONE_LANTERN = register("bone_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_RED).sound(SoundType.BONE_BLOCK).noCollision().strength(0.1F)));
    public static final IRegistryObject<LanternBlock> IRON_LANTERN = register("iron_lantern", props -> new LanternBlock(props.mapColor(MapColor.COLOR_GRAY).sound(SoundType.METAL).noCollision().strength(0.5F)));

    private static BlockBehaviour.Properties fluro(BlockBehaviour.Properties props) {
        return props.instrument(NoteBlockInstrument.HAT).sound(SoundType.GLASS).strength(0.2F, 1.0F).lightLevel(state -> 15);
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
