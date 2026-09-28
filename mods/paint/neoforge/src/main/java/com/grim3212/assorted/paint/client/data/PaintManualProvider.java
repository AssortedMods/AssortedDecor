package com.grim3212.assorted.paint.client.data;

import com.grim3212.assorted.paint.Constants;
import com.grim3212.assorted.paint.common.items.PaintItems;
import com.grim3212.assorted.lib.data.LibManualProvider;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * This part's chapter of the Assorted Decor section, which every part shares; the explicit chapter orders keep the
 * section's order whichever parts are installed.
 */
public class PaintManualProvider extends LibManualProvider {

    public PaintManualProvider(PackOutput output) {
        super(output, Constants.MOD_ID, Constants.FAMILY_ID);
    }

    @Override
    protected void addChapters() {
        List<Item> rollers = new ArrayList<>();
        rollers.add(PaintItems.PAINT_ROLLER.get());
        PaintItems.PAINT_ROLLER_COLORS.values().stream().map(IRegistryObject::get).forEach(rollers::add);

        ChapterBuilder paint = this.chapter("paint", 11);
        paint.recipesById("rollers", recipeId(PaintItems.PAINT_ROLLER.get()), recipeId("white_concrete_powder_paint_roll"), recipeId("white_concrete_paint_roll")).every(60)
                .opens(rollers.toArray(Item[]::new));
        // These draw recipes that load only with the mod whose blocks they paint.
        paint.recipesById("roadways", recipeId("roadway_red"), recipeId("roadway_white"), recipeId("roadway_yellow")).every(60)
                .when(modLoaded("assortedroads"));
        paint.recipesById("fluro", recipeId("fluro_red_paint_roll"), recipeId("fluro_blue_paint_roll"), recipeId("fluro_green_paint_roll")).every(60)
                .when(modLoaded("assortedlights"));
        paint.recipesById("siding", recipeId("siding_horizontal_white"), recipeId("siding_vertical_white")).every(60)
                .when(modLoaded("assortedbuildingblocks"));
    }
}
