package com.grim3212.assorted.roads.api;

import com.grim3212.assorted.roads.Constants;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class RoadsTags {

    public static class Blocks {
        public static final TagKey<Block> ROADWAYS = roadsTag("roadways");
        public static final TagKey<Block> ROADWAYS_ALL = roadsTag("roadways/all");
        public static final TagKey<Block> ROADWAYS_COLOR = roadsTag("roadways/color");

        private static TagKey<Block> roadsTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> ROADWAYS = roadsTag("roadways");
        public static final TagKey<Item> ROADWAYS_ALL = roadsTag("roadways/all");
        public static final TagKey<Item> ROADWAYS_COLOR = roadsTag("roadways/color");
        // What cycles a white roadway's markings; Assorted Paint's white roller, when it is installed.
        public static final TagKey<Item> ROAD_LINE_PAINTERS = roadsTag("road_line_painters");
        public static final TagKey<Item> INGOTS_STEEL = commonTag("ingots/steel");
        public static final TagKey<Item> TAR = commonTag("tar");

        private static TagKey<Item> roadsTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }

        private static TagKey<Item> commonTag(String name) {
            return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(LibCommonTags.COMMON_NAMESPACE, name));
        }
    }
}
