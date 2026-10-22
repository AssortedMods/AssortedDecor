package com.grim3212.assorted.buildingblocks.gametest;

import com.grim3212.assorted.buildingblocks.Constants;
import com.grim3212.assorted.lib.test.ClientChecks;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * How the building blocks and the lumber mill are drawn, which a headless server cannot see. Run with
 * {@code ./gradlew :fabric:runClientGameTest}; it exits non-zero on a failure.
 */
public class BuildingBlocksClientGameTests implements FabricClientGameTest {

    @Override
    public void runTest(ClientGameTestContext context) {
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            ClientChecks.assertDrawsRight(context, Constants.MOD_ID);
            BuildingBlockClientTests.run(context, world);
        }
    }
}
