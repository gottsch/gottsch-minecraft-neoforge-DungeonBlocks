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

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.phys.AABB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.block.SarcophagusBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.SarcophagusBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.core.registries.BuiltInRegistries;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * Draws every sarcophagus and coffin lid, always - the way a chest's lid is drawn - so the lid is
 * never handed between this renderer and the block's baked model, which is what made it flicker.
 * The block's own model is the body alone.
 *
 * <p>The animation needs no ticking. Each frame the block's OPEN is compared with the OPEN the lid
 * was last heading for; when they differ, a move starts from wherever the lid is drawn now, so
 * reversing it mid-move is smooth. Position is eased over {@link SarcophagusBlock#MOVE_TICKS}.
 *
 * <p>The lid is the lid-only model ({@code <id>_<part>_lid}, registered as an additional model in
 * ClientSetup), turned to the block's facing, then moved as the block's
 * {@link SarcophagusBlock.LidMotion} says: a sarcophagus lid slides along the model's x axis, a
 * coffin lid swings up on its hinge.
 */
public class SarcophagusRenderer implements BlockEntityRenderer<SarcophagusBlockEntity> {

	@Override
	public AABB getRenderBoundingBox(SarcophagusBlockEntity blockEntity) {
		return blockEntity.getRenderBoundingBox();
	}
    /** How far a sliding lid moves aside when open, in blocks. */
    private static final float OPEN_SLIDE = 10F / 16F;
    /**
     * A coffin lid's hinge: its long east edge, shoulder to foot, along its underside, in the head
     * block's model space (the foot block's is one block further along z). The same line as
     * coffin_hinge() in tools/gen_obj_models.py - change them together.
     */
    private static final Vector3f HINGE_FROM = new Vector3f(15.01577F, 8F, 7.93417F).div(16F);
    private static final Vector3f HINGE_AXIS = new Vector3f(11.42967F - 15.01577F, 0F, 31.5F - 7.93417F).normalize();
    /** How far a hinged lid swings open: a little past upright, so it stands. */
    private static final float OPEN_ANGLE = 100F;

    public SarcophagusRenderer(BlockEntityRendererProvider.Context context) {
    }

    /** The additional model holding one half's lid, for ClientSetup to register. */
    public static ModelResourceLocation lidModel(ResourceLocation blockId, BedPart part) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "block/" + blockId.getPath() + "_"
                + (part == BedPart.HEAD ? "head" : "foot") + "_lid"));
    }

    @Override
    public void render(SarcophagusBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getLevel() == null || !(state.getBlock() instanceof SarcophagusBlock)) {
            return;
        }
        boolean open = state.getValue(SarcophagusBlock.OPEN);
        double now = be.getLevel().getGameTime() + partialTick;
        if (be.shownOpen == null) {
            // first frame drawn (placed, or its chunk just loaded): show it where it is, no slide
            be.shownOpen = open;
            be.slide = open ? 1F : 0F;
        } else if (be.shownOpen != open) {
            // the lid was sent the other way: slide from wherever it is drawn now
            be.shownOpen = open;
            be.slideFrom = be.slide;
            be.moveStart = now;
        }
        float t = Mth.clamp((float) ((now - be.moveStart) / SarcophagusBlock.MOVE_TICKS), 0F, 1F);
        float eased = t * t * (3F - 2F * t);
        be.slide = Mth.lerp(eased, be.slideFrom, open ? 1F : 0F);

        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        BakedModel lid = Minecraft.getInstance().getModelManager().getModel(lidModel(id, state.getValue(SarcophagusBlock.PART)));

        pose.pushPose();
        // turn about the block's centre the way the blockstate turns the baked model (y = yaw + 180,
        // applied as a negative rotation, as vanilla's BlockModelRotation does), then slide
        Direction facing = state.getValue(SarcophagusBlock.FACING);
        pose.translate(0.5, 0, 0.5);
        pose.mulPose(Axis.YP.rotationDegrees(-((facing.toYRot() + 180F) % 360F)));
        pose.translate(-0.5, 0, -0.5);
        if (((SarcophagusBlock) state.getBlock()).lidMotion() == SarcophagusBlock.LidMotion.HINGE) {
            hinge(pose, state.getValue(SarcophagusBlock.PART), be.slide);
        } else {
            pose.translate(OPEN_SLIDE * be.slide, 0, 0);
        }
        drawShaded(be, state, lid, turn(facing), pose, buffers.getBuffer(RenderType.solid()), light, overlay);
        pose.popPose();
    }

    /**
     * Swings a coffin lid `open` (0 shut, 1 fully open) about its hinge, in model space. The two
     * halves turn about the same line - the foot's model sits a block further along z - so they
     * move as one lid.
     */
    private static void hinge(PoseStack pose, BedPart part, float open) {
        float z = HINGE_FROM.z - (part == BedPart.FOOT ? 1F : 0F);
        pose.translate(HINGE_FROM.x, HINGE_FROM.y, z);
        // negative: the free edge, on the hinge's west side, lifts
        pose.mulPose(new Quaternionf().rotationAxis(-OPEN_ANGLE * open * Mth.DEG_TO_RAD, HINGE_AXIS));
        pose.translate(-HINGE_FROM.x, -HINGE_FROM.y, -z);
    }

    /**
     * Draws the model's quads with the per-face shading the chunk renderer gives a baked block -
     * top faces full bright, north/south dimmer, east/west dimmer still. The vanilla helper
     * (ModelBlockRenderer.renderModel) skips that, and the sliding lid came out visibly lighter
     * than the same lid at rest. Ambient occlusion is still not applied; on a lid, with nothing
     * pressed against it, the difference is slight.
     */
    private static void drawShaded(SarcophagusBlockEntity be, BlockState state, BakedModel model, Rotation turn,
                                   PoseStack pose, VertexConsumer consumer, int light, int overlay) {
        RandomSource random = RandomSource.create(42L);
        for (Direction side : SIDES) {
            for (BakedQuad quad : model.getQuads(state, side, random, ModelData.EMPTY, RenderType.solid())) {
                // the quad's direction is the unturned model's; shade by the way it faces in the
                // world, as the chunk renderer does with the blockstate's rotation baked in
                float shade = be.getLevel().getShade(turn.rotate(quad.getDirection()), quad.isShade());
                consumer.putBulkData(pose.last(), quad, shade, shade, shade, 1.0F, light, overlay);
            }
        }
    }

    /** The horizontal turn the blockstate gives a north-authored model facing `facing`. */
    private static Rotation turn(Direction facing) {
        return switch (facing) {
            case EAST -> Rotation.CLOCKWISE_90;
            case SOUTH -> Rotation.CLOCKWISE_180;
            case WEST -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
    }

    /** Every face direction, then null for the quads that belong to no face (unculled ones). */
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, null};
}
