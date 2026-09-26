package com.grim3212.assorted.roads.data;

import com.grim3212.assorted.roads.api.RoadsTags;
import com.grim3212.assorted.roads.common.items.RoadsItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class RoadsItemTagProvider extends LibItemTagProvider {

    // Named by id, so the tag stays empty and harmless without Assorted Paint installed.
    private static final ResourceKey<Item> WHITE_PAINT_ROLLER = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("assortedpaint", "paint_roller_white"));

    public RoadsItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See RoadsBlockTagProvider: TagAppender only accepts ResourceKeys now.
        Function<TagKey<Item>, ItemTagger> tagger = (tag) -> new ItemTagger(appender.apply(tag));

        tagger.apply(RoadsTags.Items.TAR).add(RoadsItems.TARBALL.get());
        appender.apply(RoadsTags.Items.ROAD_LINE_PAINTERS).addOptional(WHITE_PAINT_ROLLER);

        copier.accept(RoadsTags.Blocks.ROADWAYS, RoadsTags.Items.ROADWAYS);
        copier.accept(RoadsTags.Blocks.ROADWAYS_ALL, RoadsTags.Items.ROADWAYS_ALL);
        copier.accept(RoadsTags.Blocks.ROADWAYS_COLOR, RoadsTags.Items.ROADWAYS_COLOR);
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
