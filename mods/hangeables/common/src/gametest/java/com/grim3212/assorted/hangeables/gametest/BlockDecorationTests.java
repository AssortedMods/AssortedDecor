package com.grim3212.assorted.hangeables.gametest;

import net.minecraft.server.level.ServerPlayer;
import com.grim3212.assorted.hangeables.common.items.NeonSignItem;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.nbt.NbtOps;
import net.minecraft.core.component.DataComponents;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.HangeablesBlockEntityTypes;
import com.grim3212.assorted.hangeables.api.util.DateHandler;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.CalendarBlockEntity;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;
import static com.grim3212.assorted.hangeables.gametest.HangeablesTestSupport.*;

/**
 * Blocks that hang on a wall or stand like a sign: the calendar and the neon sign.
 */
final class BlockDecorationTests {

    private BlockDecorationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("calendar_shows_the_date", BlockDecorationTests::calendarShowsTheDate);
        out.accept("neon_sign_text_survives_reload", BlockDecorationTests::neonSignTextSurvivesReload);
        out.accept("neon_sign_item_data_needs_an_operator", BlockDecorationTests::neonSignItemDataNeedsAnOperator);
    }

    /**
     * The calendar hangs on a wall with its block entity, and {@link DateHandler#calculateDate} is
     * right, including the ordinal suffixes of the teens, which a last-digit switch gets wrong.
     */
    private static void calendarShowsTheDate(GameTestHelper helper) {
        BlockPos wall = new BlockPos(4, 2, 5);
        BlockPos hanging = new BlockPos(4, 2, 4);

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(HangeablesBlocks.CALENDAR.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.NORTH)));

        helper.assertBlockPresent(HangeablesBlocks.CALENDAR.get(), hanging);
        helper.assertBlockProperty(hanging, HorizontalDirectionalBlock.FACING, Direction.NORTH);
        helper.getBlockEntity(hanging, CalendarBlockEntity.class);

        // Day one is the first day of the first year; a day is 24000 ticks and the year is 365 days.
        helper.assertValueEqual(DateHandler.calculateDate(0L, 0), "1st January, Year 1", "the date on day one");
        helper.assertValueEqual(DateHandler.calculateDate(0L, 1), "Year 1,Jan 1,Mon", "the short date on day one");
        helper.assertValueEqual(DateHandler.calculateDate(10L * 24000L, 0), "11th January, Year 1", "the date on day eleven");
        helper.assertValueEqual(DateHandler.calculateDate(20L * 24000L, 0), "21st January, Year 1", "the date on day twenty one");
        helper.assertValueEqual(DateHandler.calculateDate(31L * 24000L, 0), "1st February, Year 1", "the date the month rolls over");
        helper.assertValueEqual(DateHandler.calculateDate(365L * 24000L, 0), "1st January, Year 2", "the date the year rolls over");

        for (Map.Entry<Integer, String> expected : Map.of(1, "st", 2, "nd", 3, "rd", 4, "th", 11, "th", 12, "th", 13, "th", 21, "st", 22, "nd", 23, "rd").entrySet()) {
            helper.assertValueEqual(DateHandler.ordinalNo(expected.getKey()), expected.getValue(), "the suffix on " + expected.getKey());
        }

        helper.succeed();
    }

    /**
     * Neon sign text and mode survive a save and reload. {@code loadAdditional} resolves each line
     * against a command source, which has to cope with a block entity that has no level yet.
     */
    private static void neonSignTextSurvivesReload(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, HangeablesBlocks.NEON_SIGN.get());
        NeonSignBlockEntity sign = helper.getBlockEntity(MAIN, NeonSignBlockEntity.class);

        sign.mode = 2;
        for (int i = 0; i < 4; i++) {
            sign.setText(i, Component.literal("line " + (i + 1)));
        }

        CompoundTag saved = sign.saveWithFullMetadata(level.registryAccess());
        BlockEntity reloaded = BlockEntity.loadStatic(pos, sign.getBlockState(), saved, level.registryAccess());

        helper.assertTrue(reloaded instanceof NeonSignBlockEntity, "a saved neon sign did not load back as a neon sign");
        NeonSignBlockEntity loaded = (NeonSignBlockEntity) reloaded;

        helper.assertValueEqual(loaded.mode, 2, "neon sign mode after a save/load round trip");
        for (int i = 0; i < 4; i++) {
            helper.assertValueEqual(loaded.getText(i).getString(), "line " + (i + 1), "neon sign line " + (i + 1) + " after a save/load round trip");
        }

        helper.succeed();
    }

    /**
     * Block entity data on a neon sign item applies only for an operator, as for a vanilla sign.
     * The real placement by a non-operator runs on Fabric only, because NeoForge will not send the
     * editor packet to a test player; the placement code is common, so that covers both.
     */
    private static void neonSignItemDataNeedsAnOperator(GameTestHelper helper) {
        CompoundTag data = new CompoundTag();
        data.put("Text1", ComponentSerialization.CODEC.encodeStart(NbtOps.INSTANCE, Component.literal("from an item")).getOrThrow());
        ItemStack sign = new ItemStack(HangeablesItems.NEON_SIGN.get());
        sign.set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.<BlockEntityType<?>>of(HangeablesBlockEntityTypes.NEON_SIGN.get(), data));

        Player mock = helper.makeMockPlayer(GameType.CREATIVE);
        helper.assertFalse(mock.canUseGameMasterBlocks(), "the test player is an operator, so the check is not exercised");
        helper.assertFalse(NeonSignItem.mayApplyBlockEntityData(mock, sign), "a non-operator may apply neon sign data from an item");
        helper.assertTrue(NeonSignItem.mayApplyBlockEntityData(mock, new ItemStack(HangeablesItems.NEON_SIGN.get())), "a neon sign item with no data counts as op-only");

        if (!onNeoForge()) {
            BlockPos floor = new BlockPos(4, 1, 4);
            helper.setBlock(floor, Blocks.STONE);
            ServerPlayer player = survivalPlayer(helper, sign);
            helper.assertFalse(player.canUseGameMasterBlocks(), "the test player is an operator, so the check is not exercised");
            rightClick(player, helper.getLevel(), sign, helper.absolutePos(floor));

            NeonSignBlockEntity placed = helper.getBlockEntity(floor.above(), NeonSignBlockEntity.class);
            helper.assertTrue(placed.getText(0).getString().isEmpty(), "a non-operator set neon sign text from an item: " + placed.getText(0).getString());
        }
        helper.succeed();
    }
}
