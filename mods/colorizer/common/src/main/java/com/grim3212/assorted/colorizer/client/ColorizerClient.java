package com.grim3212.assorted.colorizer.client;

import com.grim3212.assorted.colorizer.client.color.ColorizerItemTintSource;
import com.grim3212.assorted.colorizer.client.model.ColorizerItemModel;
import com.grim3212.assorted.colorizer.client.model.ColorizerUnbakedModel;
import com.grim3212.assorted.colorizer.client.model.obj.ColorizerObjModel;
import com.grim3212.assorted.colorizer.common.blocks.ColorizerBlocks;
import com.grim3212.assorted.colorizer.common.blocks.blockentity.ColorizerBlockEntity;
import com.grim3212.assorted.lib.platform.ClientServices;
import com.grim3212.assorted.lib.registry.IRegistryObject;
import net.minecraft.client.color.block.BlockTintSource;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.stream.Collectors;

public class ColorizerClient {

    public static void init() {
        ClientServices.CLIENT.registerModelLoader(ColorizerUnbakedModel.LOADER_NAME, ColorizerUnbakedModel.Loader.INSTANCE);
        ClientServices.CLIENT.registerModelLoader(ColorizerObjModel.LOADER_NAME, ColorizerObjModel.Loader.INSTANCE);

        // The item half of the colorizer. A model json loader only produces geometry, so it cannot
        // vary an item with the block the stack has stored; this is the type that can.
        ClientServices.CLIENT.registerItemModelType(ColorizerItemModel.ID, ColorizerItemModel.Unbaked.MAP_CODEC);

        registerBlockColors();
        // The generated item models name this in their "tints" lists.
        ClientServices.CLIENT.registerItemTintSource(ColorizerItemTintSource.ID, ColorizerItemTintSource.MAP_CODEC);
    }

    private static void registerBlockColors() {
        // BlockColor became BlockTintSource: colour(state) answers the in-hand colour and
        // colorInWorld(state, level, pos) the placed one, and the tint layer index is the position of
        // the source in the block's list rather than an argument. Colours are ARGB now, so an opaque
        // white is -1 rather than 0xFFFFFF.
        ClientServices.CLIENT.registerBlockColor(new BlockTintSource() {
            @Override
            public int color(BlockState state) {
                return -1;
            }

            @Override
            public int colorInWorld(BlockState state, BlockAndTintGetter worldIn, BlockPos pos) {
                BlockEntity te = worldIn.getBlockEntity(pos);
                if (te instanceof ColorizerBlockEntity colorizer) {
                    BlockState stored = colorizer.getStoredBlockState();
                    BlockTintSource source = ClientServices.CLIENT.getBlockColors().getTintSource(stored, 0);
                    if (source != null) {
                        return source.colorInWorld(stored, worldIn, pos);
                    }
                }
                return -1;
            }
        }, () -> ColorizerBlocks.colorizerBlocks().stream().map(IRegistryObject::get).collect(Collectors.toList()));
    }
}
