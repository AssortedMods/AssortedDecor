package com.grim3212.assorted.paint.data;

import com.grim3212.assorted.paint.Constants;
import com.grim3212.assorted.paint.api.PaintTags;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.paint.common.items.PaintRollerItem;
import com.grim3212.assorted.lib.core.conditions.ConditionalRecipeProvider;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.BlockItemStateProperties;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import java.util.concurrent.CompletableFuture;

/**
 * Every recipe that takes a paint roller. The ones that paint another part's blocks name them by id and load only
 * with that mod, so this mod never needs the others' code.
 */
public class PaintRecipes extends ConditionalRecipeProvider {

    private static final String ROADS = "assortedroads";
    private static final String LIGHTS = "assortedlights";
    private static final String BUILDING_BLOCKS = "assortedbuildingblocks";

    /** The state a siding block keeps its color in, spelled out as Assorted Building Blocks has it. */
    private static final EnumProperty<DyeColor> SIDING_COLOR = EnumProperty.create("color", DyeColor.class);

    private final HolderLookup.RegistryLookup<Item> items;

    public PaintRecipes(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output, Constants.MOD_ID);
        this.items = registries.lookupOrThrow(Registries.ITEM);
    }

    @Override
    public void registerConditions() {
        for (DyeColor color : DyeColor.values()) {
            this.addConditions(modLoaded(ROADS), prefix("roadway_" + color.getName()));
            this.addConditions(modLoaded(LIGHTS), prefix("fluro_" + color.getName() + "_paint_roll"));
            // Siding is made with tar, which Assorted Roads or another mod has to supply.
            this.addConditions(and(modLoaded(BUILDING_BLOCKS), itemTagExists(PaintTags.Items.TAR)), prefix("siding_vertical_" + color.getName()), prefix("siding_horizontal_" + color.getName()));
        }
    }

    @Override
    public void buildRecipes() {
        super.buildRecipes();

        ShapedRecipeBuilder.shaped(this.items, RecipeCategory.DECORATIONS, PaintItems.PAINT_ROLLER.get()).define('S', LibCommonTags.Items.RODS_WOODEN).define('W', ItemTags.WOOL).pattern("WWW").pattern(" S ").pattern(" S ").unlockedBy("has_wool", has(ItemTags.WOOL)).save(this.output);

        // Wool and carpet need no recipe here: the rollers are dyes, so vanilla's dyeing takes them and hands them back worn.
        PaintItems.PAINT_ROLLER_COLORS.forEach((c, r) -> {
            PaintRollerItem roller = r.get();
            String color = c.getName();

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, r.get()).requires(PaintItems.PAINT_ROLLER.get()).requires(difference(this.tag(DyeHelper.getDyeTag(c)), this.tag(PaintTags.Items.PAINT_ROLLERS))).unlockedBy("has_dye", has(DyeHelper.getDyeTag(c))).save(this.output);

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_BY_DYE.get(c)).requires(roller).requires(LibCommonTags.Items.CONCRETE).unlockedBy("has_concrete", has(LibCommonTags.Items.CONCRETE)).save(this.output, key(name(DyeHelper.CONCRETE_BY_DYE.get(c).asItem()) + "_paint_roll"));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, DyeHelper.CONCRETE_POWDER_BY_DYE.get(c)).requires(roller).requires(LibCommonTags.Items.CONCRETE_POWDER).unlockedBy("has_concrete_powder", has(LibCommonTags.Items.CONCRETE_POWDER)).save(this.output, key(name(DyeHelper.CONCRETE_POWDER_BY_DYE.get(c).asItem()) + "_paint_roll"));

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, result(LIGHTS, "fluro_" + color, 1, DataComponentPatch.EMPTY)).requires(roller).requires(PaintTags.Items.FLURO).unlockedBy("has_fluro", has(PaintTags.Items.FLURO)).save(this.output, key("fluro_" + color + "_paint_roll"));

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, result(ROADS, "roadway_" + color, 1, DataComponentPatch.EMPTY)).requires(byId(ROADS, "roadway")).requires(roller).unlockedBy("has_paint", has(roller)).save(this.output, key("roadway_" + color));

            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, result(BUILDING_BLOCKS, "siding_vertical", 4, sidingColor(c))).requires(PaintTags.Items.TAR).requires(LibCommonTags.Items.COBBLESTONE).requires(roller).unlockedBy("has_tar", has(PaintTags.Items.TAR)).save(this.output, key("siding_vertical_" + color));
            ShapelessRecipeBuilder.shapeless(this.items, RecipeCategory.DECORATIONS, result(BUILDING_BLOCKS, "siding_horizontal", 4, sidingColor(c))).requires(PaintTags.Items.TAR).requires(ItemTags.PLANKS).requires(roller).unlockedBy("has_tar", has(PaintTags.Items.TAR)).save(this.output, key("siding_horizontal_" + color));
        });
    }

    /**
     * An item of another part, which is not registered while this mod's data is generated. A stand alone holder
     * still writes its id, and nothing here asks it for the item itself.
     */
    private Holder<Item> holder(String modId, String path) {
        return Holder.Reference.createStandAlone(this.items, ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(modId, path)));
    }

    private ItemStackTemplate result(String modId, String path, int count, DataComponentPatch components) {
        return new ItemStackTemplate(this.holder(modId, path), count, components);
    }

    private Ingredient byId(String modId, String path) {
        return Ingredient.of(HolderSet.direct(this.holder(modId, path)));
    }

    /**
     * A siding's color rides along as its block state. Datagen cannot build ItemStacks, as an item's default
     * components are bound during a resource reload, so the recipe result carries the patch.
     */
    private static DataComponentPatch sidingColor(DyeColor color) {
        return DataComponentPatch.builder().set(DataComponents.BLOCK_STATE, BlockItemStateProperties.EMPTY.with(SIDING_COLOR, color)).build();
    }

    /**
     * Recipes are addressed by {@code ResourceKey<Recipe<?>>} rather than a raw id now.
     */
    private static ResourceKey<Recipe<?>> key(String path) {
        return ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(Constants.MOD_ID, path));
    }

    /**
     * Recipe providers are not data providers any more - a {@link RecipeProvider.Runner} owns the
     * file writing and builds a fresh provider around the {@link RecipeOutput} it hands out.
     */
    public static class Runner extends ConditionalRecipeProvider.Runner {

        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(output, registries, Constants.MOD_ID);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
            return new PaintRecipes(registries, output);
        }

        @Override
        public String getName() {
            return "Recipes: " + Constants.MOD_ID;
        }
    }
}
