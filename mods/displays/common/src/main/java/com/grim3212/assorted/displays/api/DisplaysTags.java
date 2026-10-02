package com.grim3212.assorted.displays.api;

import com.grim3212.assorted.displays.Constants;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DisplaysTags {

    public static class Items {
        // Still the family's id: Assorted Tools puts its pokeball in this tag under that name.
        public static final TagKey<Item> CAGE_SUPPORTED = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "cage_supported"));
    }

    public static class DataComponents {
        /** Item components that hold a mob as saved entity data, like a filled pokeball's. Any item carrying one goes in a cage. */
        public static final TagKey<DataComponentType<?>> CAGE_ENTITY_DATA = TagKey.create(Registries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "cage_entity_data"));
    }
}
