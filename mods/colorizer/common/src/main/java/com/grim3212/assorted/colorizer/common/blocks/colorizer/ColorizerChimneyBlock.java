package com.grim3212.assorted.colorizer.common.blocks.colorizer;

import com.grim3212.assorted.colorizer.api.util.ColorizerUtil;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class ColorizerChimneyBlock extends ColorizerFullCubeBlock {

    public ColorizerChimneyBlock(Properties props) {
    	super(props);
    }

    @Override
    public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, RandomSource rand) {
        if (worldIn.getBlockState(pos.below()).getBlock() == Blocks.FIRE) {
            if (worldIn.getRandom().nextInt(2) == 0) {
                int smokeheight = 1;
                while (worldIn.getBlockState(pos.above(smokeheight)).getBlock() == ColorizerBlocks.COLORIZER_CHIMNEY.get()) {
                    smokeheight++;
                }

                ColorizerUtil.produceSmoke(worldIn, pos.above(smokeheight), 0.5D, 0.0D, 0.5D, 1, true);
            }
        }
    }
}
