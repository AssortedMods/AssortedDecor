package com.grim3212.assorted.displays.client;

import com.grim3212.assorted.displays.Constants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

/**
 * The client-only entry point: a second {@code @Mod} for the same mod id, constructed only on the
 * client.
 */
@Mod(value = Constants.MOD_ID, dist = Dist.CLIENT)
public class AssortedDisplaysNeoForgeClient {

    public AssortedDisplaysNeoForgeClient(IEventBus modBus, ModContainer modContainer) {
        DisplaysClient.init();
    }
}
