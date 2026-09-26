package com.grim3212.assorted.decorations.api;

import com.grim3212.assorted.decorations.Constants;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class DecorationsTags {

    public static class Blocks {
        /** What a planter pot sits down into, the colorizer stool from Assorted Colorizer. */
        public static final TagKey<Block> PLANTER_POT_STOOLS = decorationsTag("planter_pot_stools");

        private static TagKey<Block> decorationsTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> INGOTS_ALUMINUM = commonTag("ingots/aluminum");
        public static final TagKey<Item> INGOTS_STEEL = commonTag("ingots/steel");

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
