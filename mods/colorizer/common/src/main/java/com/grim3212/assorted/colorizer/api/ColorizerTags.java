package com.grim3212.assorted.colorizer.api;

import com.grim3212.assorted.colorizer.Constants;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ColorizerTags {

    public static class Blocks {
        public static final TagKey<Block> BRUSH_DISALLOWED_BLOCKS = colorizerTag("brush_disallowed_blocks");
        public static final TagKey<Block> COLORIZER_ALWAYS_CUTOUT = colorizerTag("colorizer_always_cutout");
        /** What a floor stool grows its taller legs for, the planter pot from Assorted Decorations. */
        public static final TagKey<Block> STOOL_POTS = colorizerTag("stool_pots");

        private static TagKey<Block> colorizerTag(String name) {
            return TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, name));
        }
    }
}
