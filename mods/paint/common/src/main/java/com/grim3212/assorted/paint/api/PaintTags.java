package com.grim3212.assorted.paint.api;

import com.grim3212.assorted.paint.Constants;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class PaintTags {

    public static class Items {
        public static final TagKey<Item> PAINT_ROLLERS = paintTag("paint_rollers");
        public static final TagKey<Item> TAR = commonTag("tar");
        /** Anything sticky enough to hold siding together, tar from Assorted Roads among them. */
        public static final TagKey<Item> SIDING_BINDERS = paintTag("siding_binders");
        /** Assorted Lights' fluro blocks, named here so recoloring them needs no code from that mod. */
        public static final TagKey<Item> FLURO = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("assortedlights", "fluro"));

        private static TagKey<Item> paintTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
