package com.grim3212.assorted.displays.gametest;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.lib.core.inventory.IMenuDataProvider;
import com.grim3212.assorted.lib.core.inventory.MenuData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static com.grim3212.assorted.displays.gametest.DisplaysTestSupport.MAIN;

/**
 * The cage: the mob it builds from what is caged, and its menu.
 */
final class CageTests {

    private CageTests() {
    }

    static void register(BiConsumer<String, Consumer<GameTestHelper>> out) {
        out.accept("cage_holds_mob_and_drops_it", CageTests::cageHoldsMobAndDropsIt);
        out.accept("cage_menu_rebuilds_on_the_client", CageTests::cageMenuRebuildsOnTheClient);
    }

    /**
     * A cage builds its display mob from the caged stack, reports it to a comparator, and gives the
     * stack back when broken. The drop moved to {@code CageBlockEntity#preRemoveSideEffects} during
     * the port because the block entity is gone by the time the block's removal hook runs.
     */
    private static void cageHoldsMobAndDropsIt(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(MAIN);

        helper.setBlock(MAIN, DisplaysBlocks.CAGE.get());
        CageBlockEntity cage = helper.getBlockEntity(MAIN, CageBlockEntity.class);

        ItemStack egg = new ItemStack(Items.PIG_SPAWN_EGG);
        helper.assertTrue(CageBlockEntity.isValidCage(egg) != null, "cage rejected a spawn egg");
        cage.getItemStackStorageHandler().setStackInSlot(0, egg);

        Entity caged = cage.getCachedEntity();
        helper.assertTrue(caged != null && caged.getType() == EntityTypes.PIG, "cage did not build a pig out of the spawn egg");
        // The display mob is never added to a level, so it has to carry a spawner's display id: on a
        // client level every entity is built with id 0, and Entity#getId throws on that. Rendering
        // reaches it for every living entity, so an unmarked mob crashes the client (see
        // CageBlockEntity#storeEntity).
        helper.assertValueEqual(caged.getId(), -1, "the caged mob's entity id, which marks it as a display entity,");
        helper.assertTrue(level.getBlockState(pos).getAnalogOutputSignal(level, pos, Direction.UP) > 0,
                "a stocked cage gave a comparator nothing to read");

        helper.destroyBlock(MAIN);
        helper.succeedWhen(() -> helper.assertItemEntityPresent(Items.PIG_SPAWN_EGG, MAIN, 3.0D));
    }

    /**
     * The cage's client menu needs nothing from the server, so it opens as a vanilla menu: the
     * client builds it from the menu type alone, and it matches what the server opened.
     */
    private static void cageMenuRebuildsOnTheClient(GameTestHelper helper) {
        helper.setBlock(MAIN, DisplaysBlocks.CAGE.get());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        MenuProvider provider = helper.getBlockState(MAIN).getMenuProvider(helper.getLevel(), helper.absolutePos(MAIN));
        if (provider == null) {
            helper.fail("the cage has no menu provider");
            return;
        }
        helper.assertFalse(provider instanceof IMenuDataProvider<?>, "the cage provides menu data it does not need");

        AbstractContainerMenu server = provider.createMenu(1, player.getInventory(), player);
        helper.assertFalse(MenuData.hasData(server.getType()), "the cage opens a menu type that expects data");

        AbstractContainerMenu client = server.getType().create(1, player.getInventory());
        helper.assertTrue(client.getClass() == server.getClass(), "the cage built a " + client.getClass().getSimpleName() + " on the client");
        helper.assertValueEqual(client.slots.size(), server.slots.size(), "cage client menu slots");

        helper.succeed();
    }
}
