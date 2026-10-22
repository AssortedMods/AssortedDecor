package com.grim3212.assorted.hangeables.gametest;

import com.grim3212.assorted.hangeables.common.entity.FrameEntity;
import com.grim3212.assorted.hangeables.common.entity.HangeablesEntityTypes;
import com.grim3212.assorted.hangeables.common.entity.WallpaperEntity;
import com.grim3212.assorted.hangeables.common.items.HangeablesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.lib.test.TestSupport.hitSide;

/** Wallpaper and frames as a player decorates with them: dyed, cycled, matched to their neighbours, and frames outlasting their wall. */
final class HangingDecorTests {

    private static final BlockPos WALL = new BlockPos(4, 2, 4);

    private HangingDecorTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("wallpaper_dyes_and_cycles_with_a_right_click", HangingDecorTests::wallpaperDyesAndCyclesWithARightClick);
        out.accept("wallpaper_takes_the_dye_of_its_neighbour", HangingDecorTests::wallpaperTakesTheDyeOfItsNeighbour);
        out.accept("frame_dyes_with_a_right_click", HangingDecorTests::frameDyesWithARightClick);
        out.accept("frame_stays_when_its_wall_is_removed", HangingDecorTests::frameStaysWhenItsWallIsRemoved);
    }

    private static void wallpaperDyesAndCyclesWithARightClick(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        WallpaperEntity wallpaper = hang(helper, player, HangeablesItems.WALLPAPER.get(), WALL, WallpaperEntity.class);

        interact(player, wallpaper, new ItemStack(Items.DYE.pick(DyeColor.RED)));
        helper.assertValueEqual(wallpaper.getWallpaperColor()[0], (DyeColor.RED.getFireworkColor() & 0xFF0000) >> 16, "red in the wallpaper after a red dye");

        interact(player, wallpaper, new ItemStack(HangeablesItems.WALLPAPER.get()));
        helper.assertValueEqual(wallpaper.getWallpaperID(), 1, "the wallpaper's pattern after a right click with wallpaper");
        helper.succeed();
    }

    private static void wallpaperTakesTheDyeOfItsNeighbour(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.setBlock(WALL.east(), Blocks.STONE);
        WallpaperEntity first = hang(helper, player, HangeablesItems.WALLPAPER.get(), WALL, WallpaperEntity.class);
        first.dyeWallpaper(DyeColor.BLUE);

        hang(helper, player, HangeablesItems.WALLPAPER.get(), WALL.east(), WallpaperEntity.class);
        WallpaperEntity second = helper.getEntities(HangeablesEntityTypes.WALLPAPER.get()).stream().filter(entity -> entity != first).findFirst()
                .orElseThrow(() -> helper.assertionException("the second wallpaper did not hang"));
        helper.assertTrue(Arrays.equals(second.getWallpaperColor(), first.getWallpaperColor()),
                "wallpaper hung beside blue wallpaper came out " + Arrays.toString(second.getWallpaperColor()));
        helper.succeed();
    }

    private static void frameDyesWithARightClick(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        FrameEntity frame = hang(helper, player, HangeablesItems.WOOD_FRAME.get(), WALL, FrameEntity.class);
        interact(player, frame, new ItemStack(Items.DYE.pick(DyeColor.LIME)));
        helper.assertValueEqual(frame.getFrameColor()[1], (DyeColor.LIME.getFireworkColor() & 0xFF00) >> 8, "green in the frame after a lime dye");
        helper.succeed();
    }

    /** The site's promise that frames are stronger than wallpaper; a hanging entity checks its wall every hundred ticks. */
    private static void frameStaysWhenItsWallIsRemoved(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        hang(helper, player, HangeablesItems.IRON_FRAME.get(), WALL, FrameEntity.class);
        helper.setBlock(WALL, Blocks.AIR);
        helper.runAfterDelay(120, () -> {
            helper.assertTrue(!helper.getEntities(HangeablesEntityTypes.IRON_FRAME.get()).isEmpty(), "the iron frame fell once the block behind it was gone");
            helper.succeed();
        });
    }

    /** Hangs one on the south face of a stone block at {@code wall}, as a player would. */
    private static <T extends Entity> T hang(GameTestHelper helper, Player player, Item item, BlockPos wall, Class<T> type) {
        helper.setBlock(wall, Blocks.STONE);
        ItemStack stack = new ItemStack(item);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hitSide(helper.absolutePos(wall), Direction.SOUTH)));
        List<T> hung = helper.getLevel().getEntitiesOfClass(type, new AABB(helper.absolutePos(wall.south())).inflate(0.6D));
        helper.assertFalse(hung.isEmpty(), BuiltInRegistries.ITEM.getKey(item) + " did not hang on the wall");
        return hung.getLast();
    }

    private static void interact(Player player, Entity entity, ItemStack held) {
        player.setItemInHand(InteractionHand.MAIN_HAND, held);
        entity.interact(player, InteractionHand.MAIN_HAND, Vec3.ZERO);
    }
}
