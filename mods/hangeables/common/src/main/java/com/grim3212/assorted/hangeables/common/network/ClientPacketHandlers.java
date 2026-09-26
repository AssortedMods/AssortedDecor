package com.grim3212.assorted.hangeables.common.network;

import com.grim3212.assorted.hangeables.client.screen.NeonSignScreen;
import com.grim3212.assorted.hangeables.common.blocks.blockentity.NeonSignBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ClientPacketHandlers {
    public static void openNeonSignScreen(BlockPos pos) {
        BlockEntity tileentity = Minecraft.getInstance().player.level().getBlockEntity(pos);

        // Make sure TileEntity exists
        if (!(tileentity instanceof NeonSignBlockEntity)) {
            tileentity = new NeonSignBlockEntity(pos, tileentity.getBlockState());
            tileentity.setLevel(Minecraft.getInstance().player.level());
        }

        Minecraft.getInstance().gui.setScreen(new NeonSignScreen((NeonSignBlockEntity) tileentity));
    }
}
