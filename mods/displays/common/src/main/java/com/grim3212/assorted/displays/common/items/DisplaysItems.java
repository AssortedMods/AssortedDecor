package com.grim3212.assorted.displays.common.items;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class DisplaysItems {

    public static final IRegistryObject<ResizingToolItem> RESIZING_TOOL = register("resizing_tool", props -> new ResizingToolItem(props.stacksTo(1)));

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        // Since 1.21.2 every item has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known. Without this the game
        // dies at registration with "Item id not set", which compiles perfectly happily.
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return DisplaysBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
