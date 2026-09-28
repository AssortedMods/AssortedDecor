package com.grim3212.assorted.displays.api;

import com.grim3212.assorted.displays.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class DisplaysTags {

    public static class Items {
        // Still the family's id: Assorted Tools puts its pokeball in this tag under that name.
        public static final TagKey<Item> CAGE_SUPPORTED = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.FAMILY_ID, "cage_supported"));
    }
}
