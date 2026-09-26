package com.grim3212.assorted.gates.common.items;

import com.grim3212.assorted.gates.common.sounds.GatesSounds;
import com.grim3212.assorted.gates.common.blocks.GatesBlocks;
import com.grim3212.assorted.gates.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class GatesItems {

    public static final IRegistryObject<Item> GATE_GRATING = register("gate_grating", props -> new Item(props));
    public static final IRegistryObject<Item> GARAGE_PANEL = register("garage_panel", props -> new Item(props));
    public static final IRegistryObject<GateActivatorItem> GATE_TRUMPET = register("gate_trumpet", props -> new GateActivatorItem(GatesBlocks.CASTLE_GATE::get, GatesSounds.GATE_TRUMPET::get, 60, props.stacksTo(1)));
    public static final IRegistryObject<GateActivatorItem> GARAGE_REMOTE = register("garage_remote", props -> new GateActivatorItem(GatesBlocks.GARAGE_DOOR::get, GatesSounds.GARAGE_REMOTE::get, 18, props.stacksTo(1)));

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        // Since 1.21.2 every item has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known. Without this the game
        // dies at registration with "Item id not set", which compiles perfectly happily.
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return GatesBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
