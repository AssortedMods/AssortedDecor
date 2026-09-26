package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.api.DecorTags;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.FluroBlock;
import com.grim3212.assorted.decor.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.decor.common.blocks.building.CutShapes;
import com.grim3212.assorted.decor.common.blocks.building.GemBrickSet;
import com.grim3212.assorted.lib.data.LibBlockTagProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class DecorBlockTagProvider extends LibBlockTagProvider {

    public DecorBlockTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookup) {
        super(packOutput, lookup);
    }

    @Override
    public void addCommonTags(Function<TagKey<Block>, TagAppender<Block>> appender) {
        // The intrinsic tag appender is gone; TagAppender only accepts ResourceKeys. Wrap it back
        // into something that takes blocks so the tag lists below stay readable.
        Function<TagKey<Block>, BlockTagger> tagger = (tag) -> new BlockTagger(appender.apply(tag));

        tagger.apply(DecorTags.Blocks.BRUSH_DISALLOWED_BLOCKS).add(Blocks.SPAWNER, Blocks.BEDROCK);

        tagger.apply(BlockTags.FENCES).add(DecorBlocks.COLORIZER_FENCE.get());
        tagger.apply(BlockTags.FENCE_GATES).add(DecorBlocks.COLORIZER_FENCE_GATE.get());
        tagger.apply(BlockTags.WALLS).add(DecorBlocks.COLORIZER_WALL.get());
        tagger.apply(BlockTags.TRAPDOORS).add(DecorBlocks.COLORIZER_TRAP_DOOR.get());
        tagger.apply(BlockTags.DOORS).add(DecorBlocks.COLORIZER_DOOR.get());
        tagger.apply(BlockTags.STAIRS).add(DecorBlocks.COLORIZER_STAIRS.get());
        tagger.apply(BlockTags.SLABS).add(DecorBlocks.COLORIZER_SLAB.get());

        tagger.apply(BlockTags.STANDING_SIGNS).add(DecorBlocks.NEON_SIGN.get());
        tagger.apply(BlockTags.WALL_SIGNS).add(DecorBlocks.NEON_SIGN_WALL.get());

        tagger.apply(BlockTags.DOORS).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.GLASS_DOOR.get(), DecorBlocks.CHAIN_LINK_DOOR.get(), DecorBlocks.STEEL_DOOR.get());

        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.QUARTZ_DOOR.get(), DecorBlocks.IRON_LANTERN.get(), DecorBlocks.ILLUMINATION_PLATE.get(), DecorBlocks.SIDEWALK.get(), DecorBlocks.ROADWAY.get(), DecorBlocks.ROADWAY_LIGHT.get(), DecorBlocks.ROADWAY_MANHOLE.get(), DecorBlocks.SIDING_HORIZONTAL.get(), DecorBlocks.SIDING_VERTICAL.get(), DecorBlocks.STEEL_DOOR.get(), DecorBlocks.STONE_PATH.get(), DecorBlocks.DECORATIVE_STONE.get());
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.CASTLE_GATE.get(), DecorBlocks.GARAGE_DOOR.get());
        tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(DecorBlocks.colorizerBlocks().stream().map(IRegistryObject::get).toArray(Block[]::new));

        // Only the museum case is wooden; the other six are glass boxes and want no tool at all.
        tagger.apply(BlockTags.MINEABLE_WITH_AXE).add(DecorBlocks.MUSEUM_DISPLAY_CASE.get(), DecorBlocks.LUMBER_MILL.get());

        tagger.apply(DecorTags.Blocks.ROADWAYS).add(DecorBlocks.ROADWAY.get());
        DecorBlocks.ROADWAY_COLORS.forEach((color, roadway) -> {
            tagger.apply(DecorTags.Blocks.ROADWAYS_COLOR).add(roadway.get());
            tagger.apply(BlockTags.MINEABLE_WITH_PICKAXE).add(roadway.get());
        });

        tagger.apply(DecorTags.Blocks.ROADWAYS).addTag(DecorTags.Blocks.ROADWAYS_COLOR);
        tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).addTag(DecorTags.Blocks.ROADWAYS);
        tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_LIGHT.get());
        tagger.apply(DecorTags.Blocks.ROADWAYS_ALL).add(DecorBlocks.ROADWAY_MANHOLE.get());

        FluroBlock.FLURO_BY_DYE.entrySet().stream().forEach((x) -> tagger.apply(DecorTags.Blocks.FLURO).add(x.getValue().get()));

        tagger.apply(DecorTags.Blocks.COLORIZER_ALWAYS_CUTOUT).add(DecorBlocks.COLORIZER_CHIMNEY.get(), DecorBlocks.COLORIZER_FIREPIT_COVERED.get(), DecorBlocks.COLORIZER_STOVE.get());

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

        BlockTagger addTag(TagKey<Block> tag) {
            this.appender.addTag(tag);
            return this;
        }
    }
}
