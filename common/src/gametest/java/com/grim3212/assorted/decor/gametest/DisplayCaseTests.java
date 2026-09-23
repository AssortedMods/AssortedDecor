package com.grim3212.assorted.decor.gametest;

import com.grim3212.assorted.decor.common.blocks.DecorBlocks;
import com.grim3212.assorted.decor.common.items.DecorItems;
import com.grim3212.assorted.decor.common.blocks.DisplayCaseBlock;
import com.grim3212.assorted.decor.common.blocks.MuseumDisplayCaseBlock;
import com.grim3212.assorted.decor.common.blocks.WeatheringDisplayCaseBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.DisplayCaseBlockEntity;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.WeatheringCopper.WeatherState;
import net.minecraft.world.level.block.WeatheringCopperCollection;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.decor.gametest.DecorTestSupport.MAIN;
import static com.grim3212.assorted.lib.test.TestSupport.*;

/**
 * Display cases: putting items in and taking them back out by the spot that was clicked, the grid's
 * orientation following the case's facing, and the museum edition's two halves and placard.
 */
final class DisplayCaseTests {

    private DisplayCaseTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("display_case_holds_the_item_where_it_was_clicked", DisplayCaseTests::displayCaseHoldsTheItemWhereItWasClicked);
        out.accept("display_case_grid_follows_its_facing", DisplayCaseTests::displayCaseGridFollowsItsFacing);
        out.accept("display_case_front_pane_reaches_every_row", DisplayCaseTests::displayCaseFrontPaneReachesEveryRow);
        out.accept("display_case_drops_what_is_on_show", DisplayCaseTests::displayCaseDropsWhatIsOnShow);
        out.accept("display_case_keeps_its_items_through_a_reload", DisplayCaseTests::displayCaseKeepsItsItemsThroughAReload);
        out.accept("display_case_feeds_a_comparator", DisplayCaseTests::displayCaseFeedsAComparator);
        out.accept("museum_display_case_places_and_breaks_as_one", DisplayCaseTests::museumDisplayCasePlacesAndBreaksAsOne);
        out.accept("museum_display_case_shows_items_in_its_glass_half", DisplayCaseTests::museumDisplayCaseShowsItemsInItsGlassHalf);
        out.accept("museum_display_case_placard_takes_a_name_tag", DisplayCaseTests::museumDisplayCasePlacardTakesANameTag);
        out.accept("copper_display_cases_oxidise_and_keep_what_is_on_show", DisplayCaseTests::copperDisplayCasesOxidiseAndKeepWhatIsOnShow);
        out.accept("copper_display_cases_scrape_and_wax", DisplayCaseTests::copperDisplayCasesScrapeAndWax);
        out.accept("resizing_tool_cycles_the_case", DisplayCaseTests::resizingToolCyclesTheCase);
        out.accept("display_case_grid_follows_its_size", DisplayCaseTests::displayCaseGridFollowsItsSize);
        out.accept("resizing_down_hands_back_what_it_cannot_show", DisplayCaseTests::resizingDownHandsBackWhatItCannotShow);
    }

    /** A case at its full three by three, which is what most of these tests want to talk about. */
    private static BlockState facing(DisplayCaseBlock displayCase, Direction facing) {
        return sized(displayCase, facing, DisplayCaseBlock.MAX_SIZE);
    }

    private static BlockState sized(DisplayCaseBlock displayCase, Direction facing, int size) {
        return displayCase.defaultBlockState().setValue(DisplayCaseBlock.FACING, facing).setValue(DisplayCaseBlock.SIZE, size);
    }

    /**
     * Exactly where the renderer stands the item for {@code slot}: the middle of its cell, resting
     * on its shelf. The whole point of the hit test is that aiming here picks this slot, so this is
     * what the tests aim at.
     */
    private static Vec3 drawnAt(GameTestHelper helper, BlockPos rel, Direction facing, int slot) {
        return drawnAt(helper, rel, facing, DisplayCaseBlock.MAX_SIZE, slot);
    }

    private static Vec3 drawnAt(GameTestHelper helper, BlockPos rel, Direction facing, int size, int slot) {
        double across = (DisplayCaseBlock.column(slot) + 0.5D) / size;
        double depth = (DisplayCaseBlock.row(slot) + 0.5D) / size;

        BlockPos pos = helper.absolutePos(rel);
        return new Vec3(pos.getX() + DisplayCaseBlock.localX(facing, across, depth),
                pos.getY() + DisplayCaseBlock.shelfY(size, DisplayCaseBlock.row(slot)) + DisplayCaseBlock.itemScale(size) / 2.0D,
                pos.getZ() + DisplayCaseBlock.localZ(facing, across, depth));
    }

    /**
     * A click aimed at the item drawn in {@code slot}. Only the location matters - the slot comes
     * from the line the player is looking along, not from the face that stopped it.
     */
    private static BlockHitResult aimAt(GameTestHelper helper, BlockPos rel, Direction facing, int slot) {
        return new BlockHitResult(drawnAt(helper, rel, facing, slot), facing, helper.absolutePos(rel), false);
    }

    /** Stands {@code player} a couple of blocks off the front of the case, looking at it. */
    private static void standInFront(GameTestHelper helper, ServerPlayer player, BlockPos rel, Direction facing) {
        BlockPos from = rel.relative(facing, 2);
        stand(helper, player, from.below());
        player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(helper.absolutePos(rel)));
    }

    private static DisplayCaseBlockEntity caseAt(GameTestHelper helper, BlockPos rel) {
        return helper.getBlockEntity(rel, DisplayCaseBlockEntity.class);
    }

    private static void use(GameTestHelper helper, ServerPlayer player, ItemStack held, BlockHitResult hit) {
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        player.gameMode.useItemOn(player, helper.getLevel(), player.getMainHandItem(), InteractionHand.MAIN_HAND, hit);
    }

    /** The slot aimed at is the slot filled, and an empty hand on the same spot hands it back. */
    private static void displayCaseHoldsTheItemWhereItWasClicked(GameTestHelper helper) {
        helper.setBlock(MAIN, facing(DecorBlocks.GOLD_DISPLAY_CASE.get(), Direction.NORTH));
        ServerPlayer player = survivalPlayer(helper);
        standInFront(helper, player, MAIN, Direction.NORTH);

        use(helper, player, new ItemStack(Items.DIAMOND, 3), aimAt(helper, MAIN, Direction.NORTH, 4));
        DisplayCaseBlockEntity displayCase = caseAt(helper, MAIN);
        helper.assertTrue(displayCase.getItem(4).is(Items.DIAMOND), "the clicked slot did not take the diamond");
        helper.assertValueEqual(displayCase.getItem(4).getCount(), 1, "diamonds put on show at once");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 2, "diamonds left in hand");
        for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
            if (slot != 4) {
                helper.assertTrue(displayCase.getItem(slot).isEmpty(), "slot " + slot + " filled itself");
            }
        }

        // A slot that is already full hands its item back rather than stacking a second one in.
        use(helper, player, new ItemStack(Items.DIAMOND, 2), aimAt(helper, MAIN, Direction.NORTH, 4));
        helper.assertTrue(displayCase.getItem(4).isEmpty(), "clicking a full slot did not empty it");
        helper.assertValueEqual(countInInventory(player, Items.DIAMOND), 3, "diamonds back with the player");

        helper.succeed();
    }

    /**
     * Aiming at a drawn item picks it, at every facing and from level or above. A hit test that
     * splits the face into thirds instead answers the middle row for a back row item.
     */
    private static void displayCaseGridFollowsItsFacing(GameTestHelper helper) {
        DisplayCaseBlock displayCase = DecorBlocks.IRON_DISPLAY_CASE.get();
        ServerPlayer player = survivalPlayer(helper);

        for (Direction facing : Direction.Plane.HORIZONTAL) {
            BlockState state = facing(displayCase, facing);
            helper.setBlock(MAIN, state);

            for (int eyeHeight = 0; eyeHeight < 2; eyeHeight++) {
                // Level with the case, and then from a block above it looking down in.
                stand(helper, player, MAIN.relative(facing, 2).below().above(eyeHeight * 2));

                for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
                    Vec3 item = drawnAt(helper, MAIN, facing, slot);
                    player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, item);

                    OptionalInt aimed = displayCase.getHitSlot(state, helper.absolutePos(MAIN), player,
                            new BlockHitResult(item, facing, helper.absolutePos(MAIN), false));
                    helper.assertValueEqual(aimed, OptionalInt.of(slot), "the slot aimed at, facing " + facing + " from " + (eyeHeight == 0 ? "level" : "above"));
                }
            }
        }

        helper.succeed();
    }

    /** The back row is reachable through the front pane, and does not answer as the middle row. */
    private static void displayCaseFrontPaneReachesEveryRow(GameTestHelper helper) {
        helper.setBlock(MAIN, facing(DecorBlocks.STONE_DISPLAY_CASE.get(), Direction.SOUTH));
        ServerPlayer player = survivalPlayer(helper);
        standInFront(helper, player, MAIN, Direction.SOUTH);

        use(helper, player, new ItemStack(Items.APPLE), aimAt(helper, MAIN, Direction.SOUTH, 1));
        use(helper, player, new ItemStack(Items.BREAD), aimAt(helper, MAIN, Direction.SOUTH, 7));

        DisplayCaseBlockEntity displayCase = caseAt(helper, MAIN);
        helper.assertTrue(displayCase.getItem(1).is(Items.APPLE), "the front row did not take the apple");
        helper.assertTrue(displayCase.getItem(7).is(Items.BREAD), "the back row took nothing - it is the row a middle row answer steals");
        helper.assertTrue(displayCase.getItem(4).isEmpty(), "aiming at the back row filled the middle row instead");

        helper.succeed();
    }

    private static void displayCaseDropsWhatIsOnShow(GameTestHelper helper) {
        helper.setBlock(MAIN, facing(DecorBlocks.DIAMOND_DISPLAY_CASE.get(), Direction.NORTH));
        DisplayCaseBlockEntity displayCase = caseAt(helper, MAIN);
        displayCase.setItem(0, new ItemStack(Items.EMERALD));
        displayCase.setItem(8, new ItemStack(Items.GOLD_INGOT));

        ServerPlayer player = survivalPlayer(helper);
        stand(helper, player, MAIN.below());
        player.gameMode.destroyBlock(helper.absolutePos(MAIN));

        helper.assertBlockPresent(Blocks.AIR, MAIN);
        assertDropped(helper, Items.EMERALD, 1);
        assertDropped(helper, Items.GOLD_INGOT, 1);
        assertDropped(helper, DecorBlocks.DIAMOND_DISPLAY_CASE.get().asItem(), 1);

        helper.succeed();
    }

    private static void displayCaseKeepsItsItemsThroughAReload(GameTestHelper helper) {
        helper.setBlock(MAIN, facing(DecorBlocks.COPPER_DISPLAY_CASES.weathering().unaffected().get(), Direction.WEST));
        caseAt(helper, MAIN).setItem(3, new ItemStack(Items.NETHER_STAR));

        DisplayCaseBlockEntity reloaded = afterReload(helper, MAIN, DisplayCaseBlockEntity.class);
        helper.assertTrue(reloaded.getItem(3).is(Items.NETHER_STAR), "the case lost what it was showing over a save and load");

        helper.succeed();
    }

    private static void displayCaseFeedsAComparator(GameTestHelper helper) {
        helper.setBlock(MAIN, facing(DecorBlocks.WOODEN_DISPLAY_CASE.get(), Direction.NORTH));
        BlockState state = helper.getBlockState(MAIN);

        helper.assertValueEqual(state.getAnalogOutputSignal(helper.getLevel(), helper.absolutePos(MAIN), Direction.UP), 0, "the signal out of an empty case");

        DisplayCaseBlockEntity displayCase = caseAt(helper, MAIN);
        for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
            displayCase.setItem(slot, new ItemStack(Items.STICK));
        }
        helper.assertValueEqual(state.getAnalogOutputSignal(helper.getLevel(), helper.absolutePos(MAIN), Direction.UP), 15, "the signal out of a full case");

        helper.succeed();
    }

    /** One item places both halves, and breaking either takes the other and pays out once. */
    private static void museumDisplayCasePlacesAndBreaksAsOne(GameTestHelper helper) {
        MuseumDisplayCaseBlock museum = DecorBlocks.MUSEUM_DISPLAY_CASE.get();
        helper.setBlock(MAIN, Blocks.STONE);

        // Off to one side: the case is a full cube, and standing in the space it needs blocks it.
        ServerPlayer player = survivalPlayer(helper, new ItemStack(museum));
        stand(helper, player, new BlockPos(MAIN.getX(), 0, 2));
        useOnTopOf(helper, player, MAIN);

        helper.assertBlockPresent(museum, MAIN.above());
        helper.assertBlockProperty(MAIN.above(), MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.LOWER);
        helper.assertBlockPresent(museum, MAIN.above(2));
        helper.assertBlockProperty(MAIN.above(2), MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.UPPER);
        helper.assertTrue(player.getMainHandItem().isEmpty(), "placing the museum case did not use the item");

        // Breaking the glass half takes the plinth with it, and only one case comes back.
        caseAt(helper, MAIN.above()).setItem(0, new ItemStack(Items.EMERALD));
        player.gameMode.destroyBlock(helper.absolutePos(MAIN.above(2)));
        helper.assertBlockNotPresent(museum, MAIN.above());
        helper.assertBlockNotPresent(museum, MAIN.above(2));
        assertDropped(helper, museum.asItem(), 1);
        assertDropped(helper, Items.EMERALD, 1);

        helper.succeed();
    }

    /** The glass half is the grid; the plinth under it is not. */
    private static void museumDisplayCaseShowsItemsInItsGlassHalf(GameTestHelper helper) {
        MuseumDisplayCaseBlock museum = DecorBlocks.MUSEUM_DISPLAY_CASE.get();
        BlockPos plinth = MAIN;
        BlockPos glass = MAIN.above();
        helper.setBlock(plinth, facing(museum, Direction.NORTH).setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(glass, facing(museum, Direction.NORTH).setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.UPPER));

        ServerPlayer player = survivalPlayer(helper);
        stand(helper, player, plinth.below());

        standInFront(helper, player, glass, Direction.NORTH);
        use(helper, player, new ItemStack(Items.DIAMOND), aimAt(helper, glass, Direction.NORTH, 2));
        DisplayCaseBlockEntity displayCase = caseAt(helper, plinth);
        helper.assertTrue(displayCase.getItem(2).is(Items.DIAMOND), "the glass half did not take the diamond");
        helper.assertTrue(helper.getLevel().getBlockEntity(helper.absolutePos(glass)) == null, "the glass half has a block entity of its own");

        use(helper, player, new ItemStack(Items.APPLE), aimAt(helper, plinth, Direction.NORTH, 4));
        helper.assertTrue(displayCase.getItem(4).isEmpty(), "the plinth took an item");

        helper.succeed();
    }

    private static void museumDisplayCasePlacardTakesANameTag(GameTestHelper helper) {
        MuseumDisplayCaseBlock museum = DecorBlocks.MUSEUM_DISPLAY_CASE.get();
        helper.setBlock(MAIN, facing(museum, Direction.NORTH).setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.LOWER));
        helper.setBlock(MAIN.above(), facing(museum, Direction.NORTH).setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.UPPER));

        ServerPlayer player = survivalPlayer(helper);
        stand(helper, player, MAIN.below());

        ItemStack tag = new ItemStack(Items.NAME_TAG, 2);
        tag.set(DataComponents.CUSTOM_NAME, Component.literal("Tyrannosaurus"));
        use(helper, player, tag, hitSide(helper.absolutePos(MAIN), Direction.NORTH));

        DisplayCaseBlockEntity displayCase = caseAt(helper, MAIN);
        helper.assertValueEqual(displayCase.getCustomName(), Component.literal("Tyrannosaurus"), "the placard's name");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "name tags left in hand");

        // A blank tag wipes the placard and is not spent doing it.
        use(helper, player, new ItemStack(Items.NAME_TAG), hitSide(helper.absolutePos(MAIN), Direction.NORTH));
        helper.assertTrue(displayCase.getCustomName() == null, "a blank name tag did not clear the placard");
        helper.assertValueEqual(player.getMainHandItem().getCount(), 1, "name tags left after clearing");

        helper.succeed();
    }

    /**
     * Each unwaxed stage but the last is randomly ticked and steps to the next one, and what the
     * case was showing survives the step - the block changes under a block entity that has to be
     * kept, or the whole shelf drops on the floor every time copper weathers.
     */
    private static void copperDisplayCasesOxidiseAndKeepWhatIsOnShow(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();
        ServerLevel level = helper.getLevel();

        WeatheringCopperCollection.zipApply(WeatheringCopperCollection.STATES, DecorBlocks.COPPER_DISPLAY_CASES.weathering(), (age, displayCase) -> {
            DisplayCaseBlock block = displayCase.get();
            boolean shouldOxidise = age != WeatherState.OXIDIZED;
            helper.setBlock(MAIN, facing(block, Direction.NORTH));
            caseAt(helper, MAIN).setItem(4, new ItemStack(Items.DIAMOND));

            // The baked property that actually schedules the change. It cannot be worked out from
            // the loaders' oxidation registries, which load later, so it is the thing to assert.
            if (helper.getBlockState(MAIN).isRandomlyTicking() != shouldOxidise) {
                wrong.add(name(block) + (shouldOxidise ? " is not randomly ticked, so it never oxidises" : " is randomly ticked but has nowhere to go"));
                return;
            }
            if (!shouldOxidise) {
                return;
            }

            Optional<BlockState> next = ((WeatheringDisplayCaseBlock) block).getNext(helper.getBlockState(MAIN));
            if (next.isEmpty()) {
                wrong.add(name(block) + " has no next oxidation stage");
                return;
            }

            // Exactly what ChangeOverTimeBlock#changeOverTime does once its roll succeeds.
            level.setBlockAndUpdate(helper.absolutePos(MAIN), next.get());

            Block expected = DecorBlocks.COPPER_DISPLAY_CASES.weathering().pick(age.next()).get();
            if (!helper.getBlockState(MAIN).is(expected)) {
                wrong.add(name(block) + " became " + name(helper.getBlockState(MAIN).getBlock()) + ", not " + name(expected));
            }
            if (!(level.getBlockEntity(helper.absolutePos(MAIN)) instanceof DisplayCaseBlockEntity oxidised) || !oxidised.getItem(4).is(Items.DIAMOND)) {
                wrong.add(name(block) + " lost what it was showing when it oxidised");
            }
            if (helper.getBlockState(MAIN).getValue(DisplayCaseBlock.FACING) != Direction.NORTH) {
                wrong.add(name(block) + " turned when it oxidised");
            }
        });

        if (helper.getEntities(EntityTypes.ITEM).stream().anyMatch(item -> item.getItem().is(Items.DIAMOND))) {
            wrong.add("a diamond was dropped while oxidising - the block entity was removed rather than kept");
        }

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " oxidation problem(s): " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * Asked the way the axe and honeycomb ask, because NeoForge ignores the vanilla
     * {@code WAXABLES} / {@code NEXT_BY_BLOCK} fields. Miss a registry and it fails silently.
     */
    private static void copperDisplayCasesScrapeAndWax(GameTestHelper helper) {
        List<String> wrong = new ArrayList<>();

        DecorBlocks.COPPER_DISPLAY_CASES.weathering().progressMapping((from, to) -> {
            Optional<BlockState> scraped = WeatheringCopper.getPrevious(to.get().defaultBlockState());
            if (scraped.filter(state -> state.is(from.get())).isEmpty()) {
                wrong.add(name(to.get()) + " scrapes back to " + scraped.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(from.get()));
            }
        });

        DecorBlocks.COPPER_DISPLAY_CASES.zipUnwaxedWaxed((unwaxed, waxed) -> {
            Optional<BlockState> waxedState = HoneycombItem.getWaxed(unwaxed.get().defaultBlockState());
            if (waxedState.filter(state -> state.is(waxed.get())).isEmpty()) {
                wrong.add(name(unwaxed.get()) + " waxes into " + waxedState.map(state -> name(state.getBlock())).orElse("nothing") + ", not " + name(waxed.get()));
            }
        });

        helper.assertTrue(wrong.isEmpty(), wrong.size() + " scrape/wax problem(s) on " + Services.PLATFORM.getPlatformName() + ": " + String.join("; ", wrong));
        helper.succeed();
    }

    /**
     * The tool walks a case round one, four, nine and back to one, and every size answers for the
     * slots it shows and only those.
     */
    private static void resizingToolCyclesTheCase(GameTestHelper helper) {
        DisplayCaseBlock displayCase = DecorBlocks.IRON_DISPLAY_CASE.get();
        helper.setBlock(MAIN, sized(displayCase, Direction.NORTH, 1));

        ServerPlayer player = survivalPlayer(helper, new ItemStack(DecorItems.RESIZING_TOOL.get()));
        standInFront(helper, player, MAIN, Direction.NORTH);

        for (int expected : new int[]{2, 3, 1, 2}) {
            player.getMainHandItem().useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitTop(helper.absolutePos(MAIN))));
            helper.assertBlockProperty(MAIN, DisplayCaseBlock.SIZE, expected);
        }

        helper.succeed();
    }

    /** Aiming at a drawn item picks it at every size, not just at the three by three. */
    private static void displayCaseGridFollowsItsSize(GameTestHelper helper) {
        DisplayCaseBlock displayCase = DecorBlocks.IRON_DISPLAY_CASE.get();
        ServerPlayer player = survivalPlayer(helper);
        stand(helper, player, MAIN.relative(Direction.NORTH, 2).below());

        for (int size = 1; size <= DisplayCaseBlock.MAX_SIZE; size++) {
            BlockState state = sized(displayCase, Direction.NORTH, size);
            helper.setBlock(MAIN, state);

            for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
                Vec3 item = drawnAt(helper, MAIN, Direction.NORTH, size, slot);
                player.lookAt(net.minecraft.commands.arguments.EntityAnchorArgument.Anchor.EYES, item);
                OptionalInt aimed = displayCase.getHitSlot(state, helper.absolutePos(MAIN), player,
                        new BlockHitResult(item, Direction.NORTH, helper.absolutePos(MAIN), false));

                if (DisplayCaseBlock.isShown(size, slot)) {
                    helper.assertValueEqual(aimed, OptionalInt.of(slot), "the slot aimed at in a case of " + size);
                } else {
                    // A slot the case is not showing has no box of its own; whatever the look finds
                    // instead, it must not be that slot.
                    helper.assertFalse(aimed.isPresent() && aimed.getAsInt() == slot,
                            "slot " + slot + " answered in a case of " + size + ", which does not show it");
                }
            }
        }

        helper.succeed();
    }

    /**
     * Shrinking hands back what the smaller case cannot show, and growing again finds the rest
     * still in place. Losing eight items to a click would be a poor way to learn what the tool does.
     */
    private static void resizingDownHandsBackWhatItCannotShow(GameTestHelper helper) {
        DisplayCaseBlock displayCase = DecorBlocks.GOLD_DISPLAY_CASE.get();
        helper.setBlock(MAIN, sized(displayCase, Direction.NORTH, 3));

        DisplayCaseBlockEntity stocked = caseAt(helper, MAIN);
        for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
            stocked.setItem(slot, new ItemStack(Items.DIAMOND));
        }

        // Straight back down to one: eight of the nine have nowhere to go.
        displayCase.resize(helper.getBlockState(MAIN), helper.getLevel(), helper.absolutePos(MAIN));

        helper.assertBlockProperty(MAIN, DisplayCaseBlock.SIZE, 1);
        helper.assertTrue(caseAt(helper, MAIN).getItem(0).is(Items.DIAMOND), "the one slot a single case shows was emptied too");
        for (int slot = 1; slot < DisplayCaseBlock.SLOTS; slot++) {
            helper.assertTrue(caseAt(helper, MAIN).getItem(slot).isEmpty(), "slot " + slot + " is still held by a case that cannot show it");
        }
        assertDropped(helper, Items.DIAMOND, DisplayCaseBlock.SLOTS - 1);

        helper.succeed();
    }

    private static String name(Block block) {
        return BuiltInRegistries.BLOCK.getKey(block).toString();
    }

    private static void assertDropped(GameTestHelper helper, net.minecraft.world.item.Item item, int count) {
        int dropped = helper.getEntities(EntityTypes.ITEM).stream().mapToInt(entity -> entity.getItem().is(item) ? entity.getItem().getCount() : 0).sum();
        helper.assertValueEqual(dropped, count, "dropped " + item);
    }
}
