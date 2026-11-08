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
package mod.gottsch.forge.dungeonblocks.core.entity.client;

import net.minecraft.client.resources.model.ModelResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.neoforged.neoforge.client.model.data.ModelData;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.SkeletonRemainsEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * Draws {@link SkeletonRemainsEntity Skeleton Remains}: vanilla's skeleton model (its baked layer,
 * so its proportions are vanilla's) with each posture's limb angles, in the mod's warm bone
 * (textures/entity/skeleton_remains.png, from tools/gen_skeleton_remains_texture.py).
 *
 * <p>The whole figure is first turned to the entity's yaw, as a mob is; then its posture places it
 * (tipped onto its back, side or front with the hips over the entity's position, lowered to sit, or
 * left standing to hang), and then the model's own flip, as LivingEntityRenderer does.
 * The angle tables are radians on each part, in model space. Vanilla's skeleton has no knees or
 * elbows, so every pose is whole limbs turned at the shoulder and hip. Every resting pose sits
 * {@link #CLEARANCE} above the ground.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class SkeletonRemainsRenderer extends EntityRenderer<SkeletonRemainsEntity> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "textures/entity/skeleton_remains.png");
    private static final float PI = (float) Math.PI;
    private static final float HALF_PI = PI / 2;

    /**
     * A hair above whatever it rests on: a face exactly on the ground's surface z-fights with it
     * (seen in game on the lying spine, Nov 4). About a sixth of a pixel, too little to see as a gap.
     */
    private static final float CLEARANCE = 0.01F;
    /** Lying on its back or front: half the body's 4px depth up, so it rests on the ground. */
    private static final float FLAT_LIFT = 2 / 16F + CLEARANCE;
    /** On its side: the lower shoulder rests on the ground, 6px out from the middle (an arm past the body). */
    private static final float SIDE_LIFT = 6 / 16F + CLEARANCE;
    /** How far the hips are above the feet: a lying figure is moved this far to put them over its position. */
    private static final float HIP = 12 / 16F;
    /** Sitting: lowered so the legs, flat on the ground, rest on it (hips 12px up, legs 2px thick). */
    private static final float SEAT_DROP = 11 / 16F - CLEARANCE;

    // part order: head, body, right arm, left arm, right leg, left leg; each {xRot, yRot, zRot}
    /** On its back: head turned aside, arms a little out from the sides, legs a little apart. */
    private static final float[][] LYING = {{0, 0.5F, 0}, {0, 0, 0}, {0, 0, 0.12F}, {0, 0, -0.12F},
            {0, 0, 0.06F}, {0, 0, -0.06F}};
    /** Slumped: head fallen forward and aside, arms hanging forward, legs out in front and apart. */
    private static final float[][] SITTING = {{0.6F, 0, 0.25F}, {0, 0, 0}, {-0.25F, 0, 0.12F}, {-0.25F, 0, -0.12F},
            {-HALF_PI, 0.25F, 0}, {-HALF_PI, -0.25F, 0}};
    /** Curled on its side: head bowed, arms in front, legs drawn up, one more than the other. */
    private static final float[][] ON_SIDE = {{0.3F, 0, 0}, {0, 0, 0}, {-0.9F, 0, 0}, {-1.1F, 0, 0},
            {-0.9F, 0, 0}, {-0.5F, 0, 0}};
    /** Face down: head turned aside, one arm flung out past the head, one at its side, a leg lifted a little. */
    private static final float[][] FACE_DOWN = {{0, 0.6F, 0}, {0, 0, 0}, {-PI, 0, 0.2F}, {0, 0, -0.15F},
            {0, 0, 0.08F}, {0.2F, 0, -0.08F}};
    /**
     * Hung by the wrists: arms up and spread a little, head fallen forward and aside, legs limp. With
     * the arm raised (xRot -PI) a positive zRot tips the right hand in toward the head, so the spread
     * is the negative: the wrists then sit 5.9px out, in the shackles' cuffs.
     */
    private static final float[][] HANGING = {{0.55F, 0, 0.2F}, {0, 0, 0}, {-PI, 0, -0.12F}, {-PI, 0, 0.12F},
            {0.05F, 0, 0.03F}, {-0.05F, 0, -0.03F}};

    /**
     * The shackles a hanging one hangs from (blockbench/skeleton_remains_shackles.bbmodel), a block
     * model registered by ClientSetup: its y 0 is level with the raised hands' tips.
     */
    public static final ModelResourceLocation SHACKLES = ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/skeleton_remains_shackles"));
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, null};

    /** Hanging: the shoulders pulled up by the body's weight, in model px, so the hands are above the head. */
    private static final float SHRUG = 4.0F;

    private final ModelPart root;
    private final ModelPart[] parts;
    /** The arms' own shoulder height, which only hanging changes. */
    private final float shoulderY;

    public SkeletonRemainsRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.root = context.bakeLayer(ModelLayers.SKELETON);
        this.root.getChild("hat").visible = false;
        this.parts = new ModelPart[] {root.getChild("head"), root.getChild("body"), root.getChild("right_arm"),
                root.getChild("left_arm"), root.getChild("right_leg"), root.getChild("left_leg")};
        this.shoulderY = parts[2].y;
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(SkeletonRemainsEntity remains, float yaw, float partialTick, PoseStack pose,
                       MultiBufferSource buffers, int light) {
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - remains.getYRot()));
        float[][] angles = switch (remains.getPosture()) {
            case LYING -> {
                pose.translate(0, FLAT_LIFT, -HIP);
                pose.mulPose(Axis.XP.rotationDegrees(90.0F));
                yield LYING;
            }
            case FACE_DOWN -> {
                pose.translate(0, FLAT_LIFT, HIP);
                pose.mulPose(Axis.XP.rotationDegrees(-90.0F));
                yield FACE_DOWN;
            }
            case ON_SIDE -> {
                pose.translate(HIP, SIDE_LIFT, 0);
                pose.mulPose(Axis.ZP.rotationDegrees(90.0F));
                yield ON_SIDE;
            }
            case SITTING -> {
                pose.translate(0, -SEAT_DROP, 0);
                yield SITTING;
            }
            case HANGING -> HANGING;
        };
        // ModelParts are shared by every remains drawn: set every angle, and the shoulders, every time
        for (int i = 0; i < parts.length; i++) {
            parts[i].xRot = angles[i][0];
            parts[i].yRot = angles[i][1];
            parts[i].zRot = angles[i][2];
        }
        float shoulder = remains.getPosture() == SkeletonRemainsEntity.Posture.HANGING ? shoulderY - SHRUG : shoulderY;
        parts[2].y = shoulder;
        parts[3].y = shoulder;
        pose.scale(-1.0F, -1.0F, 1.0F);
        pose.translate(0.0F, -1.501F, 0.0F);
        this.root.render(pose, buffers.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), light, OverlayTexture.NO_OVERLAY);
        pose.popPose();
        if (remains.getPosture() == SkeletonRemainsEntity.Posture.HANGING) {
            shackles(remains, pose, buffers, light);
        }
        super.render(remains, yaw, partialTick, pose, buffers, light);
    }

    /** The shackles over the raised hands, turned with the body, shaded per face as a baked block is. */
    private void shackles(SkeletonRemainsEntity remains, PoseStack pose, MultiBufferSource buffers, int light) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(SHACKLES);
        pose.pushPose();
        pose.mulPose(Axis.YP.rotationDegrees(180.0F - remains.getYRot()));
        pose.translate(-0.5D, SkeletonRemainsEntity.HANDS_UP, -0.5D);
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
        RandomSource random = RandomSource.create(42L);
        for (Direction side : SIDES) {
            for (BakedQuad quad : model.getQuads(null, side, random, ModelData.EMPTY, RenderType.cutout())) {
                float shade = remains.level().getShade(quad.getDirection(), quad.isShade());
                consumer.putBulkData(pose.last(), quad, shade, shade, shade, 1F, light, OverlayTexture.NO_OVERLAY);
            }
        }
        pose.popPose();
    }

    @Override
    public ResourceLocation getTextureLocation(SkeletonRemainsEntity remains) {
        return TEXTURE;
    }
}
