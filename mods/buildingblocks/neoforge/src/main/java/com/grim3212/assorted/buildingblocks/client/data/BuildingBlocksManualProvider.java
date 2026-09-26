package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.buildingblocks.Family;
import com.grim3212.assorted.buildingblocks.common.blocks.BuildingBlocksBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.buildingblocks.common.blocks.building.GemBrickSet;
import com.grim3212.assorted.buildingblocks.common.blocks.building.StoneFamily;
import com.grim3212.assorted.buildingblocks.common.blocks.building.WoodSet;
import com.grim3212.assorted.buildingblocks.common.items.BuildingBlocksItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This part's chapters of the Assorted Decor section, which every part shares; the explicit chapter order keeps the
 * section's order whichever parts are installed. Every block and item has to open a page, or the provider refuses to generate.
 */
public class BuildingBlocksManualProvider extends LibManualProvider {

    public BuildingBlocksManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Family.ID);
    }

    @Override
    protected void addChapters() {
        this.section(Family.MANUAL_ORDER, Family.ICONS.toArray(Identifier[]::new));

        this.addBuildingBlocks();
        this.addDoors();
    }

    private void addBuildingBlocks() {
        ChapterBuilder chapter = this.chapter("building_blocks", 0);
        chapter.recipes("lumber_mill", BuildingBlocksBlocks.LUMBER_MILL.get()).opens(BuildingBlocksBlocks.LUMBER_MILL.get());

        chapter.recipesById("cut_shapes", stonecut(BuildingBlocks.cuts().get(BuildingBlocks.LAPIS.bricks()).slab().get(), BuildingBlocks.LAPIS.bricks().get()),
                stonecut(BuildingBlocks.cuts().get(BuildingBlocks.LAPIS.bricks()).stairs().get(), BuildingBlocks.LAPIS.bricks().get()),
                stonecut(BuildingBlocks.cuts().get(BuildingBlocks.LAPIS.bricks()).wall().get(), BuildingBlocks.LAPIS.bricks().get()),
                mill(BuildingBlocks.cuts().get(BuildingBlocks.OAK.parquet()).stairs().get(), BuildingBlocks.OAK.parquet().get())).every(50);

        chapter.recipesById("glowstone_bricks", stonecut(BuildingBlocks.GLOWSTONE_BRICKS.get(), Blocks.GLOWSTONE))
                .opens(withCuts(BuildingBlocks.GLOWSTONE_BRICKS));

        List<Block> gems = new ArrayList<>(List.of(withCuts(BuildingBlocks.POLISHED_OBSIDIAN)));
        List<Identifier> gemRecipes = new ArrayList<>(List.of(stonecut(BuildingBlocks.POLISHED_OBSIDIAN.get(), Blocks.OBSIDIAN)));
        Map<GemBrickSet, Block> gemBases = Map.of(BuildingBlocks.LAPIS, Blocks.LAPIS_BLOCK, BuildingBlocks.REDSTONE, Blocks.REDSTONE_BLOCK);
        for (GemBrickSet set : List.of(BuildingBlocks.LAPIS, BuildingBlocks.REDSTONE)) {
            gems.addAll(List.of(withCuts(set.bricks(), set.tiles(), set.polished())));
            Block base = gemBases.get(set);
            gemRecipes.addAll(List.of(stonecut(set.bricks().get(), base), stonecut(set.tiles().get(), base), stonecut(set.polished().get(), base)));
        }
        chapter.recipesById("gem_bricks", gemRecipes.toArray(Identifier[]::new)).every(50).opens(gems.toArray(Block[]::new));

        chapter.recipesById("metal_bricks", stonecut(BuildingBlocks.IRON_BRICKS.get(), Blocks.IRON_BLOCK), stonecut(BuildingBlocks.GOLD_BRICKS.get(), Blocks.GOLD_BLOCK),
                        stonecut(BuildingBlocks.DIAMOND_BRICKS.get(), Blocks.DIAMOND_BLOCK))
                .opens(withCuts(BuildingBlocks.IRON_BRICKS, BuildingBlocks.GOLD_BRICKS, BuildingBlocks.DIAMOND_BRICKS));

        chapter.recipesById("bricks", stonecut(BuildingBlocks.BASKETWEAVE_BRICKS.get(), Blocks.BRICKS), stonecut(BuildingBlocks.HERRINGBONE_BRICKS.get(), Blocks.BRICKS))
                .opens(withCuts(BuildingBlocks.BASKETWEAVE_BRICKS, BuildingBlocks.HERRINGBONE_BRICKS));

        chapter.recipes("cobblestone", BuildingBlocks.REINFORCED_COBBLESTONE.get(), BuildingBlocks.FRAMED_COBBLESTONE.get(), BuildingBlocks.CHECKERED_STONE.get())
                .opens(withCuts(BuildingBlocks.REINFORCED_COBBLESTONE, BuildingBlocks.FRAMED_COBBLESTONE, BuildingBlocks.CHECKERED_STONE));

        // Every stone's patterns share one page; the granite recipes stand for them all.
        List<Block> stones = new ArrayList<>();
        List<Block> columns = new ArrayList<>();
        for (StoneFamily family : BuildingBlocks.stones()) {
            columns.add(family.fluted().get());
            columns.add(family.column().get());
            BuildingBlocks.blocksOf(family).stream().filter(block -> !columns.contains(block)).forEach(stones::add);
        }
        StoneFamily granite = BuildingBlocks.GRANITE;
        chapter.recipesById("stone_patterns", stonecut(granite.bricks().plain().get(), Blocks.GRANITE), stonecut(granite.tiles().get(), Blocks.GRANITE),
                        stonecut(granite.carved().get(), Blocks.GRANITE)).every(50)
                .opens(stones.toArray(Block[]::new));

        chapter.recipesById("weathering", this.recipeId("mossy_granite_bricks_from_vine"), this.recipeId("mossy_granite_bricks_from_moss_block"),
                this.recipeId("cracked_granite_bricks"));

        chapter.recipesById("columns", stonecut(BuildingBlocks.STONE.fluted().get(), Blocks.STONE), stonecut(BuildingBlocks.STONE.column().get(), Blocks.STONE),
                        stonecut(BuildingBlocks.DEEPSLATE.column().get(), Blocks.COBBLED_DEEPSLATE)).every(50)
                .opens(columns.toArray(Block[]::new));

        List<Block> timber = new ArrayList<>();
        List<Block> panels = new ArrayList<>();
        List<Block> beams = new ArrayList<>(List.of(BuildingBlocks.IRON_BEAM.get()));
        for (WoodSet wood : BuildingBlocks.woods()) {
            panels.add(wood.panel().get());
            beams.add(wood.beam().get());
            BuildingBlocks.blocksOf(wood).stream().filter(block -> !panels.contains(block) && !beams.contains(block)).forEach(timber::add);
        }
        WoodSet oak = BuildingBlocks.OAK;
        chapter.recipesById("timber", mill(oak.parquet().get(), Blocks.OAK_PLANKS), mill(oak.framed().get(), Blocks.OAK_PLANKS)).every(50).opens(timber.toArray(Block[]::new));
        chapter.recipesById("panels", mill(oak.panel().get(), Blocks.OAK_PLANKS), mill(BuildingBlocks.CHERRY.panel().get(), Blocks.CHERRY_PLANKS)).every(50).opens(panels.toArray(Block[]::new));
        chapter.recipesById("beams", mill(oak.beam().get(), Blocks.OAK_PLANKS), stonecut(BuildingBlocks.IRON_BEAM.get(), Items.IRON_INGOT)).every(50).opens(beams.toArray(Block[]::new));

        chapter.recipes("meat_block", BuildingBlocks.MEAT_BLOCK.get()).opens(BuildingBlocks.MEAT_BLOCK.get());

        chapter.recipesById("decorative_stone", recipeId("decorative_path_stonecutting")).opens(BuildingBlocksBlocks.DECORATIVE_STONE.get());
        // Its coloured recipes need a paint roller, so they come with Assorted Paint.
        chapter.items("siding", BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), BuildingBlocksBlocks.SIDING_VERTICAL.get()).every(60)
                .opens(BuildingBlocksBlocks.SIDING_HORIZONTAL.get(), BuildingBlocksBlocks.SIDING_VERTICAL.get());
    }

    private Identifier mill(Block result, Block base) {
        return this.recipeId(name(result) + "_from_" + name(base) + "_lumber_mill");
    }

    private Identifier stonecut(Block result, ItemLike base) {
        return this.recipeId(name(result) + "_from_" + BuiltInRegistries.ITEM.getKey(base.asItem()).getPath() + "_stonecutting");
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).getPath();
    }

    @SafeVarargs
    private static Block[] withCuts(IRegistryObject<Block>... blocks) {
        return BuildingBlocks.withCuts(blocks).toArray(Block[]::new);
    }

    private void addDoors() {
        Block[] doors = {BuildingBlocksBlocks.GLASS_DOOR.get(), BuildingBlocksBlocks.QUARTZ_DOOR.get(), BuildingBlocksBlocks.STEEL_DOOR.get()};

        ChapterBuilder chapter = this.chapter("doors", 9);
        chapter.recipes("chain_link", BuildingBlocksItems.CHAIN_LINK.get(), BuildingBlocksBlocks.CHAIN_LINK_FENCE.get(), BuildingBlocksBlocks.CHAIN_LINK_DOOR.get()).every(60)
                .opens(BuildingBlocksBlocks.CHAIN_LINK_DOOR.get(), BuildingBlocksBlocks.CHAIN_LINK_FENCE.get())
                .opens(BuildingBlocksItems.CHAIN_LINK.get());
        chapter.recipes("doors", BuildingBlocksBlocks.GLASS_DOOR.get(), BuildingBlocksBlocks.QUARTZ_DOOR.get(), BuildingBlocksBlocks.STEEL_DOOR.get()).every(50).opens(doors);
    }
}
