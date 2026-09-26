package com.grim3212.assorted.lights;

import com.grim3212.assorted.lights.client.LightsClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedLightsFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        LightsClient.init();
    }
}
