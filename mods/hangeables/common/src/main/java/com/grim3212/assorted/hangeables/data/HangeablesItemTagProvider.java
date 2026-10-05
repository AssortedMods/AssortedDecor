package com.grim3212.assorted.hangeables.data;

import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class HangeablesItemTagProvider extends LibItemTagProvider {

    public HangeablesItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // See HangeablesBlockTagProvider: TagAppender only accepts ResourceKeys now.
        appender.apply(ItemTags.SIGNS).add(BuiltInRegistries.ITEM.getResourceKey(HangeablesItems.NEON_SIGN.get()).orElseThrow());
    }
}
