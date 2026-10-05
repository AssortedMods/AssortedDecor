package com.grim3212.assorted.paint.gametest;

import com.grim3212.assorted.paint.api.PaintTags;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.core.block.ICanColor;
import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.lib.util.LibCommonTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.sheep.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.paint.gametest.PaintTestSupport.*;

/**
 * Paint rollers: recolouring blocks and sheep, and the dye tags other mods know a roller by.
 */
final class PaintTests {

    private PaintTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("paint_roller_recolors_blocks", PaintTests::paintRollerRecolorsBlocks);
        out.accept("paint_rollers_recolor_every_color", PaintTests::paintRollersRecolorEveryColor);
        out.accept("paint_roller_dyes_a_sheep", PaintTests::paintRollerDyesASheep);
        out.accept("paint_rollers_are_dyes_of_their_color", PaintTests::paintRollersAreDyesOfTheirColor);
        out.accept("paint_roller_dyes_wool_and_carpet_in_the_grid", PaintTests::paintRollerDyesWoolAndCarpetInTheGrid);
    }

    /**
     * A paint roller repaints both kinds of target: a vanilla dyed block, which it swaps for the
     * matching block of its own colour, and every {@code ICanColor} block of whichever mods are installed.
     */
    private static void paintRollerRecolorsBlocks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wool = new BlockPos(2, 1, 4);
        BlockPos colorable = new BlockPos(6, 1, 4);

        helper.setBlock(wool, DyeHelper.WOOL_BY_DYE.get(DyeColor.WHITE));

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack roller = new ItemStack(PaintItems.PAINT_ROLLER_COLORS.get(DyeColor.BLUE).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, roller);

        roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(wool))));
        helper.assertBlockPresent(DyeHelper.WOOL_BY_DYE.get(DyeColor.BLUE), wool);

        // None of this mod's own blocks take paint, so these only turn up when another part is installed.
        List<String> unpainted = new ArrayList<>();
        for (Block block : BuiltInRegistries.BLOCK) {
            if (!(block instanceof ICanColor canColor) || canColor.currentColor(block.defaultBlockState()) == DyeColor.BLUE) {
                continue;
            }

            helper.setBlock(colorable.below(), Blocks.STONE);
            helper.setBlock(colorable, block.defaultBlockState());
            ItemStack fresh = new ItemStack(PaintItems.PAINT_ROLLER_COLORS.get(DyeColor.BLUE).get());
            player.setItemInHand(InteractionHand.MAIN_HAND, fresh);
            fresh.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(colorable))));

            BlockState painted = level.getBlockState(helper.absolutePos(colorable));
            if (!(painted.getBlock() instanceof ICanColor paintedColor) || paintedColor.currentColor(painted) != DyeColor.BLUE) {
                unpainted.add(BuiltInRegistries.BLOCK.getKey(block).toString());
            }
        }

        helper.assertTrue(unpainted.isEmpty(), "a blue roller left these blocks their own color: " + unpainted);
        helper.succeed();
    }

    /**
     * All sixteen rollers, against both kinds of vanilla dyed block the roller knows about.
     * {@code DyeHelper.BLOCKS_BY_DYE} is the whole list it searches, and it holds wool, concrete,
     * concrete powder and carpet - not terracotta, despite what the checklist used to claim.
     */
    private static void paintRollersRecolorEveryColor(GameTestHelper helper) {
        BlockPos wool = new BlockPos(2, 1, 4);
        BlockPos concrete = new BlockPos(6, 1, 4);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        for (DyeColor color : DyeColor.values()) {
            // A roller does nothing to a block already its own colour, so start somewhere else.
            DyeColor from = color == DyeColor.WHITE ? DyeColor.BLACK : DyeColor.WHITE;
            helper.setBlock(wool, DyeHelper.WOOL_BY_DYE.get(from));
            helper.setBlock(concrete, DyeHelper.CONCRETE_BY_DYE.get(from));

            ItemStack roller = new ItemStack(PaintItems.PAINT_ROLLER_COLORS.get(color).get());
            player.setItemInHand(InteractionHand.MAIN_HAND, roller);

            roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(wool))));
            roller.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(concrete))));

            helper.assertBlockPresent(DyeHelper.WOOL_BY_DYE.get(color), wool);
            helper.assertBlockPresent(DyeHelper.CONCRETE_BY_DYE.get(color), concrete);
            helper.assertValueEqual(roller.getDamageValue(), 2, "durability the " + color.getName() + " roller spent on two blocks");
        }

        helper.succeed();
    }

    /** A roller dyes a sheep the same way a dye would, and wears by one for it. */
    private static void paintRollerDyesASheep(GameTestHelper helper) {
        Sheep sheep = helper.spawn(EntityTypes.SHEEP, MAIN);
        sheep.setColor(DyeColor.WHITE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack roller = new ItemStack(PaintItems.PAINT_ROLLER_COLORS.get(DyeColor.RED).get());
        player.setItemInHand(InteractionHand.MAIN_HAND, roller);

        helper.assertTrue(roller.getItem().interactLivingEntity(roller, player, sheep, InteractionHand.MAIN_HAND).consumesAction(),
                "the roller did not handle a sheep");
        helper.assertTrue(sheep.getColor() == DyeColor.RED, "the roller did not dye the sheep");
        helper.assertValueEqual(roller.getDamageValue(), 1, "durability the roller spent on a sheep");
        helper.succeed();
    }

    /**
     * Every roller is a dye of its own color. Other mods find a roller by these tags rather than by
     * its class, Assorted Roads' white line markings among them.
     */
    private static void paintRollersAreDyesOfTheirColor(GameTestHelper helper) {
        List<String> untagged = new ArrayList<>();
        PaintItems.PAINT_ROLLER_COLORS.forEach((color, roller) -> {
            ItemStack stack = new ItemStack(roller.get());
            if (!stack.is(LibCommonTags.Items.DYES) || !stack.is(DyeHelper.getDyeTag(color)) || !stack.is(PaintTags.Items.PAINT_ROLLERS)) {
                untagged.add(color.getName());
            }
        });

        helper.assertTrue(untagged.isEmpty(), "rollers missing from their dye tags: " + untagged);
        helper.succeed();
    }

    /** On both loaders a roller dyes wool and carpet in the crafting grid like a dye, and comes back worn by one. */
    private static void paintRollerDyesWoolAndCarpetInTheGrid(GameTestHelper helper) {
        ItemStack roller = new ItemStack(PaintItems.PAINT_ROLLER_COLORS.get(DyeColor.BLUE).get());
        for (Block white : List.of(DyeHelper.WOOL_BY_DYE.get(DyeColor.WHITE), DyeHelper.CARPET_BY_DYE.get(DyeColor.WHITE))) {
            CraftingInput input = CraftingInput.of(2, 1, List.of(roller.copy(), new ItemStack(white)));
            Optional<RecipeHolder<CraftingRecipe>> found = helper.getLevel().recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, helper.getLevel());
            helper.assertTrue(found.isPresent(), "no recipe dyes " + BuiltInRegistries.BLOCK.getKey(white) + " with a roller");

            CraftingRecipe recipe = found.get().value();
            Block expected = white == DyeHelper.WOOL_BY_DYE.get(DyeColor.WHITE) ? DyeHelper.WOOL_BY_DYE.get(DyeColor.BLUE) : DyeHelper.CARPET_BY_DYE.get(DyeColor.BLUE);
            helper.assertTrue(recipe.assemble(input).is(expected.asItem()), BuiltInRegistries.BLOCK.getKey(white) + " did not come out blue");

            NonNullList<ItemStack> remaining = recipe.getRemainingItems(input);
            helper.assertTrue(remaining.get(0).getItem() == roller.getItem(), "the roller was not handed back");
            helper.assertValueEqual(remaining.get(0).getDamageValue(), 1, "durability the roller spent in the grid");
        }
        helper.succeed();
    }
}
