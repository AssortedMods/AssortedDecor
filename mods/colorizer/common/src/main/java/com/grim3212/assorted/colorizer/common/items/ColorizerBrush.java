package com.grim3212.assorted.colorizer.common.items;

import com.grim3212.assorted.colorizer.ColorizerCommonMod;
import com.grim3212.assorted.lib.core.item.ExtraPropertyItem;
import net.minecraft.world.item.ItemStack;

/**
 * Picks up a block and paints it onto colorizers. Its tooltip is a default
 * {@link ColorizerBrushInfo} component.
 */
public class ColorizerBrush extends ExtraPropertyItem {

    public ColorizerBrush(Properties properties) {
        super(properties.durability(16).component(ColorizerDataComponents.COLORIZER_BRUSH_INFO.get(), ColorizerBrushInfo.INSTANCE));
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return ColorizerCommonMod.COMMON_CONFIG.colorizerBrushCount.get();
    }
}
