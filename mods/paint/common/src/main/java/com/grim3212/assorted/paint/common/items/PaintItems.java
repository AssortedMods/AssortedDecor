package com.grim3212.assorted.paint.common.items;

import com.google.common.collect.Maps;
import com.grim3212.assorted.paint.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;

public class PaintItems {

    public static final RegistryProvider<Item> ITEMS = RegistryProvider.create(Registries.ITEM, Constants.MOD_ID).aliasFrom(Constants.FAMILY_ID);

    public static final IRegistryObject<Item> PAINT_ROLLER = register("paint_roller", props -> new Item(props.stacksTo(1)));

    public static final Map<DyeColor, IRegistryObject<PaintRollerItem>> PAINT_ROLLER_COLORS = Maps.newEnumMap(DyeColor.class);

    static {
        Arrays.stream(DyeColor.values()).forEach((color) -> PAINT_ROLLER_COLORS.put(color, register("paint_roller_" + color.getName(), props -> new PaintRollerItem(color, props))));
    }

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        // Since 1.21.2 every item has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known. Without this the game
        // dies at registration with "Item id not set", which compiles perfectly happily.
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
