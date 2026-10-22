package com.grim3212.assorted.hangeables.common.items;

import net.minecraft.core.component.DataComponents;
import com.grim3212.assorted.hangeables.common.blocks.HangeablesBlocks;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.NeonSignBlockEntity;
import com.grim3212.assorted.hangeables.common.network.NeonOpenPacket;
import com.grim3212.assorted.lib.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.Nullable;

public class NeonSignItem extends StandingAndWallBlockItem {

    public NeonSignItem(Item.Properties props) {
        super(HangeablesBlocks.NEON_SIGN.get(), HangeablesBlocks.NEON_SIGN_WALL.get(), Direction.DOWN, props);
    }

    /**
     * Whether the block entity data on {@code stack} may be applied: only by an operator, as for a
     * vanilla sign. Vanilla's op-only type set cannot take a modded type, so the check lives here.
     */
    public static boolean mayApplyBlockEntityData(@Nullable Player player, ItemStack stack) {
        return !stack.has(DataComponents.BLOCK_ENTITY_DATA) || player != null && player.canUseGameMasterBlocks();
    }

    /**
     * Vanilla applies a stack's block entity data inside place now, with no hook, so data a player may not apply
     * is held back across it. A sign that took no data opens its editor, as it did before.
     */
    @Override
    public InteractionResult place(BlockPlaceContext context) {
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        boolean applies = stack.has(DataComponents.BLOCK_ENTITY_DATA) && mayApplyBlockEntityData(player, stack);
        TypedEntityData<BlockEntityType<?>> withheld = applies ? null : stack.remove(DataComponents.BLOCK_ENTITY_DATA);
        InteractionResult result = super.place(context);
        if (withheld != null && !stack.isEmpty()) {
            stack.set(DataComponents.BLOCK_ENTITY_DATA, withheld);
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        if (result.consumesAction() && !level.isClientSide() && !applies && player != null && level.getBlockEntity(pos) instanceof NeonSignBlockEntity sign) {
            sign.setOwner(player);
            Services.NETWORK.sendTo(player, new NeonOpenPacket(pos));
        }

        return result;
    }
}
