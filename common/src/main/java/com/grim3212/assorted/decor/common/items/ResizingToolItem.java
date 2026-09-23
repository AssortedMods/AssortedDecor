package com.grim3212.assorted.decor.common.items;

import com.grim3212.assorted.decor.common.blocks.DisplayCaseBlock;
import com.grim3212.assorted.lib.core.item.ItemDescription;
import com.grim3212.assorted.lib.core.item.LibDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/** Cycles a display case between one item, four and nine. Cases are placed showing one. */
public class ResizingToolItem extends Item {

    private static final String DESCRIPTION_KEY = "tooltip.resizing_tool";

    public ResizingToolItem(Properties props) {
        super(props.component(LibDataComponents.DESCRIPTION.get(),
                new ItemDescription(Component.translatable(DESCRIPTION_KEY).withStyle(ChatFormatting.GRAY))));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof DisplayCaseBlock displayCase)) {
            return InteractionResult.PASS;
        }

        if (!level.isClientSide()) {
            BlockState resized = displayCase.resize(state, level, pos);
            level.playSound(null, pos, SoundEvents.ITEM_FRAME_ROTATE_ITEM, SoundSource.BLOCKS, 1.0F, 0.8F + 0.2F * resized.getValue(DisplayCaseBlock.SIZE));

            Player player = context.getPlayer();
            level.gameEvent(player, GameEvent.BLOCK_CHANGE, pos);
        }

        return InteractionResult.SUCCESS;
    }
}
