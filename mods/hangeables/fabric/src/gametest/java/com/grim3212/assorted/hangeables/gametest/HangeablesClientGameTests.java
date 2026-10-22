package com.grim3212.assorted.hangeables.gametest;

import com.grim3212.assorted.hangeables.Constants;
import com.grim3212.assorted.lib.test.ClientChecks;
import com.grim3212.assorted.hangeables.client.screen.NeonSignScreen;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.NeonSignBlockEntity;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import org.lwjgl.sdl.SDLKeyboard;

/** The neon sign typed into as a player would: 26.3 sends no characters to a screen that has not asked for them. */
public class HangeablesClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            ClientChecks.assertDrawsRight(context, Constants.MOD_ID);
            BlockPos pos = world.getServer().computeOnServer(server -> {
                BlockPos at = server.getPlayerList().getPlayers().get(0).blockPosition().above(2);
                server.overworld().setBlockAndUpdate(at, HangeablesBlocks.NEON_SIGN.get().defaultBlockState());
                return at;
            });
            context.waitFor(client -> client.level != null && client.level.getBlockEntity(pos) instanceof NeonSignBlockEntity);

            context.setScreen(() -> new NeonSignScreen((NeonSignBlockEntity) Minecraft.getInstance().level.getBlockEntity(pos)));
            context.waitForScreen(NeonSignScreen.class);
            // Test typing skips SDL, which sends a real keyboard's characters only while text input is on.
            if (!context.computeOnClient(client -> SDLKeyboard.SDL_TextInputActive(client.getWindow().handle()))) {
                throw new AssertionError("the neon sign screen opened without turning on text input, so a player cannot type in it");
            }
            context.getInput().typeChars("Hi");
            context.waitTick();

            String line = context.computeOnClient(client -> ((NeonSignBlockEntity) client.level.getBlockEntity(pos)).getText(0).getString());
            if (!line.equals("Hi")) {
                throw new AssertionError("typing Hi into a neon sign left its first line as \"" + line + "\"");
            }
            context.setScreen(() -> null);
        }
    }
}
