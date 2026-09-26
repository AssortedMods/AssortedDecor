package com.grim3212.assorted.lights.api;

import com.grim3212.assorted.lights.Constants;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class LightsTags {

    public static class Blocks {
        public static final TagKey<Block> FLURO = lightsTag("fluro");

        private static TagKey<Block> lightsTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> LANTERN_SOURCE = lightsTag("lantern_source");
        public static final TagKey<Item> FLURO = lightsTag("fluro");
        // Assorted Roads builds its roadway light from this, naming it by id so it needs no code of ours.
        public static final TagKey<Item> ILLUMINATION_PLATES = lightsTag("illumination_plates");
        public static final TagKey<Item> INGOTS_ALUMINUM = commonTag("ingots/aluminum");

        private static TagKey<Item> lightsTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
