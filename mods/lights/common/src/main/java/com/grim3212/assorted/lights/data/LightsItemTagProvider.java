package com.grim3212.assorted.lights.data;

import com.grim3212.assorted.lights.api.LightsTags;
import com.grim3212.assorted.lights.common.blocks.LightsBlocks;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class LightsItemTagProvider extends LibItemTagProvider {

    public LightsItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See LightsBlockTagProvider: TagAppender only accepts ResourceKeys now.
        Function<TagKey<Item>, ItemTagger> tagger = (tag) -> new ItemTagger(appender.apply(tag));

        tagger.apply(LightsTags.Items.LANTERN_SOURCE).addOptionalTag(ItemTags.CANDLES);
        tagger.apply(LightsTags.Items.LANTERN_SOURCE).add(LightsBlocks.ILLUMINATION_TUBE.get().asItem(), Blocks.TORCH.asItem(), Blocks.SOUL_TORCH.asItem());

        tagger.apply(LightsTags.Items.ILLUMINATION_PLATES).add(LightsBlocks.ILLUMINATION_PLATE.get().asItem());

        copier.accept(LightsTags.Blocks.FLURO, LightsTags.Items.FLURO);
    }

    private record ItemTagger(TagAppender<Item> appender) {

        ItemTagger add(Item... items) {
            for (Item item : items) {
                this.appender.add(BuiltInRegistries.ITEM.getResourceKey(item).orElseThrow());
            }

            return this;
        }

        ItemTagger addOptionalTag(TagKey<Item> tag) {
            this.appender.addOptionalTag(tag);
            return this;
        }
    }
}
