package com.grim3212.assorted.colorizer;

import com.grim3212.assorted.colorizer.client.ColorizerClient;
import net.fabricmc.api.ClientModInitializer;

public class AssortedColorizerFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ColorizerClient.init();
    }
}
