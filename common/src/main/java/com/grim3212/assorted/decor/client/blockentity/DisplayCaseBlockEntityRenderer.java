package com.grim3212.assorted.decor.client.blockentity;

import com.grim3212.assorted.decor.common.blocks.DisplayCaseBlock;
import com.grim3212.assorted.decor.common.blocks.MuseumDisplayCaseBlock;
import com.grim3212.assorted.decor.common.blocks.blockentity.DisplayCaseBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

/** Draws a display case's items on their shelves, and the museum case's placard. */
public class DisplayCaseBlockEntityRenderer implements BlockEntityRenderer<DisplayCaseBlockEntity, DisplayCaseBlockEntityRenderer.DisplayCaseRenderState> {

    /** The museum case shows its items a block up, in the glass half standing on the plinth. */
    private static final float MUSEUM_FLOOR = 1.0F;

    private static final float PLACARD_Y = 0.5F;
    private static final float PLACARD_Z = 0.52F;
    /** The widest the name may be drawn, in blocks: the dark field of the plate it sits on. */
    private static final float PLACARD_WIDTH = 0.5625F;
    private static final float PLACARD_SCALE = 0.010416667F;
    private static final int PLACARD_COLOR = -1;

    private final ItemModelResolver itemModelResolver;
    private final Font font;

    public DisplayCaseBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.font = context.font();
    }

    @Override
    public DisplayCaseRenderState createRenderState() {
        return new DisplayCaseRenderState();
    }

    @Override
    public void extractRenderState(DisplayCaseBlockEntity blockEntity, DisplayCaseRenderState state, float partialTicks, Vec3 cameraPosition, net.minecraft.client.renderer.feature.ModelFeatureRenderer.@Nullable CrumblingOverlay breakProgress) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, partialTicks, cameraPosition, breakProgress);

        BlockState blockState = blockEntity.getBlockState();
        Level level = blockEntity.getLevel();
        boolean museum = blockState.getBlock() instanceof MuseumDisplayCaseBlock;

        state.facing = blockState.getValue(DisplayCaseBlock.FACING);
        state.size = blockState.getValue(DisplayCaseBlock.SIZE);
        state.floorY = museum ? MUSEUM_FLOOR : 0.0F;
        // The plinth is a block away from the glass the items are in, so their light is read there.
        state.itemLightCoords = museum && level != null ? LightCoordsUtil.getLightCoords(level, blockEntity.getBlockPos().above()) : state.lightCoords;

        int seed = (int) blockEntity.getBlockPos().asLong();
        for (int slot = 0; slot < DisplayCaseBlock.SLOTS; slot++) {
            this.itemModelResolver.updateForTopItem(state.items[slot], blockEntity.getItem(slot), ItemDisplayContext.FIXED, level, null, seed + slot);
        }

        state.placard = null;
        Component name = museum ? blockEntity.getCustomName() : null;
        if (name != null) {
            state.placard = name.getVisualOrderText();
            int width = this.font.width(name);
            state.placardScale = width > 0 ? Math.min(PLACARD_SCALE, PLACARD_WIDTH / width) : PLACARD_SCALE;
            state.placardOffset = -width / 2.0F;
        }
    }

    @Override
    public void submit(DisplayCaseRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera) {
        for (int slot = 0; slot < state.items.length; slot++) {
            ItemStackRenderState item = state.items[slot];
            if (item.isEmpty()) {
                continue;
            }

            if (!DisplayCaseBlock.isShown(state.size, slot)) {
                continue;
            }

            double across = (DisplayCaseBlock.column(slot) + 0.5D) / state.size;
            double depth = (DisplayCaseBlock.row(slot) + 0.5D) / state.size;
            float scale = DisplayCaseBlock.itemScale(state.size);

            // On its shelf: the same heights the riser is modelled at and the hit boxes measure from.
            poseStack.pushPose();
            poseStack.translate(DisplayCaseBlock.localX(state.facing, across, depth),
                    state.floorY + DisplayCaseBlock.shelfY(state.size, DisplayCaseBlock.row(slot)) + scale / 2.0F,
                    DisplayCaseBlock.localZ(state.facing, across, depth));
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
            poseStack.scale(scale, scale, scale);
            item.submit(poseStack, submitNodeCollector, state.itemLightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        if (state.placard != null) {
            poseStack.pushPose();
            poseStack.translate(0.5F, PLACARD_Y, 0.5F);
            poseStack.mulPose(Axis.YP.rotationDegrees(-state.facing.toYRot()));
            poseStack.translate(0.0F, 0.0F, PLACARD_Z);
            // Negative Y because the font draws downwards.
            poseStack.scale(state.placardScale, -state.placardScale, state.placardScale);
            submitNodeCollector.submitText(poseStack, state.placardOffset, -4.0F, state.placard, false, Font.DisplayMode.POLYGON_OFFSET, state.lightCoords, PLACARD_COLOR, 0, 0);
            poseStack.popPose();
        }
    }

    public static class DisplayCaseRenderState extends BlockEntityRenderState {
        public Direction facing = Direction.NORTH;
        public int size = 1;
        public float floorY;
        public int itemLightCoords;
        public final ItemStackRenderState[] items = createItemStates();
        public @Nullable FormattedCharSequence placard;
        public float placardScale = PLACARD_SCALE;
        public float placardOffset;

        private static ItemStackRenderState[] createItemStates() {
            ItemStackRenderState[] states = new ItemStackRenderState[DisplayCaseBlock.SLOTS];
            for (int slot = 0; slot < states.length; slot++) {
                states[slot] = new ItemStackRenderState();
            }
            return states;
        }
    }
}
