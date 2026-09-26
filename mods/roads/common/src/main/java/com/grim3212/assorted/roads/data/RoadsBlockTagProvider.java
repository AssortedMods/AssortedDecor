package com.grim3212.assorted.roads.data;

import com.grim3212.assorted.roads.api.RoadsTags;
import com.grim3212.assorted.roads.common.blocks.RoadsBlocks;
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

public class RoadsBlockTagProvider extends LibBlockTagProvider {

    public RoadsBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(RoadsBlocks.SIDEWALK.get(), RoadsBlocks.ROADWAY.get(), RoadsBlocks.ROADWAY_LIGHT.get(), RoadsBlocks.ROADWAY_MANHOLE.get(), RoadsBlocks.STONE_PATH.get());

        tagger.apply(RoadsTags.Blocks.ROADWAYS).add(RoadsBlocks.ROADWAY.get());
        RoadsBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
            tagger.apply(RoadsTags.Blocks.ROADWAYS_COLOR).add(roadway.get());
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(roadway.get());
        });

        tagger.apply(RoadsTags.Blocks.ROADWAYS).addTag(RoadsTags.Blocks.ROADWAYS_COLOR);
        tagger.apply(RoadsTags.Blocks.ROADWAYS_ALL).addTag(RoadsTags.Blocks.ROADWAYS);
        tagger.apply(RoadsTags.Blocks.ROADWAYS_ALL).add(RoadsBlocks.ROADWAY_LIGHT.get());
        tagger.apply(RoadsTags.Blocks.ROADWAYS_ALL).add(RoadsBlocks.ROADWAY_MANHOLE.get());
    }

    private record BlockTagger(TagAppender<Block> appender) {

        BlockTagger add(Block... blocks) {
            for (Block block : blocks) {
                this.appender.add(BuiltInRegistries.BLOCK.getResourceKey(block).orElseThrow());
            }

            return this;
        }

        BlockTagger addTag(TagKey<Block> tag) {
            this.appender.addTag(tag);
            return this;
        }
    }
}
