package com.grim3212.assorted.displays.common.inventory;

import com.grim3212.assorted.displays.Constants;
import com.grim3212.assorted.lib.platform.Services;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import com.grim3212.assorted.lib.registry.RegistryProvider;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;

public class DisplaysContainerTypes {
    public static final RegistryProvider<MenuType<?>> MENU_TYPES = RegistryProvider.create(Registries.MENU, Constants.MOD_ID);

    public static final IRegistryObject<MenuType<CageContainer>> CAGE = MENU_TYPES.register("cage", () -> Services.PLATFORM.createMenuType(CageContainer::new));

    public static void init() {
    }
}
