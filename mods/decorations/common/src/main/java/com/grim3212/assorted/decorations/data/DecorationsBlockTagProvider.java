package com.grim3212.assorted.decorations.data;

import com.grim3212.assorted.decorations.api.DecorationsTags;
import com.grim3212.assorted.decorations.common.blocks.DecorationsBlocks;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DecorationsBlockTagProvider extends LibBlockTagProvider {

    public DecorationsBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // Named by id, as Assorted Colorizer may not be installed.
        appender.apply(DecorationsTags.Blocks.PLANTER_POT_STOOLS).addOptional(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("assortedcolorizer", "colorizer_stool")));
        // The fountain needs the right tool to drop, so it has to say which tool that is.
        appender.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BuiltInRegistries.BLOCK.getResourceKey(DecorationsBlocks.FOUNTAIN.get()).orElseThrow());
    }
}
