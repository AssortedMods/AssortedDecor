package com.grim3212.assorted.colorizer.gametest;

import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.colorizer.ColorizerFireplaceBaseBlock;
import com.grim3212.assorted.colorizer.common.items.ColorizerItems;
import com.grim3212.assorted.lib.test.TestSupport;
import com.grim3212.assorted.lib.util.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.colorizer.gametest.ColorizerTestSupport.MAIN;

/** The brush running out after its uses, and a lit fireplace put out by a punch, as the site describes. */
final class BrushAndFireTests {

    private BrushAndFireTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("colorizer_brush_turns_empty_after_its_last_use", BrushAndFireTests::colorizerBrushTurnsEmptyAfterItsLastUse);
        out.accept("lit_fireplace_goes_out_when_punched", BrushAndFireTests::litFireplaceGoesOutWhenPunched);
    }

    /** One use left: painting spends it, and the player is handed an empty brush rather than a broken one. */
    private static void colorizerBrushTurnsEmptyAfterItsLastUse(GameTestHelper helper) {
        helper.setBlock(MAIN, ColorizerBlocks.COLORIZER.get());
        ItemStack brush = new ItemStack(ColorizerItems.COLORIZER_BRUSH.get());
        NBTHelper.putTag(brush, "stored_state", NbtUtils.writeBlockState(Blocks.GOLD_BLOCK.defaultBlockState()));
        brush.setDamageValue(brush.getMaxDamage() - 1);
        ServerPlayer player = TestSupport.survivalPlayer(helper, brush);

        TestSupport.rightClick(player, helper.getLevel(), brush, helper.absolutePos(MAIN));
        helper.assertTrue(ColorizerBlocks.COLORIZER.get().getStoredState(helper.getLevel(), helper.absolutePos(MAIN)).is(Blocks.GOLD_BLOCK), "the last use did not paint");
        ItemStack held = player.getItemInHand(InteractionHand.MAIN_HAND);
        helper.assertTrue(held.is(ColorizerItems.COLORIZER_BRUSH.get()) && !NBTHelper.hasTag(held, "stored_state"),
                "after its last use the brush became " + held + ", not an empty brush");
        helper.succeed();
    }

    private static void litFireplaceGoesOutWhenPunched(GameTestHelper helper) {
        BlockPos at = MAIN;
        helper.setBlock(at, ColorizerBlocks.COLORIZER_FIREPLACE.get().defaultBlockState().setValue(ColorizerFireplaceBaseBlock.ACTIVE, true));
        ServerPlayer player = TestSupport.survivalPlayer(helper);
        helper.getBlockState(at).attack(helper.getLevel(), helper.absolutePos(at), player);
        helper.assertBlockProperty(at, ColorizerFireplaceBaseBlock.ACTIVE, false);
        helper.succeed();
    }
}
