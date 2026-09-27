package com.grim3212.assorted.paint.data;

import com.grim3212.assorted.paint.api.PaintTags;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.data.LibItemTagProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class PaintItemTagProvider extends LibItemTagProvider {

    public PaintItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup, CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookup, blockTags);
    }

    @Override
    public void addCommonTags(Function<TagKey<Item>, TagAppender<Item>> appender, BiConsumer<TagKey<Block>, TagKey<Item>> copier) {
        // A roller is a dye of its color, so vanilla's dyeing takes it and other mods can match it by tag.
        PaintItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> {
            ResourceKey<Item> key = BuiltInRegistries.ITEM.getResourceKey(roller.get()).orElseThrow();
            appender.apply(PaintTags.Items.PAINT_ROLLERS).add(key);
            appender.apply(LibCommonTags.Items.DYES).add(key);
            appender.apply(DyeHelper.getDyeTag(color)).add(key);
        });

        appender.apply(PaintTags.Items.SIDING_BINDERS).add(Items.HONEYCOMB.builtInRegistryHolder().key(), Items.HONEY_BOTTLE.builtInRegistryHolder().key(), Items.RESIN_CLUMP.builtInRegistryHolder().key())
                .addOptionalTag(LibCommonTags.Items.SLIMEBALLS).addOptionalTag(PaintTags.Items.TAR);
    }
}
