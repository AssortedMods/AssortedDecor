package com.grim3212.assorted.colorizer.data;

import com.grim3212.assorted.colorizer.api.ColorizerTags;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
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
import net.minecraft.world.level.block.Blocks;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class ColorizerBlockTagProvider extends LibBlockTagProvider {

    public ColorizerBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(ColorizerTags.Blocks.BRUSH_DISALLOWED_BLOCKS).add(Blocks.SPAWNER, Blocks.BEDROCK);

        tagger.apply(BlockTags.FENCES).add(ColorizerBlocks.COLORIZER_FENCE.get());
        tagger.apply(BlockTags.FENCE_GATES).add(ColorizerBlocks.COLORIZER_FENCE_GATE.get());
        tagger.apply(BlockTags.WALLS).add(ColorizerBlocks.COLORIZER_WALL.get());
        tagger.apply(BlockTags.TRAPDOORS).add(ColorizerBlocks.COLORIZER_TRAP_DOOR.get());
        tagger.apply(BlockTags.DOORS).add(ColorizerBlocks.COLORIZER_DOOR.get());
        tagger.apply(BlockTags.STAIRS).add(ColorizerBlocks.COLORIZER_STAIRS.get());
        tagger.apply(BlockTags.SLABS).add(ColorizerBlocks.COLORIZER_SLAB.get());

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(ColorizerBlocks.colorizerBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new));

        tagger.apply(ColorizerTags.Blocks.COLORIZER_ALWAYS_CUTOUT).add(ColorizerBlocks.COLORIZER_CHIMNEY.get(), ColorizerBlocks.COLORIZER_FIREPIT_COVERED.get(), ColorizerBlocks.COLORIZER_STOVE.get());
        // Optional, so the tag still loads without Assorted Decorations installed.
        appender.apply(ColorizerTags.Blocks.STOOL_POTS).addOptional(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("assorteddecorations", "planter_pot")));
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
