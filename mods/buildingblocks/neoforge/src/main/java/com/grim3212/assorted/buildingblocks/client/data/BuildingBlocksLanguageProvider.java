package com.grim3212.assorted.buildingblocks.client.data;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.lib.data.LibLanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.DyeColor;

/**
 * Generates the en_us.json of this mod. A block, item or entity whose name is its id in title case needs
 * no line here (see {@link LibLanguageProvider}); the manual's keys are the Assorted Decor section's, which every part shares.
 */
public class BuildingBlocksLanguageProvider extends LibLanguageProvider {

    /** A blank line between paragraphs; the manual splits its text the way the font does. */
    private static final String BREAK = "\n\n";

    public BuildingBlocksLanguageProvider(PackOutput output) {
        super(output, Constants.MOD_ID);
    }

    @Override
    protected void addNames() {
        this.add("itemGroup.assorteddecor", "Assorted Decor");

        // A siding is one block per direction, coloured by its state, and each colour is named by a
        // key of its own rather than a block.
        this.add("block.assortedbuildingblocks.siding_vertical", "Vertical Siding");
        this.add("block.assortedbuildingblocks.siding_horizontal", "Horizontal Siding");
        for (DyeColor color : DyeColor.values()) {
            this.add("block.assortedbuildingblocks.siding_vertical_" + color.getName(), titleCase(color.getName()) + " Vertical Siding");
            this.add("block.assortedbuildingblocks.siding_horizontal_" + color.getName(), titleCase(color.getName()) + " Horizontal Siding");
        }

        this.addManual();
    }

    /** This part's chapters of the Assorted Decor section, and the section's own title, which every part writes the same. */
    private void addManual() {
        this.add("manual.assorteddecor.title", "Assorted Decor");
        this.add("manual.assorteddecor.description",
                "Blocks that take on the look of other blocks, plus furniture, lights, decorations, wall art, roads and more.");

        this.addBuildingBlocksChapter();
        this.addDoorsChapter();
    }

    private void addBuildingBlocksChapter() {
        this.add("manual.assorteddecor.chapter.building_blocks", "Building Blocks");
        this.add("container.assortedbuildingblocks.lumber_mill", "Lumber Mill");

        this.add("manual.assorteddecor.chapter.building_blocks.lumber_mill.title", "Lumber Mill");
        this.add("manual.assorteddecor.chapter.building_blocks.lumber_mill",
                "The stonecutter's twin for wood. Put in planks and pick what to cut them into. It also supports logs to break up into any of the child blocks and items.");

        this.add("manual.assorteddecor.chapter.building_blocks.glowstone_bricks.title", "Glowstone Bricks");
        this.add("manual.assorteddecor.chapter.building_blocks.glowstone_bricks",
                "Glowstone cut into bricks on the stonecutter.");

        this.add("manual.assorteddecor.chapter.building_blocks.cut_shapes.title", "Slabs, Stairs and Walls");
        this.add("manual.assorteddecor.chapter.building_blocks.cut_shapes",
                "Almost every block here comes off a stonecutter or lumber mill. Stone and metal are cut on the stonecutter, wood on the lumber mill. Most come as slabs and stairs too, and the bricks and tiles as walls.");

        this.add("manual.assorteddecor.chapter.building_blocks.gem_bricks.title", "Obsidian, Lapis and Redstone");
        this.add("manual.assorteddecor.chapter.building_blocks.gem_bricks",
                "Obsidian is cut into polished obsidian, and lapis and redstone blocks into bricks, tiles and polished blocks.");

        this.add("manual.assorteddecor.chapter.building_blocks.metal_bricks.title", "Metal Bricks");
        this.add("manual.assorteddecor.chapter.building_blocks.metal_bricks",
                "Iron, gold and diamond blocks are cut into bricks on the stonecutter, one for one.");

        this.add("manual.assorteddecor.chapter.building_blocks.bricks.title", "Brick Patterns");
        this.add("manual.assorteddecor.chapter.building_blocks.bricks",
                "Brick blocks are cut into basketweave and herringbone patterns on the stonecutter.");

        this.add("manual.assorteddecor.chapter.building_blocks.cobblestone.title", "Cobblestone");
        this.add("manual.assorteddecor.chapter.building_blocks.cobblestone",
                "Cobblestone can be reinforced with iron or framed in sticks, and calcite and cobbled deepslate together make a checkerboard.");

        this.add("manual.assorteddecor.chapter.building_blocks.stone_patterns.title", "Stone Patterns");
        this.add("manual.assorteddecor.chapter.building_blocks.stone_patterns",
                "Every stone is cut into bricks, tiles and a carved creeper face. Stone, granite, diorite, andesite, calcite, tuff, dripstone, deepslate, blackstone, sandstone and red sandstone." + BREAK + "The stonecutter makes them from the raw stone or from its polished form.");

        this.add("manual.assorteddecor.chapter.building_blocks.weathering.title", "Mossy and Cracked");
        this.add("manual.assorteddecor.chapter.building_blocks.weathering",
                "Every stone's bricks have a mossy and a cracked form, made the way vanilla stone bricks are. Vines or moss for mossy, a furnace for cracked.");

        this.add("manual.assorteddecor.chapter.building_blocks.columns.title", "Columns");
        this.add("manual.assorteddecor.chapter.building_blocks.columns",
                "Fluted stone and columns come off the stonecutter from any of the stones." + BREAK + "Columns have a base at the bottom, a capital at the top and a plain shaft between. They can also be placed against a wall to lay them on their side.");

        this.add("manual.assorteddecor.chapter.building_blocks.timber.title", "Boards");
        this.add("manual.assorteddecor.chapter.building_blocks.timber",
                "The lumber mill cuts every kind of planks into parquet and framed planks.");

        this.add("manual.assorteddecor.chapter.building_blocks.panels.title", "Panels");
        this.add("manual.assorteddecor.chapter.building_blocks.panels",
                "The lumber mill cuts a plank into eight panels. A panel lines the face you place it against. It can be placed against a wall, a floor or a ceiling.");

        this.add("manual.assorteddecor.chapter.building_blocks.beams.title", "Beams");
        this.add("manual.assorteddecor.chapter.building_blocks.beams",
                "The lumber mill cuts a plank into four beams, and the stonecutter an iron ingot into an iron beam. They run along the top or bottom of the block, the way you are facing. A beam placed against the side of another grows an arm to meet it, so they cross and tee on their own.");

        this.add("manual.assorteddecor.chapter.building_blocks.meat_block.title", "Meat Block");
        this.add("manual.assorteddecor.chapter.building_blocks.meat_block",
                "Nine porkchops pressed around a bone. It comes apart into the nine porkchops again.");

        this.add("manual.assorteddecor.chapter.building_blocks.decorative_stone.title", "Decorative Stone");
        this.add("manual.assorteddecor.chapter.building_blocks.decorative_stone",
                "Decorative stone comes off the stonecutter from plain stone, which makes it cheap enough to use by the wall.");

        this.add("manual.assorteddecor.chapter.building_blocks.siding.title", "Siding");
        this.add("manual.assorteddecor.chapter.building_blocks.siding",
                "Siding boards a wall in horizontal or vertical planking, in any of the sixteen colors. The "
                        + "color comes from the paint roller in the recipe, and the rollers come with Assorted Paint.");
    }

    private void addDoorsChapter() {
        this.add("manual.assorteddecor.chapter.doors", "Doors and Fences");

        this.add("manual.assorteddecor.chapter.doors.chain_link.title", "Chain Link");
        this.add("manual.assorteddecor.chapter.doors.chain_link",
                "Chain link makes a fence you can see through");

        this.add("manual.assorteddecor.chapter.doors.doors.title", "Doors");
        this.add("manual.assorteddecor.chapter.doors.doors",
                "Glass, quartz and steel doors, for the places a plank door looks wrong. All three can take a "
                        + "padlock from Assorted Storage like any other door.");
    }
}
