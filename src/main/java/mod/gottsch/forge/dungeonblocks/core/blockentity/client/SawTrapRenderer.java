/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * DungeonBlocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with DungeonBlocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.blockentity.client;

import net.minecraft.world.phys.AABB;
import net.minecraft.client.resources.model.ModelResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.SawTrapBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.SawTrapBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Draws a saw trap's blade: rising fast out of the slot when RAISED turns true, sinking slower when
 * it turns false, spinning all the while it is out. Down, nothing is drawn: the blade is inside a
 * solid cube. On first sight it is drawn where it rests, without moving.
 *
 * <p>The model (block/saw_blade) is a disc 2px thick standing on the block's middle, in the yz plane,
 * so it runs along z, as the slot does for AXIS=Z; for AXIS=X it is turned a quarter, as the
 * blockstate turns the slot. Lit by the block above, since the trap's own light inside a solid
 * cube is dark. Faces are shaded per side as a baked block's are (the tomb door's method).
 *
 * @author Mark Gottschling on 11/05/2026
 */
public class SawTrapRenderer implements BlockEntityRenderer<SawTrapBlockEntity> {
    public static final ModelResourceLocation BLADE = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/saw_blade"));
    /** Ticks for the blade to come up: a snap. */
    private static final float RISE_TICKS = 3F;
    /** Ticks for it to sink back. */
    private static final float SINK_TICKS = 8F;
    /** Where the blade's centre is drawn, in blocks above the model's: down (hidden), and up. */
    private static final float DOWN = -2F / 16F;
    private static final float UP = 9F / 16F;
    /** Degrees the blade turns per tick. */
    private static final float SPIN = 36F;
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, null};

    public SawTrapRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(SawTrapBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getLevel() == null || !(state.getBlock() instanceof SawTrapBlock)) {
            return;
        }
        boolean raised = state.getValue(SawTrapBlock.RAISED);
        double now = be.getLevel().getGameTime() + partialTick;
        if (be.shownRaised == null) {
            be.shownRaised = raised;
            be.raise = raised ? 1F : 0F;
            be.raiseFrom = be.raise;
        } else if (be.shownRaised != raised) {
            be.shownRaised = raised;
            be.raiseFrom = be.raise;
            be.moveStart = now;
        }
        float t = Mth.clamp((float) ((now - be.moveStart) / (raised ? RISE_TICKS : SINK_TICKS)), 0F, 1F);
        float eased = raised ? 1F - (1F - t) * (1F - t) : t * t * (3F - 2F * t);
        be.raise = Mth.lerp(eased, be.raiseFrom, raised ? 1F : 0F);
        if (be.raise <= 0F) {
            return;
        }

        int lit = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());
        boolean alongX = state.getValue(SawTrapBlock.AXIS) == Direction.Axis.X;
        BakedModel blade = Minecraft.getInstance().getModelManager().getModel(BLADE);
        pose.pushPose();
        if (alongX) {
            pose.translate(0.5, 0, 0.5);
            pose.mulPose(Axis.YP.rotationDegrees(-90F));
            pose.translate(-0.5, 0, -0.5);
        }
        pose.translate(0, Mth.lerp(be.raise, DOWN, UP), 0);
        pose.translate(0.5, 0.5, 0.5);
        pose.mulPose(Axis.XP.rotationDegrees((float) ((now * SPIN) % 360.0)));
        pose.translate(-0.5, -0.5, -0.5);
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
        RandomSource random = RandomSource.create(42L);
        Rotation turn = alongX ? Rotation.CLOCKWISE_90 : Rotation.NONE;
        for (Direction side : SIDES) {
            for (BakedQuad quad : blade.getQuads(state, side, random, ModelData.EMPTY, RenderType.cutout())) {
                float shade = be.getLevel().getShade(turn.rotate(quad.getDirection()), quad.isShade());
                consumer.putBulkData(pose.last(), quad, shade, shade, shade, 1F, lit, overlay);
            }
        }
        pose.popPose();
    }

    @Override
    public AABB getRenderBoundingBox(SawTrapBlockEntity blockEntity) {
        return blockEntity.getRenderBoundingBox();
    }
}
