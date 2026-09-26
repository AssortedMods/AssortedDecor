package com.grim3212.assorted.decor.data;

import com.grim3212.assorted.decor.Constants;
import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.decor.common.blocks.building.CutShapes;
import com.grim3212.assorted.decor.common.blocks.building.StoneFamily;
import com.grim3212.assorted.decor.common.blocks.building.WeatheredSet;
import com.grim3212.assorted.decor.common.blocks.building.WoodSet;
import com.grim3212.assorted.decor.common.crafting.DecorConditions;
import com.grim3212.assorted.decor.common.crafting.LumberMillRecipe;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SlabBlock;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The building block recipes, behind the {@code building_blocks} part. Every shape comes off the
 * stonecutter, or the lumber mill for wood; only blocks made of two things are crafted.
 */
final class BuildingBlockRecipes {

    private static final RecipeCategory CATEGORY = RecipeCategory.BUILDING_BLOCKS;

    private final DecorRecipes recipes;
    private final HolderGetter<Item> items;
    private final RecipeOutput output;

    BuildingBlockRecipes(DecorRecipes recipes, HolderGetter<Item> items, RecipeOutput output) {
        this.recipes = recipes;
        this.items = items;
        this.output = output;
    }

    void build() {
        this.save(ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, DecorBlocks.LUMBER_MILL.get()).define('I', LibCommonTags.Items.INGOTS_IRON).define('#', ItemTags.PLANKS)
                .pattern(" I ").pattern("###").unlockedBy("has_planks", has(ItemTags.PLANKS)), name(DecorBlocks.LUMBER_MILL.get()));

        this.stonecutAll(Blocks.GLOWSTONE, BuildingBlocks.GLOWSTONE_BRICKS);
        this.stonecutAll(Blocks.OBSIDIAN, BuildingBlocks.POLISHED_OBSIDIAN);
        this.stonecutAll(Blocks.LAPIS_BLOCK, BuildingBlocks.LAPIS.bricks(), BuildingBlocks.LAPIS.tiles(), BuildingBlocks.LAPIS.polished());
        this.stonecutAll(Blocks.REDSTONE_BLOCK, BuildingBlocks.REDSTONE.bricks(), BuildingBlocks.REDSTONE.tiles(), BuildingBlocks.REDSTONE.polished());
        this.stonecutAll(Blocks.IRON_BLOCK, BuildingBlocks.IRON_BRICKS);
        this.stonecutAll(Blocks.GOLD_BLOCK, BuildingBlocks.GOLD_BRICKS);
        this.stonecutAll(Blocks.DIAMOND_BLOCK, BuildingBlocks.DIAMOND_BRICKS);
        this.stonecutAll(Blocks.BRICKS, BuildingBlocks.BASKETWEAVE_BRICKS, BuildingBlocks.HERRINGBONE_BRICKS);
        this.stonecut(BuildingBlocks.IRON_BEAM.get(), Items.IRON_INGOT, 1);

        this.save(ShapedRecipeBuilder.shaped(this.items, CATEGORY, BuildingBlocks.REINFORCED_COBBLESTONE.get()).define('I', LibCommonTags.Items.INGOTS_IRON).define('C', Blocks.COBBLESTONE)
                .pattern(" I ").pattern("ICI").pattern(" I ").unlockedBy("has_iron", has(LibCommonTags.Items.INGOTS_IRON)), name(BuildingBlocks.REINFORCED_COBBLESTONE.get()));
        this.save(ShapedRecipeBuilder.shaped(this.items, CATEGORY, BuildingBlocks.FRAMED_COBBLESTONE.get()).define('S', LibCommonTags.Items.RODS_WOODEN).define('C', Blocks.COBBLESTONE)
                .pattern(" S ").pattern("SCS").pattern(" S ").unlockedBy("has_cobblestone", has(Blocks.COBBLESTONE)), name(BuildingBlocks.FRAMED_COBBLESTONE.get()));
        Block checkered = BuildingBlocks.CHECKERED_STONE.get();
        this.save(ShapedRecipeBuilder.shaped(this.items, CATEGORY, checkered, 4).define('L', Blocks.CALCITE).define('D', Blocks.COBBLED_DEEPSLATE)
                .pattern("LD").pattern("DL").unlockedBy("has_calcite", has(Blocks.CALCITE)), name(checkered));

        BuildingBlocks.stones().forEach(this::stone);
        BuildingBlocks.woods().forEach(this::wood);
        BuildingBlocks.woods().forEach(this::vanillaWood);

        this.save(ShapelessRecipeBuilder.shapeless(this.items, CATEGORY, BuildingBlocks.MEAT_BLOCK.get()).requires(Items.PORKCHOP, 9).unlockedBy("has_porkchop", has(Items.PORKCHOP)), name(BuildingBlocks.MEAT_BLOCK.get()));
        this.save(ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.FOOD, Items.PORKCHOP, 9).requires(BuildingBlocks.MEAT_BLOCK.get()).unlockedBy("has_meat_block", has(BuildingBlocks.MEAT_BLOCK.get())), "porkchop_from_meat_block");

        // A block's slab, stairs and wall also come off the cutter from the block itself.
        for (Map.Entry<IRegistryObject<Block>, CutShapes> entry : BuildingBlocks.cuts().entrySet()) {
            Block base = entry.getKey().get();
            List<Block> shapes = BuildingBlocks.withCuts(entry.getKey());
            for (Block cut : shapes.subList(1, shapes.size())) {
                if (isWood(base)) {
                    this.mill(cut, base, count(cut));
                } else {
                    this.stonecut(cut, base, count(cut));
                }
            }
        }
    }

    /** Everything a stone makes comes off the stonecutter from the raw stone, and from its polished form. */
    private void stone(StoneFamily family) {
        WeatheredSet bricks = family.bricks();
        List<Supplier<? extends Block>> shapes = List.of(family.polished(), bricks.plain(), family.tiles(), family.carved(), family.fluted(), family.column());
        this.stonecutAll(family.raw().get(), shapes);
        this.stonecutAll(family.polished().get(), shapes.subList(1, shapes.size()));
        // Moss and cracks come about as on vanilla's stone bricks, not off a cutter.
        if (ours(bricks.mossy().get())) {
            this.mossy(bricks.mossy().get(), bricks.plain().get());
        }
        if (ours(bricks.cracked().get())) {
            this.save(SimpleCookingRecipeBuilder.smelting(Ingredient.of(bricks.plain().get()), CATEGORY, CookingBookCategory.BLOCKS, bricks.cracked().get(), 0.1F, 200)
                    .unlockedBy(hasName(bricks.plain().get()), has(bricks.plain().get())), name(bricks.cracked().get()));
        }
    }

    private void wood(WoodSet wood) {
        // Panels and beams come as many to a block as the colorizer's do off the stonecutter.
        for (Block result : BuildingBlocks.withCuts(wood.parquet(), wood.framed(), wood.panel(), wood.beam())) {
            int count = result == wood.panel().get() ? 8 : result == wood.beam().get() ? 4 : count(result);
            this.millPlanksAndLogs(result, count, wood);
        }
    }

    /**
     * Vanilla's own wood off the lumber mill, never for less than crafting it costs: from a plank
     * where one is enough, otherwise from a log, worth four planks (a bamboo block two).
     */
    private void vanillaWood(WoodSet wood) {
        String name = wood.name();
        boolean bamboo = wood == BuildingBlocks.BAMBOO;
        boolean nether = !wood.flammable();
        String log = logName(wood);
        Item planks = wood.planks().get().asItem();
        TagKey<Item> logs = logs(wood);
        int logQuarters = 4 * planksPerLog(wood);

        this.mill(planks, logs, planksPerLog(wood));
        this.mill(vanilla("stripped_" + log), vanilla(log), 1);
        this.mill(vanilla(name + "_shelf"), vanilla("stripped_" + log), 1);
        if (!bamboo) {
            String bark = name + (nether ? "_hyphae" : "_wood");
            this.mill(vanilla("stripped_" + bark), vanilla(bark), 1);
        }

        // Costs in quarter planks for so many made, sticks at two to a quarter, as vanilla crafts them.
        this.millAtCost(vanilla(name + "_slab"), 2, 1, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_stairs"), 4, 1, planks, logs, logQuarters);
        this.millAtCost(Items.STICK, 2, 1, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_button"), 4, 1, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_pressure_plate"), 8, 1, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_fence"), 18, 3, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_fence_gate"), 12, 1, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_door"), 24, 3, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_trapdoor"), 24, 2, planks, logs, logQuarters);
        this.millAtCost(vanilla(name + "_sign"), 25, 3, planks, logs, logQuarters);
        if (bamboo) {
            Item mosaic = vanilla("bamboo_mosaic");
            this.millPlanksAndLogs(mosaic, 1, wood);
            for (Item cut : List.of(vanilla("bamboo_mosaic_slab"), vanilla("bamboo_mosaic_stairs"))) {
                this.millPlanksAndLogs(cut, count(cut), wood);
                this.mill(cut, mosaic, count(cut));
            }
        } else if (!nether) {
            // A boat takes five planks, one more than a log; the raft is left out, as a bamboo block is only two.
            this.mill(vanilla(name + "_boat"), logs, 1);
        }
    }

    /** {@code result} from a plank if one covers its cost, and from a log if that does. */
    private void millAtCost(Item result, int quarters, int made, Item planks, TagKey<Item> logs, int logQuarters) {
        int fromPlanks = 4 * made / quarters;
        int fromLog = logQuarters * made / quarters;
        if (fromPlanks > 0) {
            this.mill(result, planks, fromPlanks);
        }
        if (fromLog > 0) {
            this.mill(result, logs, fromLog);
        }
    }

    /** So many of {@code result} to a plank, and a log's worth of planks times that to a log. */
    private void millPlanksAndLogs(ItemLike result, int perPlank, WoodSet wood) {
        this.mill(result, wood.planks().get(), perPlank);
        this.mill(result, logs(wood), perPlank * planksPerLog(wood));
    }

    private static String logName(WoodSet wood) {
        return wood == BuildingBlocks.BAMBOO ? "bamboo_block" : wood.name() + (wood.flammable() ? "_log" : "_stem");
    }

    /** Every log, wood and stripped form of it, as vanilla's planks recipe takes. */
    private static TagKey<Item> logs(WoodSet wood) {
        return TagKey.create(Registries.ITEM, Identifier.withDefaultNamespace(wood == BuildingBlocks.BAMBOO ? "bamboo_blocks" : logName(wood) + "s"));
    }

    private static int planksPerLog(WoodSet wood) {
        return wood == BuildingBlocks.BAMBOO ? 2 : 4;
    }

    private static Item vanilla(String path) {
        return BuiltInRegistries.ITEM.getOptional(Identifier.withDefaultNamespace(path)).orElseThrow(() -> new IllegalStateException("no minecraft:" + path));
    }

    private void mossy(Block result, Block base) {
        this.save(ShapelessRecipeBuilder.shapeless(this.items, CATEGORY, result).requires(base).requires(Items.VINE).unlockedBy("has_vine", has(Items.VINE)), name(result) + "_from_vine");
        this.save(ShapelessRecipeBuilder.shapeless(this.items, CATEGORY, result).requires(base).requires(Items.MOSS_BLOCK).unlockedBy("has_moss_block", has(Items.MOSS_BLOCK)), name(result) + "_from_moss_block");
    }

    @SafeVarargs
    private void stonecutAll(Block base, Supplier<? extends Block>... results) {
        this.stonecutAll(base, List.of(results));
    }

    /** Every one of ours among {@code results}, and every shape cut from them, straight from {@code base}. */
    private void stonecutAll(Block base, List<Supplier<? extends Block>> results) {
        for (Supplier<? extends Block> supplier : results) {
            for (Block result : BuildingBlocks.withCuts(supplier)) {
                this.stonecut(result, base, count(result));
            }
        }
    }

    private void stonecut(Block result, ItemLike base, int count) {
        this.save(SingleItemRecipeBuilder.stonecutting(Ingredient.of(base), CATEGORY, result, count).unlockedBy(hasName(base), has(base)),
                name(result) + "_from_" + name(base) + "_stonecutting");
    }

    private void mill(ItemLike result, ItemLike base, int count) {
        this.save(new SingleItemRecipeBuilder(CATEGORY, LumberMillRecipe::new, Ingredient.of(base), result, count).unlockedBy(hasName(base), has(base)),
                name(result) + "_from_" + name(base) + "_lumber_mill");
    }

    private void mill(ItemLike result, TagKey<Item> base, int count) {
        this.save(new SingleItemRecipeBuilder(CATEGORY, LumberMillRecipe::new, Ingredient.of(this.items.getOrThrow(base)), result, count)
                .unlockedBy("has_" + base.location().getPath(), has(base)), name(result) + "_from_" + base.location().getPath() + "_lumber_mill");
    }

    /** Two slabs to a block, as vanilla's stonecutter gives. */
    private static int count(ItemLike result) {
        return result.asItem() instanceof BlockItem block && block.getBlock() instanceof SlabBlock ? 2 : 1;
    }

    private void save(RecipeBuilder builder, String path) {
        Identifier id = Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
        this.recipes.addConditions(this.recipes.partEnabled(DecorConditions.Parts.BUILDING_BLOCKS), id);
        builder.save(this.output, ResourceKey.create(Registries.RECIPE, id));
    }

    private static boolean ours(Block block) {
        return Constants.MOD_ID.equals(BuiltInRegistries.BLOCK.getKey(block).getNamespace());
    }

    private static boolean isWood(Block block) {
        return BuildingBlocks.woods().stream().anyMatch(wood -> BuildingBlocks.blocksOf(wood).contains(block));
    }

    private static Criterion<InventoryChangeTrigger.TriggerInstance> has(ItemLike item) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(item);
    }

    private Criterion<InventoryChangeTrigger.TriggerInstance> has(TagKey<Item> tag) {
        return InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(this.items, tag));
    }

    private static String hasName(ItemLike item) {
        return "has_" + name(item);
    }

    private static String name(ItemLike item) {
        return BuiltInRegistries.ITEM.getKey(item.asItem()).getPath();
    }
}
