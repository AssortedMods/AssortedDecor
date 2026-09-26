package com.grim3212.assorted.colorizer.common.items;

import com.grim3212.assorted.colorizer.Constants;
import com.grim3212.assorted.colorizer.Family;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;

public class ColorizerDataComponents {

    public static final RegistryProvider<DataComponentType<?>> DATA_COMPONENTS = RegistryProvider.create(Registries.DATA_COMPONENT_TYPE, Constants.MOD_ID).aliasFrom(Family.ID);

    public static final IRegistryObject<DataComponentType<ColorizerBrushInfo>> COLORIZER_BRUSH_INFO = DATA_COMPONENTS.register("colorizer_brush_info",
            () -> new DataComponentType.Builder<ColorizerBrushInfo>().persistent(ColorizerBrushInfo.CODEC).networkSynchronized(ColorizerBrushInfo.STREAM_CODEC).build());

    // Runs before ColorizerItems, whose colorizer brush carries COLORIZER_BRUSH_INFO as a default component.
    public static void init() {
        Services.PLATFORM.showComponentTooltip(COLORIZER_BRUSH_INFO);
    }
}
