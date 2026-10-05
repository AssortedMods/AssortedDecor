package com.grim3212.assorted.hangeables.gametest;

import com.grim3212.assorted.hangeables.common.entity.HangeablesEntityTypes;
import com.grim3212.assorted.hangeables.common.entity.FrameEntity;
import com.grim3212.assorted.hangeables.common.entity.WallpaperEntity;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.*;

/**
 * Decorations that are entities: frames and wallpaper.
 */
final class EntityDecorationTests {

    private EntityDecorationTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("frame_places_and_drops", EntityDecorationTests::framePlacesAndDrops);
        out.accept("wallpaper_places_and_drops", EntityDecorationTests::wallpaperPlacesAndDrops);
    }

    /**
     * A frame placed from its item becomes a real entity on the wall and gives the item back when
     * it is broken. Frames are {@code BlockAttachedEntity} subclasses, whose bounding box and
     * survival checks were restructured in the port.
     */
    private static void framePlacesAndDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wall = new BlockPos(4, 2, 4);
        BlockPos hanging = wall.south();

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack frameItem = new ItemStack(HangeablesItems.WOOD_FRAME.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, frameItem);
        frameItem.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.SOUTH)));

        helper.assertEntityPresent(HangeablesEntityTypes.WOOD_FRAME.get(), hanging);

        FrameEntity frame = helper.getEntities(HangeablesEntityTypes.WOOD_FRAME.get()).getFirst();
        helper.assertTrue(frame.survives(), "a frame on a stone wall did not think it could survive there");
        helper.hurt(frame, level.damageSources().generic(), 100.0F);

        helper.succeedWhen(() -> helper.assertItemEntityPresent(HangeablesItems.WOOD_FRAME.get(), hanging, 2.0D));
    }

    /**
     * The same for wallpaper, which overrides {@code survives} so several can share one wall, and
     * whose entity carries its pattern and dye in synched data.
     */
    private static void wallpaperPlacesAndDrops(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos wall = new BlockPos(4, 2, 4);
        BlockPos hanging = wall.south();

        helper.setBlock(wall, Blocks.STONE);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack wallpaperItem = new ItemStack(HangeablesItems.WALLPAPER.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, wallpaperItem);
        wallpaperItem.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.SOUTH)));

        helper.assertEntityPresent(HangeablesEntityTypes.WALLPAPER.get(), hanging);

        WallpaperEntity wallpaper = helper.getEntities(HangeablesEntityTypes.WALLPAPER.get()).getFirst();
        wallpaper.dyeWallpaper(DyeColor.RED);
        helper.assertValueEqual(wallpaper.getWallpaperColor()[0], (DyeColor.RED.getFireworkColor() & 0xFF0000) >> 16, "wallpaper did not take the dye");

        helper.hurt(wallpaper, level.damageSources().generic(), 100.0F);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(HangeablesItems.WALLPAPER.get(), hanging, 2.0D));
    }
}
