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
import mod.gottsch.forge.dungeonblocks.core.block.TombDoorBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.TombDoorBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;

/**
 * Draws a tomb door's stone slab from its LOWER half, always: shut, open, and sliding between. The
 * slide starts whenever the blockstate's OPEN differs from what is shown, and eases over
 * {@link TombDoorBlock#MOVE_TICKS}; on first sight it is drawn where it rests, without sliding.
 *
 * <p>The model (block/tomb_door_slab) is authored facing north; it is turned to FACING, then moved
 * along +x - the slide direction for a north-facing door. Faces are shaded per side as the chunk
 * renderer shades a baked block (the sarcophagus lid's method), so the slab matches the stone round
 * it. The light is the brighter of the two halves', so the upper half is not drawn in the dark.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class TombDoorRenderer implements BlockEntityRenderer<TombDoorBlockEntity> {
    public static final ModelResourceLocation SLAB = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/tomb_door_slab"));
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, null};

    public TombDoorRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TombDoorBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getLevel() == null || !(state.getBlock() instanceof TombDoorBlock)) {
            return;
        }
        boolean open = state.getValue(TombDoorBlock.OPEN);
        double now = be.getLevel().getGameTime() + partialTick;
        if (be.shownOpen == null) {
            be.shownOpen = open;
            be.slide = open ? 1F : 0F;
            be.slideFrom = be.slide;
        } else if (be.shownOpen != open) {
            be.shownOpen = open;
            be.slideFrom = be.slide;
            be.moveStart = now;
        }
        float t = Mth.clamp((float) ((now - be.moveStart) / TombDoorBlock.MOVE_TICKS), 0F, 1F);
        float eased = t * t * (3F - 2F * t);
        be.slide = Mth.lerp(eased, be.slideFrom, open ? 1F : 0F);

        int upper = LevelRenderer.getLightColor(be.getLevel(), be.getBlockPos().above());
        int lit = Math.max(light & 0xFFFF, upper & 0xFFFF) | Math.max(light >>> 16, upper >>> 16) << 16;

        BakedModel slab = Minecraft.getInstance().getModelManager().getModel(SLAB);
        Direction facing = state.getValue(TombDoorBlock.FACING);
        pose.pushPose();
        pose.translate(0.5, 0, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180F) % 360F)));
        pose.translate(-0.5, 0, -0.5);
        pose.translate(TombDoorBlock.OPEN_SLIDE * be.slide, 0, 0);
        VertexConsumer consumer = buffers.getBuffer(RenderType.solid());
        RandomSource random = RandomSource.create(42L);
        Rotation turn = turn(facing);
        for (Direction side : SIDES) {
            for (BakedQuad quad : slab.getQuads(state, side, random, ModelData.EMPTY, RenderType.solid())) {
                float shade = be.getLevel().getShade(turn.rotate(quad.getDirection()), quad.isShade());
                consumer.putBulkData(pose.last(), quad, shade, shade, shade, 1F, lit, overlay);
            }
        }
        pose.popPose();
    }

    /** The horizontal turn the blockstate would give a north-authored model facing `facing`. */
    private static Rotation turn(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    @Override
    public AABB getRenderBoundingBox(TombDoorBlockEntity blockEntity) {
        return blockEntity.getRenderBoundingBox();
    }
}
