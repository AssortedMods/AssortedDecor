package com.grim3212.assorted.hangeables.common.items;

import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.items.FrameItem.FrameMaterial;
import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;

import java.util.function.Function;

public class HangeablesItems {

    public static final IRegistryObject<WallpaperItem> WALLPAPER = register("wallpaper", props -> new WallpaperItem(props));
    public static final IRegistryObject<FrameItem> WOOD_FRAME = register("wood_frame", props -> new FrameItem(FrameMaterial.WOOD, props));
    public static final IRegistryObject<FrameItem> IRON_FRAME = register("iron_frame", props -> new FrameItem(FrameMaterial.IRON, props));

    public static final IRegistryObject<NeonSignItem> NEON_SIGN = register("neon_sign", props -> new NeonSignItem(props.stacksTo(16)));

    private static <T extends Item> IRegistryObject<T> register(final String name, final Function<Item.Properties, ? extends T> factory) {
        // Since 1.21.2 every item has to know its own id before it is constructed, so the
        // properties are built here where the registration name is known. Without this the game
        // dies at registration with "Item id not set", which compiles perfectly happily.
        final ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        return HangeablesBlocks.ITEMS.register(name, () -> factory.apply(new Item.Properties().setId(key)));
    }

    public static void init() {
    }
}
