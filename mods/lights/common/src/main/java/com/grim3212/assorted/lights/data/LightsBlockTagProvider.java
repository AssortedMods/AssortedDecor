package com.grim3212.assorted.lights.data;

import com.grim3212.assorted.lights.api.LightsTags;
import com.grim3212.assorted.lights.common.blocks.FluroBlock;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class LightsBlockTagProvider extends LibBlockTagProvider {

    public LightsBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(LightsBlocks.IRON_LANTERN.get(), LightsBlocks.ILLUMINATION_PLATE.get());

        FluroBlock.FLURO_BY_DYE.values().forEach(fluro -> tagger.apply(LightsTags.Blocks.FLURO).add(fluro.get()));
    }

    private record BlockTagger(TagAppender<Block> appender) {

        BlockTagger add(Block... blocks) {
            for (Block block : blocks) {
                this.appender.add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            }

            return this;
        }
    }
}
