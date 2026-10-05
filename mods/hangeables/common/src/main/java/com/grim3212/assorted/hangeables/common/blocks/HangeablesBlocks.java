package com.grim3212.assorted.hangeables.common.blocks;

import com.grim3212.assorted.hangeables.Constants;
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
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.Supplier;

public class HangeablesBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<NeonSignBlock> NEON_SIGN = registerNoItem("neon_sign", props -> new NeonSignStandingBlock(props.mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(10f).noCollision()));
    public static final IRegistryObject<NeonSignBlock> NEON_SIGN_WALL = registerNoItem("neon_sign_wall", props -> new NeonSignWallBlock(props.mapColor(MapColor.COLOR_BLACK).instrument(NoteBlockInstrument.BASEDRUM).strength(10f).noCollision().overrideLootTable(NEON_SIGN.get().getLootTable()).overrideDescription(NEON_SIGN.get().getDescriptionId())));

    public static final IRegistryObject<CalendarBlock> CALENDAR = register("calendar", props -> new CalendarBlock(props.mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).noCollision().strength(1f)));
    public static final IRegistryObject<WallClockBlock> WALL_CLOCK = register("wall_clock", props -> new WallClockBlock(props.mapColor(MapColor.WOOD).ignitedByLava().instrument(NoteBlockInstrument.BASS).sound(SoundType.WOOD).strength(1f)));

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

    public static void init() {
    }
}
