package com.grim3212.assorted.colorizer.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.helpers.ColorizerCreativeItems;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.colorizer.gametest.ColorizerTestSupport.*;

/**
 * What the mod ships: models and names, colorizer item models, loader keys both loaders read, and recipes that load.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("mod_assets_are_complete", AssetTests::modAssetsAreComplete);
        out.accept("colorizer_items_draw_their_stored_block", AssetTests::colorizerItemsDrawTheirStoredBlock);
        out.accept("loader_models_are_read_on_both_loaders", AssetTests::loaderModelsAreReadOnBothLoaders);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
        out.accept("crafting_recipes_answer_for_no_other", AssetTests::craftingRecipesAnswerForNoOther);
    }

    /**
     * The creative tab is registered, and every block and item has a model and a name. Every gap is
     * reported at once.
     */
    private static void modAssetsAreComplete(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(ColorizerCreativeItems.TAB),
                "the Assorted Decor creative tab is not registered");

        JsonObject lang = readJson("/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        helper.assertTrue(lang != null, "/assets/" + Constants.MOD_ID + "/lang/en_us.json is not on the classpath");
        helper.assertTrue(lang.has("itemGroup." + Constants.FAMILY_ID), "the creative tab has no name in en_us.json");

        List<String> missing = new ArrayList<>();

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json")) {
                missing.add("blockstate for " + id);
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for block " + id);
            }
        }

        for (Map.Entry<ResourceKey<Item>, Item> entry : BuiltInRegistries.ITEM.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            if (!resourceExists("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json")) {
                missing.add("item model for " + id);
            }
            if (!lang.has(entry.getValue().getDescriptionId())) {
                missing.add("name " + entry.getValue().getDescriptionId() + " for item " + id);
            }
        }

        helper.assertTrue(missing.isEmpty(), missing.size() + " missing assets: " + String.join("; ", missing));
        helper.succeed();
    }

    /**
     * Every colorizer item json names the {@code assortedcolorizer:colorizer} item model type. A plain
     * {@code minecraft:model} bakes the empty colorizer once and nothing warns. What the model
     * draws is checked by {@code ColorizerClientGameTests}.
     */
    private static void colorizerItemsDrawTheirStoredBlock(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        int checked = 0;

        for (IRegistryObject<? extends Block> registered : ColorizerBlocks.colorizerBlocks()) {
            Item item = registered.get().asItem();
            if (item == Items.AIR) {
                continue;
            }
            checked++;

            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            JsonObject json = readJson("/assets/" + id.getNamespace() + "/items/" + id.getPath() + ".json");
            JsonObject model = json == null ? null : json.getAsJsonObject("model");
            String type = model != null && model.has("type") ? model.get("type").getAsString() : null;
            if (!(Constants.MOD_ID + ":colorizer").equals(type)) {
                wrong.add(id + " is drawn by " + type);
            }
        }

        helper.assertTrue(checked > 20, "only " + checked + " colorizer items were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " colorizer item(s) cannot draw their stored block: " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every custom blockstate model and loader model carries both loaders' keys: NeoForge reads
     * {@code "type"} and {@code "loader"}, Fabric only {@code "fabric:type"}. With only NeoForge's
     * key, Fabric loads a plain static model and nothing warns.
     */
    private static void loaderModelsAreReadOnBothLoaders(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        List<String> checkedModels = new ArrayList<>();
        int customVariants = 0;

        for (Map.Entry<ResourceKey<Block>, Block> entry : BuiltInRegistries.BLOCK.entrySet()) {
            Identifier id = entry.getKey().identifier();
            if (!Constants.MOD_ID.equals(id.getNamespace())) {
                continue;
            }
            JsonObject blockstate = readJson("/assets/" + id.getNamespace() + "/blockstates/" + id.getPath() + ".json");
            if (blockstate == null) {
                continue;
            }

            for (JsonObject variant : blockstateVariants(blockstate)) {
                if (variant.has("type")) {
                    customVariants++;
                    if (!variant.get("type").equals(variant.get("fabric:type"))) {
                        wrong.add("blockstate " + id.getPath() + " has type " + variant.get("type") + " but fabric:type " + variant.get("fabric:type"));
                    }
                }

                String model = variant.has("model") ? variant.get("model").getAsString() : null;
                if (model == null || checkedModels.contains(model) || !model.startsWith(Constants.MOD_ID + ":")) {
                    continue;
                }
                checkedModels.add(model);

                Identifier modelId = Identifier.parse(model);
                JsonObject json = readJson("/assets/" + modelId.getNamespace() + "/models/" + modelId.getPath() + ".json");
                if (json != null && json.has("loader") && !json.get("loader").equals(json.get("fabric:type"))) {
                    wrong.add("model " + model + " has loader " + json.get("loader") + " but fabric:type " + json.get("fabric:type"));
                }
            }
        }

        helper.assertTrue(customVariants > 0, "no custom blockstate variants were found to check");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " json(s) Fabric would read as static: " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Every recipe file either loaded or was skipped by this loader's own load conditions; anything
     * else failed to parse.
     */
    private static void everyRecipeLoadsOrIsConditionedOff(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        FileToIdConverter recipes = FileToIdConverter.json("recipe");
        String conditionsKey = Services.PLATFORM.getPlatformName().equals("Fabric") ? "fabric:load_conditions" : "neoforge:conditions";
        List<String> failed = new ArrayList<>();

        recipes.listMatchingResources(server.getResourceManager()).forEach((file, resource) -> {
            Identifier id = recipes.fileToId(file);
            if (!id.getNamespace().equals(Constants.MOD_ID) || server.getRecipeManager().byKey(ResourceKey.create(Registries.RECIPE, id)).isPresent()) {
                return;
            }

            try (BufferedReader reader = resource.openAsReader()) {
                JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                if (!json.has(conditionsKey)) {
                    failed.add(id.toString());
                }
            } catch (IOException e) {
                failed.add(id + " (" + e.getMessage() + ")");
            }
        });

        helper.assertTrue(failed.isEmpty(), failed.size() + " recipes failed to load without being conditioned off: " + String.join(", ", failed.subList(0, Math.min(10, failed.size()))));
        helper.succeed();
    }

    /**
     * Every non-vanilla item tag has a {@code tag.item.<namespace>.<path>} name, the check Fabric
     * API warns about at dev startup. Both loaders name the standard c: tags, so anything missing
     * is ours.
     */
    private static void everyItemTagHasAName(GameTestHelper helper) {
        Language language = Language.getInstance();
        List<String> missing = helper.getLevel().registryAccess().lookupOrThrow(Registries.ITEM).getTags()
                .map(tag -> tag.key().location())
                .filter(id -> !"minecraft".equals(id.getNamespace()))
                .map(id -> "tag.item." + id.getNamespace() + "." + id.getPath().replace('/', '.'))
                .filter(key -> !language.has(key))
                .sorted()
                .toList();
        helper.assertTrue(missing.isEmpty(), "item tags with no name in any lang file: " + missing);
        helper.succeed();
    }

    /**
     * Each of this mod's crafting recipes, laid out in the grid, is answered by that recipe alone.
     * Vanilla matches shaped recipes mirrored too, so a mirror image of another recipe counts.
     */
    private static void craftingRecipesAnswerForNoOther(GameTestHelper helper) {
        List<RecipeHolder<CraftingRecipe>> crafting = new ArrayList<>();
        for (RecipeHolder<?> holder : helper.getLevel().getServer().getRecipeManager().getRecipes()) {
            if (holder.value() instanceof CraftingRecipe) {
                crafting.add(castCrafting(holder));
            }
        }

        List<String> clashes = new ArrayList<>();
        int checked = 0;
        for (RecipeHolder<CraftingRecipe> ours : crafting) {
            if (!Constants.MOD_ID.equals(ours.id().identifier().getNamespace())) {
                continue;
            }
            Optional<CraftingInput> grid = grid(ours.value());
            if (grid.isEmpty()) {
                continue;
            }
            checked++;
            for (RecipeHolder<CraftingRecipe> other : crafting) {
                if (other != ours && other.value().matches(grid.get(), helper.getLevel())) {
                    clashes.add(ours.id().identifier() + " is also " + other.id().identifier());
                }
            }
        }
        helper.assertTrue(checked > 25, "only " + checked + " of this mod's crafting recipes were laid out to check");
        helper.assertTrue(clashes.isEmpty(), clashes.size() + " recipe clash(es): " + String.join("; ", clashes));
        helper.succeed();
    }

    /** The grid a recipe asks for, filled with the first item each ingredient takes. */
    private static Optional<CraftingInput> grid(CraftingRecipe recipe) {
        List<ItemStack> items = new ArrayList<>();
        if (recipe instanceof ShapedRecipe shaped) {
            for (Optional<Ingredient> ingredient : shaped.getIngredients()) {
                items.add(ingredient.map(AssetTests::first).orElse(ItemStack.EMPTY));
            }
            return Optional.of(CraftingInput.of(shaped.getWidth(), shaped.getHeight(), items));
        }
        if (recipe.placementInfo().isImpossibleToPlace()) {
            return Optional.empty();
        }
        recipe.placementInfo().ingredients().forEach(ingredient -> items.add(first(ingredient)));
        if (items.isEmpty() || items.size() > 9) {
            return Optional.empty();
        }
        int width = Math.min(3, items.size());
        int height = (items.size() + 2) / 3;
        while (items.size() < width * height) {
            items.add(ItemStack.EMPTY);
        }
        return Optional.of(CraftingInput.of(width, height, items));
    }

    private static ItemStack first(Ingredient ingredient) {
        return ingredient.items().findFirst().map(Holder::value).map(ItemStack::new).orElse(ItemStack.EMPTY);
    }

    @SuppressWarnings("unchecked")
    private static RecipeHolder<CraftingRecipe> castCrafting(RecipeHolder<?> holder) {
        return (RecipeHolder<CraftingRecipe>) holder;
    }
}
