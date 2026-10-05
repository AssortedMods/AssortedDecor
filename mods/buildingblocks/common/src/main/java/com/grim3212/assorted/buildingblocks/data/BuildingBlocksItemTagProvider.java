package com.grim3212.assorted.buildingblocks.data;

import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockItemTags;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BuildingBlocksItemTagProvider extends LibItemTagProvider {


    public BuildingBlocksItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See BuildingBlocksBlockTagProvider: TagAppender only accepts ResourceKeys now.
        Function<TagKey<Item>, ItemTagger> tagger = (tag) -> new ItemTagger(appender.apply(tag));

        BuildingBlocks.cuts().values().forEach(cuts -> {
            tagger.apply(BlockItemTags.SLABS.item()).add(cuts.slab().get().asItem());
            tagger.apply(BlockItemTags.STAIRS.item()).add(cuts.stairs().get().asItem());
            if (cuts.wall() != null) {
                tagger.apply(BlockItemTags.WALLS.item()).add(cuts.wall().get().asItem());
            }
        });
        // Vanilla's burn times read these, and leave the nether woods out of the furnace.
        copier.accept(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
        copier.accept(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
        BuildingBlocks.woods().stream().filter(wood -> !wood.flammable())
                .forEach(wood -> BuildingBlocks.blocksOf(wood).forEach(block -> tagger.apply(ItemTags.NON_FLAMMABLE_WOOD).add(block.asItem())));
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
