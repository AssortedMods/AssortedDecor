package com.grim3212.assorted.displays.gametest;

import com.grim3212.assorted.displays.common.blocks.DisplaysBlocks;
import com.grim3212.assorted.displays.client.blockentity.DisplayCaseBlockEntityRenderer;
import com.grim3212.assorted.displays.common.blocks.MuseumDisplayCaseBlock;
import com.grim3212.assorted.displays.common.blocks.blockentity.CageBlockEntity;
import com.grim3212.assorted.displays.common.blocks.blockentity.DisplayCaseBlockEntity;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

/**
 * That a caged mob survives render state extraction and that both display cases draw what they hold: what a
 * headless server cannot see. Run with {@code ./gradlew :displays:fabric:runClientGameTest}; it exits non-zero on a failure.
 */
public class DisplaysClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        // Inside a world: an ItemStack cannot be made on the title screen, because an item's default
        // components are only bound once a world's registries have loaded.
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            // A caged mob is built on the client and drawn without ever being added to the level, so
            // it is never given an entity id there - Level#getNextEntityId answers 0 off the server.
            // Extracting a living entity's render state reads the id for its head slot whether or not
            // anything is worn, and Entity#getId throws on 0, so an unmarked mob crashes the client
            // the moment the cage is stocked. Only a client can see this: on a server the mob is
            // handed a real id.
            BlockPos cagePos = world.getServer().computeOnServer(server -> {
                ServerLevel level = server.overworld();
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(4);
                level.setBlockAndUpdate(at, DisplaysBlocks.CAGE.get().defaultBlockState());
                ((CageBlockEntity) level.getBlockEntity(at)).getItemStackStorageHandler()
                        .setStackInSlot(0, new ItemStack(Items.PIG_SPAWN_EGG));
                return at;
            });
            context.waitFor(client -> client.level != null
                    && client.level.getBlockEntity(cagePos) instanceof CageBlockEntity cage
                    && cage.getItemStackStorageHandler().getStackInSlot(0).is(Items.PIG_SPAWN_EGG));

            context.runOnClient(client -> {
                Entity caged = ((CageBlockEntity) client.level.getBlockEntity(cagePos)).getCachedEntity();
                if (caged == null) {
                    throw new AssertionError("a stocked cage built no mob on the client");
                }
                // The call the cage's renderer makes; it threw ReportedException before the mob was
                // marked as a display entity.
                client.getEntityRenderDispatcher().extractEntity(caged, 0.0F);
            });

            displayCases(context, world);
        }
    }

    /**
     * Both display cases, drawn. Their renderer resolves an item model a slot and, for the museum
     * one, reads the light a block above the block entity - none of which a headless server runs.
     */
    private static void displayCases(ClientGameTestContext context, TestSingleplayerContext world) {
        BlockPos plain = world.getServer().computeOnServer(server -> {
            ServerLevel level = server.overworld();
            BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(8);
            level.setBlockAndUpdate(at, DisplaysBlocks.GOLD_DISPLAY_CASE.get().defaultBlockState());
            ((DisplayCaseBlockEntity) level.getBlockEntity(at)).setItem(4, new ItemStack(Items.DIAMOND));

            BlockPos museum = at.above(2);
            level.setBlockAndUpdate(museum, DisplaysBlocks.MUSEUM_DISPLAY_CASE.get().defaultBlockState().setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.LOWER));
            level.setBlockAndUpdate(museum.above(), DisplaysBlocks.MUSEUM_DISPLAY_CASE.get().defaultBlockState().setValue(MuseumDisplayCaseBlock.HALF, DoubleBlockHalf.UPPER));
            DisplayCaseBlockEntity displayCase = (DisplayCaseBlockEntity) level.getBlockEntity(museum);
            displayCase.setItem(0, new ItemStack(Items.EMERALD));
            displayCase.setCustomName(Component.literal("Tyrannosaurus"));
            return at;
        });
        BlockPos museum = plain.above(2);

        context.waitFor(client -> client.level != null
                && client.level.getBlockEntity(plain) instanceof DisplayCaseBlockEntity showing && showing.getItem(4).is(Items.DIAMOND)
                && client.level.getBlockEntity(museum) instanceof DisplayCaseBlockEntity named && named.getCustomName() != null);

        context.runOnClient(client -> {
            assertDrawn(client, plain, 4, false);
            assertDrawn(client, museum, 0, true);
        });
    }

    /** Extracts one case's render state exactly as the renderer is asked to every frame. */
    private static void assertDrawn(Minecraft client, BlockPos pos, int slot, boolean museum) {
        DisplayCaseBlockEntity displayCase = (DisplayCaseBlockEntity) client.level.getBlockEntity(pos);
        DisplayCaseBlockEntityRenderer.DisplayCaseRenderState state = client.getBlockEntityRenderDispatcher().tryExtractRenderState(displayCase, 0.0F, null, false);

        if (state == null) {
            throw new AssertionError("a stocked display case at " + pos + " extracted no render state");
        }
        if (state.items[slot].isEmpty()) {
            throw new AssertionError("a display case at " + pos + " draws nothing in slot " + slot);
        }
        if (museum != (state.placard != null)) {
            throw new AssertionError("a display case at " + pos + " drew its placard: " + (state.placard != null));
        }
    }
}
