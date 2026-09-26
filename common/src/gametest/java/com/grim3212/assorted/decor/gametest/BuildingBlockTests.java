package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.blocks.building.BeamBlock;
import com.grim3212.assorted.decor.common.blocks.building.BuildingBlocks;
import com.grim3212.assorted.decor.common.blocks.building.ColumnBlock;
import com.grim3212.assorted.decor.common.blocks.building.ColumnPart;
import com.grim3212.assorted.decor.common.blocks.building.WoodSet;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import com.grim3212.assorted.decor.common.crafting.LumberMillRecipe;
import com.grim3212.assorted.decor.common.inventory.LumberMillMenu;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.RecipeDisplayEntry;
import net.minecraft.world.item.crafting.display.SlotDisplay;
import net.minecraft.world.item.crafting.display.StonecutterRecipeDisplay;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.MultifaceBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * The building blocks from More Building Blocks: panels, beams and columns shaping themselves, tool
 * tiers, and fire and fuel.
 */
final class BuildingBlockTests {

    private BuildingBlockTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("panels_line_the_face_they_are_placed_on", BuildingBlockTests::panelsLineTheFaceTheyArePlacedOn);
        out.accept("beams_join_where_they_meet", BuildingBlockTests::beamsJoinWhereTheyMeet);
        out.accept("columns_set_themselves_out", BuildingBlockTests::columnsSetThemselvesOut);
        out.accept("building_blocks_need_the_tool_their_material_does", BuildingBlockTests::buildingBlocksNeedTheToolTheirMaterialDoes);
        out.accept("building_block_walls_join_other_walls", BuildingBlockTests::buildingBlockWallsJoinOtherWalls);
        out.accept("wood_building_blocks_burn_as_their_planks_do", BuildingBlockTests::woodBuildingBlocksBurnAsTheirPlanksDo);
        out.accept("lumber_mill_cuts_planks_into_every_wood_block", BuildingBlockTests::lumberMillCutsPlanksIntoEveryWoodBlock);
        out.accept("lumber_mill_breaks_down_vanilla_wood", BuildingBlockTests::lumberMillBreaksDownVanillaWood);
        out.accept("lumber_mill_recipes_reach_the_recipe_book", BuildingBlockTests::lumberMillRecipesReachTheRecipeBook);
        out.accept("jei_plugin_is_registered_on_fabric", BuildingBlockTests::jeiPluginIsRegisteredOnFabric);
    }

    /** A panel placed against a wall lines that side, needs nothing behind it, and a second one used on it lines another face. */
    private static void panelsLineTheFaceTheyArePlacedOn(GameTestHelper helper) {
        Block panel = BuildingBlocks.OAK.panel().get();
        BlockPos wall = new BlockPos(1, 1, 1);
        helper.setBlock(wall, Blocks.STONE);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        use(helper, player, panel, wall, Direction.EAST);
        BlockPos at = wall.east();
        helper.assertBlockPresent(panel, at);
        helper.assertTrue(MultifaceBlock.hasFace(helper.getBlockState(at), Direction.WEST), "a panel placed on the east face of a wall should line its west side");

        helper.setBlock(wall, Blocks.AIR);
        helper.assertBlockPresent(panel, at);

        player.setXRot(90.0F);
        use(helper, player, panel, at, Direction.UP);
        BlockState both = helper.getBlockState(at);
        helper.assertTrue(MultifaceBlock.hasFace(both, Direction.WEST) && MultifaceBlock.hasFace(both, Direction.DOWN),
                "a panel used on a panel while looking down should add a floor, but the block is " + both);
        helper.succeed();
    }

    /** A beam laid into the side of another grows the arm that meets it; one laid alongside does not. */
    private static void beamsJoinWhereTheyMeet(GameTestHelper helper) {
        Block beam = BuildingBlocks.OAK.beam().get();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        for (int x = 1; x <= 3; x++) {
            helper.setBlock(new BlockPos(x, 1, 1), Blocks.STONE);
        }

        player.setYRot(Direction.SOUTH.toYRot());
        use(helper, player, beam, new BlockPos(2, 1, 1), Direction.UP);
        use(helper, player, beam, new BlockPos(3, 1, 1), Direction.UP);
        BlockPos crossing = new BlockPos(2, 2, 1);
        helper.assertValueEqual(helper.getBlockState(crossing).getValue(BeamBlock.AXIS), Direction.Axis.Z, "axis of a beam placed looking south");
        helper.assertFalse(helper.getBlockState(crossing).getValue(BeamBlock.EAST), "two beams side by side joined up");

        player.setYRot(Direction.EAST.toYRot());
        use(helper, player, beam, new BlockPos(1, 1, 1), Direction.UP);
        helper.assertTrue(helper.getBlockState(crossing).getValue(BeamBlock.WEST), "a beam running into the side of another did not grow an arm to meet it");
        helper.assertTrue(BeamBlock.reaches(helper.getBlockState(new BlockPos(1, 2, 1)), Direction.EAST), "the beam laid looking east does not reach east");
        helper.succeed();
    }

    /** A stack of three upright columns reads base, shaft, capital; taking the top off makes the middle the capital. */
    private static void columnsSetThemselvesOut(GameTestHelper helper) {
        Block column = BuildingBlocks.STONE.column().get();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(new BlockPos(1, 0, 1), Blocks.STONE);
        for (int y = 0; y < 3; y++) {
            use(helper, player, column, new BlockPos(1, y, 1), Direction.UP);
        }

        helper.assertValueEqual(helper.getBlockState(new BlockPos(1, 1, 1)).getValue(ColumnBlock.PART), ColumnPart.BASE, "bottom of three columns");
        helper.assertValueEqual(helper.getBlockState(new BlockPos(1, 2, 1)).getValue(ColumnBlock.PART), ColumnPart.SHAFT, "middle of three columns");
        helper.assertValueEqual(helper.getBlockState(new BlockPos(1, 3, 1)).getValue(ColumnBlock.PART), ColumnPart.CAPITAL, "top of three columns");

        helper.setBlock(new BlockPos(1, 3, 1), Blocks.AIR);
        helper.assertValueEqual(helper.getBlockState(new BlockPos(1, 2, 1)).getValue(ColumnBlock.PART), ColumnPart.CAPITAL, "the middle column once the top one is gone");

        BlockPos wall = new BlockPos(4, 1, 1);
        helper.setBlock(wall, Blocks.STONE);
        use(helper, player, column, wall, Direction.EAST);
        helper.assertValueEqual(helper.getBlockState(wall.east()).getValue(ColumnBlock.AXIS), Direction.Axis.X, "axis of a column placed against the side of a wall");
        helper.succeed();
    }

    /** Every block but the meat is mined with a pickaxe or axe, and the metal and obsidian ones only with vanilla's tier for that material. */
    private static void buildingBlocksNeedTheToolTheirMaterialDoes(GameTestHelper helper) {
        List<String> unmineable = new ArrayList<>();
        for (IRegistryObject<? extends Block> block : BuildingBlocks.all()) {
            BlockState state = block.get().defaultBlockState();
            if (block != BuildingBlocks.MEAT_BLOCK && !state.is(BlockTags.MINEABLE_WITH_PICKAXE) && !state.is(BlockTags.MINEABLE_WITH_AXE)) {
                unmineable.add(block.getId().getPath());
            }
        }
        helper.assertTrue(unmineable.isEmpty(), "no tool mines " + unmineable);

        assertTier(helper, BuildingBlocks.IRON_BRICKS.get(), Items.WOODEN_PICKAXE, Items.STONE_PICKAXE, Blocks.IRON_BLOCK);
        assertTier(helper, BuildingBlocks.GOLD_BRICKS.get(), Items.STONE_PICKAXE, Items.IRON_PICKAXE, Blocks.GOLD_BLOCK);
        assertTier(helper, BuildingBlocks.DIAMOND_BRICKS.get(), Items.STONE_PICKAXE, Items.IRON_PICKAXE, Blocks.DIAMOND_BLOCK);
        assertTier(helper, BuildingBlocks.POLISHED_OBSIDIAN.get(), Items.IRON_PICKAXE, Items.DIAMOND_PICKAXE, Blocks.OBSIDIAN);
        helper.succeed();
    }

    /** An lapis brick wall between a vanilla cobblestone wall and a sandstone tile wall joins both; it only does through {@code #walls}. */
    private static void buildingBlockWallsJoinOtherWalls(GameTestHelper helper) {
        BlockPos middle = new BlockPos(2, 1, 1);
        helper.setBlock(middle.west(), Blocks.COBBLESTONE_WALL);
        helper.setBlock(middle.east(), BuildingBlocks.withCuts(BuildingBlocks.SANDSTONE.tiles()).getLast());
        // Placed as a player would, reading the walls already either side of it.
        BlockState placed = BuildingBlocks.cuts().get(BuildingBlocks.LAPIS.bricks()).wall().get().defaultBlockState();
        helper.setBlock(middle, Block.updateFromNeighbourShapes(placed, helper.getLevel(), helper.absolutePos(middle)));

        BlockState wall = helper.getBlockState(middle);
        helper.assertTrue(wall.getValue(WallBlock.WEST) != WallSide.NONE, "an lapis brick wall does not join the cobblestone wall beside it");
        helper.assertTrue(wall.getValue(WallBlock.EAST) != WallSide.NONE, "an lapis brick wall does not join the sandstone tile wall beside it");
        helper.assertTrue(helper.getBlockState(middle.west()).getValue(WallBlock.EAST) != WallSide.NONE, "a cobblestone wall does not join the lapis brick wall beside it");
        helper.succeed();
    }

    /**
     * Fire takes to every overworld wood block and each burns in a furnace, while the nether woods do
     * neither. Both halves are wired once per loader, so this runs on both.
     */
    private static void woodBuildingBlocksBurnAsTheirPlanksDo(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        BlockPos block = new BlockPos(1, 2, 1);
        for (WoodSet wood : BuildingBlocks.woods()) {
            for (Block made : BuildingBlocks.blocksOf(wood)) {
                helper.setBlock(block, made);
                boolean catches = BaseFireBlock.getState(helper.getLevel(), helper.absolutePos(block.east())).getValueOrElse(FireBlock.WEST, false);
                int burnTime = Services.PLATFORM.getFuelTime(helper.getLevel(), new ItemStack(made));
                if (catches != wood.flammable()) {
                    wrong.add(made + (catches ? " catches fire" : " does not catch fire"));
                }
                if ((burnTime > 0) != wood.flammable()) {
                    wrong.add(made + " burns in a furnace for " + burnTime);
                }
            }
        }
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " wood block(s) burn unlike their planks: " + String.join("; ", wrong));
        helper.succeed();
    }

    /** Oak planks in the lumber mill list every oak block, and picking parquet takes one plank for one parquet. */
    private static void lumberMillCutsPlanksIntoEveryWoodBlock(GameTestHelper helper) {
        BlockPos at = new BlockPos(1, 1, 1);
        helper.setBlock(at, DecorBlocks.LUMBER_MILL.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        LumberMillMenu menu = new LumberMillMenu(1, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(at)));
        menu.container.setItem(0, new ItemStack(Blocks.OAK_PLANKS, 2));

        List<Item> offered = menu.getVisibleRecipes().stream().map(recipe -> recipe.value().resultItem().getItem()).toList();
        List<String> missing = BuildingBlocks.blocksOf(BuildingBlocks.OAK).stream().map(Block::asItem).filter(item -> !offered.contains(item)).map(Item::toString).toList();
        helper.assertTrue(missing.isEmpty(), "the lumber mill does not offer " + missing + " for oak planks");

        int parquet = offered.indexOf(BuildingBlocks.OAK.parquet().get().asItem());
        menu.clickMenuButton(player, parquet);
        helper.assertTrue(menu.getSlot(1).getItem().is(BuildingBlocks.OAK.parquet().get().asItem()), "picking parquet left " + menu.getSlot(1).getItem() + " in the result slot");
        menu.getSlot(1).onTake(player, menu.getSlot(1).getItem());
        helper.assertValueEqual(menu.container.getItem(0).getCount(), 1, "planks left after one cut");
        helper.succeed();
    }

    /** Logs and planks cut into vanilla's own wood, for no fewer planks than crafting them takes; a log into anything its planks make. */
    private static void lumberMillBreaksDownVanillaWood(GameTestHelper helper) {
        assertMillOffers(helper, Blocks.OAK_LOG, Map.of(Items.OAK_PLANKS, 4, Items.STRIPPED_OAK_LOG, 1, Items.OAK_DOOR, 2, Items.OAK_FENCE, 2, Items.OAK_BOAT, 1));
        assertMillOffers(helper, Blocks.STRIPPED_OAK_WOOD, Map.of(Items.OAK_PLANKS, 4, Items.OAK_FENCE_GATE, 1, Items.STICK, 8, Items.OAK_SLAB, 8,
                BuildingBlocks.OAK.parquet().get().asItem(), 4, BuildingBlocks.OAK.panel().get().asItem(), 32, BuildingBlocks.OAK.beam().get().asItem(), 16));
        assertMillOffers(helper, Blocks.OAK_PLANKS, Map.of(Items.OAK_SLAB, 2, Items.OAK_STAIRS, 1, Items.STICK, 2, Items.OAK_BUTTON, 1,
                BuildingBlocks.OAK.panel().get().asItem(), 8, BuildingBlocks.OAK.beam().get().asItem(), 4));
        assertMillOffers(helper, Blocks.CRIMSON_STEM, Map.of(Items.CRIMSON_PLANKS, 4, Items.STRIPPED_CRIMSON_STEM, 1));
        assertMillOffers(helper, Blocks.BAMBOO_BLOCK, Map.of(Items.BAMBOO_PLANKS, 2, Items.BAMBOO_DOOR, 1, Items.STICK, 4, Items.BAMBOO_MOSAIC, 2));
        assertMillOffers(helper, Blocks.BAMBOO_PLANKS, Map.of(Items.BAMBOO_MOSAIC, 1));

        // Planks never make what takes more than one, and a bamboo block is too little for a raft.
        assertMillLacks(helper, Blocks.OAK_PLANKS, List.of(Items.OAK_DOOR, Items.OAK_BOAT, Items.OAK_FENCE));
        assertMillLacks(helper, Blocks.BAMBOO_BLOCK, List.of(Items.BAMBOO_RAFT, Items.BAMBOO_TRAPDOOR));
        helper.succeed();
    }

    private static List<ItemStack> millOffers(GameTestHelper helper, Block input) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        LumberMillMenu menu = new LumberMillMenu(1, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(BlockPos.ZERO)));
        menu.container.setItem(0, new ItemStack(input));
        return menu.getVisibleRecipes().stream().map(recipe -> recipe.value().resultItem()).toList();
    }

    private static void assertMillOffers(GameTestHelper helper, Block input, Map<Item, Integer> expected) {
        List<ItemStack> offered = millOffers(helper, input);
        expected.forEach((item, count) -> helper.assertTrue(offered.stream().anyMatch(stack -> stack.is(item) && stack.getCount() == count),
                "the lumber mill does not cut " + input + " into " + count + " " + item + ", only " + offered));
    }

    private static void assertMillLacks(GameTestHelper helper, Block input, List<Item> unexpected) {
        List<ItemStack> offered = millOffers(helper, input);
        unexpected.forEach(item -> helper.assertFalse(offered.stream().anyMatch(stack -> stack.is(item)), "the lumber mill cuts " + input + " into " + item));
    }

    /**
     * Every lumber mill recipe is unlocked by its advancement and hands the recipe book a display with
     * the lumber mill as its station, the way a stonecutter recipe does.
     */
    private static void lumberMillRecipesReachTheRecipeBook(GameTestHelper helper) {
        MinecraftServer server = helper.getLevel().getServer();
        List<String> wrong = new ArrayList<>();
        int checked = 0;
        for (RecipeHolder<?> holder : server.getRecipeManager().getRecipes()) {
            if (!(holder.value() instanceof LumberMillRecipe recipe)) {
                continue;
            }
            checked++;
            Identifier id = holder.id().identifier();
            if (server.getAdvancements().get(Identifier.fromNamespaceAndPath(id.getNamespace(), "recipes/building_blocks/" + id.getPath())) == null) {
                wrong.add(id + " has no advancement to unlock it");
            }
            List<RecipeDisplayEntry> entries = new ArrayList<>();
            server.getRecipeManager().listDisplaysForRecipe(holder.id(), entries::add);
            boolean atTheMill = recipe.display().stream().anyMatch(display -> display instanceof StonecutterRecipeDisplay cut
                    && cut.craftingStation() instanceof SlotDisplay.ItemSlotDisplay station && station.item().value() == DecorBlocks.LUMBER_MILL.get().asItem());
            if (entries.isEmpty() || !atTheMill) {
                wrong.add(id + " has no recipe book display at the lumber mill");
            }
        }
        helper.assertTrue(checked > 100, "only " + checked + " lumber mill recipes were loaded");
        helper.assertTrue(wrong.isEmpty(), wrong.size() + " lumber mill recipe(s) cannot reach the recipe book: " + String.join("; ", wrong));
        helper.succeed();
    }

    private static void jeiPluginIsRegisteredOnFabric(GameTestHelper helper) {
        com.grim3212.assorted.lib.test.TestSupport.assertJeiPluginIsRegistered(helper, "assorteddecor", "com.grim3212.assorted.decor.compat.jei.JEIAssortedDecor");
        helper.succeed();
    }

    private static void assertTier(GameTestHelper helper, Block block, Item tooWeak, Item enough, Block material) {
        BlockState state = block.defaultBlockState();
        helper.assertFalse(new ItemStack(tooWeak).isCorrectToolForDrops(state), block + " drops for a " + tooWeak + ", which " + material + " does not");
        helper.assertTrue(new ItemStack(enough).isCorrectToolForDrops(state), block + " does not drop for a " + enough + ", which " + material + " does");
    }

    /** Uses a block's item on a face of {@code against}, as a player right clicking its middle. */
    private static void use(GameTestHelper helper, Player player, Block block, BlockPos against, Direction face) {
        ItemStack stack = new ItemStack(block);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos abs = helper.absolutePos(against);
        Vec3 hit = Vec3.atCenterOf(abs).add(Vec3.atLowerCornerOf(face.getUnitVec3i()).scale(0.5));
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, new BlockHitResult(hit, face, abs, false)));
    }
}
