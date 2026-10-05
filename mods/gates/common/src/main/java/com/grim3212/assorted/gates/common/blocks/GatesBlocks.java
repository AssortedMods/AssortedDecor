package com.grim3212.assorted.gates.common.blocks;

import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.gates.common.items.GatesItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Function;
import java.util.function.Supplier;

public class GatesBlocks {

    public static final RegistryProvider<Block> BLOCKS = RegistryProvider.create(Registries.BLOCK, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);
    // Blocks and their item forms get registered before other items
    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<GateBlock> CASTLE_GATE = register("castle_gate", props -> new GateBlock(props.mapColor(MapColor.METAL).strength(1.0F, 10.0F).sound(SoundType.CHAIN).noOcclusion().isSuffocating(GatesBlocks::never).isViewBlocking(GatesBlocks::never), () -> GatesItems.GATE_TRUMPET.get(), SoundEvents.IRON_DOOR_OPEN, SoundEvents.IRON_DOOR_CLOSE));
    public static final IRegistryObject<GateBlock> GARAGE_DOOR = register("garage_door", props -> new GateBlock(props.mapColor(MapColor.SNOW).strength(1.0F, 10.0F).sound(SoundType.METAL).noOcclusion().isSuffocating(GatesBlocks::never).isViewBlocking(GatesBlocks::never), () -> GatesItems.GARAGE_REMOTE.get(), SoundEvents.PISTON_EXTEND, SoundEvents.PISTON_CONTRACT));

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

    private static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    public static void init() {
    }
}
