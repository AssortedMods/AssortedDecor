package com.grim3212.assorted.paint.common.crafting;

import com.grim3212.assorted.lib.util.DyeHelper;
import com.grim3212.assorted.paint.common.items.PaintRollerItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * A roller and one wool or carpet, dyed like vanilla's dye recipes. NeoForge's copies of those take a roller already,
 * vanilla's on Fabric name the dye item, so without this rollers would only dye in the grid on NeoForge.
 */
public class RollerDyeingRecipe extends CustomRecipe {

    public static final RollerDyeingRecipe INSTANCE = new RollerDyeingRecipe();
    public static final MapCodec<RollerDyeingRecipe> MAP_CODEC = MapCodec.unit(INSTANCE);
    public static final StreamCodec<RegistryFriendlyByteBuf, RollerDyeingRecipe> STREAM_CODEC = StreamCodec.unit(INSTANCE);
    public static final RecipeSerializer<RollerDyeingRecipe> SERIALIZER = new RecipeSerializer<>(MAP_CODEC, STREAM_CODEC);

    private static final List<Map<DyeColor, Block>> DYEABLE = List.of(DyeHelper.WOOL_BY_DYE, DyeHelper.CARPET_BY_DYE);

    @Override
    public boolean matches(CraftingInput input, Level level) {
        return this.result(input) != null;
    }

    @Override
    public ItemStack assemble(CraftingInput input) {
        Block result = this.result(input);
        return result == null ? ItemStack.EMPTY : new ItemStack(result);
    }

    /** The block the grid dyes to, or null unless it holds one roller and one wool or carpet of another color. */
    @Nullable
    private Block result(CraftingInput input) {
        PaintRollerItem roller = null;
        ItemStack target = ItemStack.EMPTY;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (stack.getItem() instanceof PaintRollerItem found && roller == null) {
                roller = found;
            } else if (target.isEmpty()) {
                target = stack;
            } else {
                return null;
            }
        }
        if (roller == null || target.isEmpty()) {
            return null;
        }

        ItemStack dyeing = target;
        for (Map<DyeColor, Block> byDye : DYEABLE) {
            if (byDye.values().stream().anyMatch(block -> dyeing.is(block.asItem()))) {
                Block dyed = byDye.get(roller.getDyeColor());
                return dyeing.is(dyed.asItem()) ? null : dyed;
            }
        }
        return null;
    }

    @Override
    public RecipeSerializer<RollerDyeingRecipe> getSerializer() {
        return SERIALIZER;
    }
}
