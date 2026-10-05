package com.grim3212.assorted.displays.data;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
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

public class DisplaysBlockTagProvider extends LibBlockTagProvider {

    public DisplaysBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        // Only the museum case is wooden; the other six are glass boxes and want no tool at all.
        tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(DisplaysBlocks.MUSEUM_DISPLAY_CASE.get());
        // The cage needs the right tool to drop, so it has to say which tool that is.
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DisplaysBlocks.CAGE.get());
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
