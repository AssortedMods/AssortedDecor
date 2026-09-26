package com.grim3212.assorted.colorizer.data;

import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ColorizerItemTagProvider extends LibItemTagProvider {

    public ColorizerItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See ColorizerBlockTagProvider: TagAppender only accepts ResourceKeys now.
        Function<TagKey<Item>, ItemTagger> tagger = (tag) -> new ItemTagger(appender.apply(tag));

        tagger.apply(LibCommonTags.Items.FENCES).add(ColorizerBlocks.COLORIZER_FENCE.get().asItem());
        tagger.apply(LibCommonTags.Items.FENCE_GATES).add(ColorizerBlocks.COLORIZER_FENCE_GATE.get().asItem());
        tagger.apply(BlockItemTags.WALLS.item()).add(ColorizerBlocks.COLORIZER_WALL.get().asItem());
        tagger.apply(BlockItemTags.TRAPDOORS.item()).add(ColorizerBlocks.COLORIZER_TRAP_DOOR.get().asItem());
        tagger.apply(BlockItemTags.STAIRS.item()).add(ColorizerBlocks.COLORIZER_STAIRS.get().asItem());
        tagger.apply(BlockItemTags.SLABS.item()).add(ColorizerBlocks.COLORIZER_SLAB.get().asItem());
    }

    private record ItemTagger(TagAppender<Item> appender) {

        ItemTagger add(Item... items) {
            for (Item item : items) {
                this.appender.add(BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow());
            }

            return this;
        }
    }
}
