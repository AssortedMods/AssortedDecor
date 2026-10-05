package com.grim3212.assorted.buildingblocks.api;

import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class BuildingBlocksTags {

    public static class Items {
        public static final TagKey<Item> INGOTS_ALUMINUM = commonTag("ingots/aluminum");
        public static final TagKey<Item> INGOTS_STEEL = commonTag("ingots/steel");
        public static final TagKey<Item> NUGGETS_ALUMINUM = commonTag("nuggets/aluminum");
        public static final TagKey<Item> NUGGETS_STEEL = commonTag("nuggets/steel");

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
