package com.grim3212.assorted.buildingblocks.data;

import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.CutShapes;
import com.grim3212.assorted.buildingblocks.common.blocks.building.GemBrickSet;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class BuildingBlocksBlockTagProvider extends LibBlockTagProvider {

    public BuildingBlocksBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(BlockTags.DOORS).add(BuildingBlocksBlocks.QUARTZ_DOOR.get(), BuildingBlocksBlocks.GLASS_DOOR.get(), BuildingBlocksBlocks.CHAIN_LINK_DOOR.get(), BuildingBlocksBlocks.STEEL_DOOR.get());

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(BuildingBlocksBlocks.QUARTZ_DOOR.get(), BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), BuildingBlocksBlocks.SIDING_VERTICAL.get(), BuildingBlocksBlocks.STEEL_DOOR.get(), BuildingBlocksBlocks.DECORATIVE_STONE.get());
        tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(BuildingBlocksBlocks.LUMBER_MILL.get());

        this.addBuildingBlockTags(tagger);
    }

    private void addBuildingBlockTags(Function<TagKey<Block>, BlockTagger> tagger) {
        List<Block> wood = new ArrayList<>();
        BuildingBlocks.woods().forEach(set -> wood.addAll(BuildingBlocks.blocksOf(set)));

        for (IRegistryObject<? extends Block> registered : BuildingBlocks.all()) {
            Block block = registered.get();
            // The meat block breaks by hand as quickly as with anything.
            if (wood.contains(block)) {
                tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(block);
            } else if (block != BuildingBlocks.MEAT_BLOCK.get()) {
                tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
            }
        }

        for (CutShapes cuts : BuildingBlocks.cuts().values()) {
            boolean wooden = wood.contains(cuts.slab().get());
            tagger.apply(wooden ? BlockTags.WOODEN_SLABS : BlockTags.SLABS).add(cuts.slab().get());
            tagger.apply(wooden ? BlockTags.WOODEN_STAIRS : BlockTags.STAIRS).add(cuts.stairs().get());
            if (cuts.wall() != null) {
                tagger.apply(BlockTags.WALLS).add(cuts.wall().get());
            }
        }

        // The same tool tiers as the vanilla blocks each set is cut from.
        tagger.apply(BlockTags.NEEDS_DIAMOND_TOOL).add(BuildingBlocks.withCuts(BuildingBlocks.POLISHED_OBSIDIAN).toArray(Block[]::new));
        tagger.apply(BlockTags.NEEDS_STONE_TOOL).add(gemBlocks(BuildingBlocks.LAPIS));
        tagger.apply(BlockTags.NEEDS_STONE_TOOL).add(BuildingBlocks.withCuts(BuildingBlocks.IRON_BRICKS).toArray(Block[]::new));
        tagger.apply(BlockTags.NEEDS_STONE_TOOL).add(BuildingBlocks.IRON_BEAM.get());
        tagger.apply(BlockTags.NEEDS_IRON_TOOL).add(BuildingBlocks.withCuts(BuildingBlocks.GOLD_BRICKS, BuildingBlocks.DIAMOND_BRICKS).toArray(Block[]::new));
    }

    private static Block[] gemBlocks(GemBrickSet set) {
        return BuildingBlocks.withCuts(set.bricks(), set.tiles(), set.polished()).toArray(Block[]::new);
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
