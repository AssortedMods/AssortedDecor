package com.grim3212.assorted.decorations.gametest;

import net.minecraft.locale.Language;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.server.MinecraftServer;
import com.grim3212.assorted.lib.platform.Services;
import java.io.BufferedReader;
import java.io.IOException;
import com.grim3212.assorted.decorations.Constants;
import com.grim3212.assorted.decorations.Family;
import com.grim3212.assorted.decorations.common.helpers.DecorationsCreativeItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
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

import static com.grim3212.assorted.decorations.gametest.DecorationsTestSupport.*;

/**
 * What the mod ships: models and names, and recipes that load and answer for no other.
 */
final class AssetTests {

    private AssetTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("mod_assets_are_complete", AssetTests::modAssetsAreComplete);
        out.accept("every_recipe_loads_or_is_conditioned_off", AssetTests::everyRecipeLoadsOrIsConditionedOff);
        out.accept("every_item_tag_has_a_name", AssetTests::everyItemTagHasAName);
        out.accept("crafting_recipes_answer_for_no_other", AssetTests::craftingRecipesAnswerForNoOther);
    }

    /**
     * The creative tab is registered, and every block and item has a model and a name. Every gap is
     * reported at once.
     */
    private static void modAssetsAreComplete(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.CREATIVE_MODE_TAB.containsKey(DecorationsCreativeItems.TAB),
                "the Assorted Decor creative tab is not registered");

        JsonObject lang = readJson("/assets/" + Constants.MOD_ID + "/lang/en_us.json");
        helper.assertTrue(lang != null, "/assets/" + Constants.MOD_ID + "/lang/en_us.json is not on the classpath");
        helper.assertTrue(lang.has("itemGroup." + Family.ID), "the creative tab has no name in en_us.json");

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
        helper.assertTrue(checked >= 4, "only " + checked + " of this mod's crafting recipes were laid out to check");
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
