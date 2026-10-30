/*
 * This file is part of  DungeonBlocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
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
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mod.gottsch.forge.dungeonblocks.core.block.AbstractBannerBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.DungeonBannerBlockEntity;
import mod.gottsch.forge.dungeonblocks.core.config.DungeonBlocksConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * Draws a wall banner and waves its cloth.
 *
 * <p><b>Orientation.</b> The transform mirrors vanilla's banner renderer: translate to the block
 * centre, spin by {@code -FACING.toYRot()}, then the usual {@code scale(1, -1, -1)} entity-model
 * flip. That flip is not decoration — {@code CubeListBuilder}'s texture unwrap assumes it, and
 * without it every face comes out upside down. What it buys {@link DungeonBannerModel} is a frame
 * that is the same for all four facings: +y down, +z into the wall, origin at the block centre.
 *
 * <p><b>Nothing is read from the BlockEntity.</b> The wave depends only on the clock and the block
 * position, so {@link DungeonBannerBlockEntity} is just the hook the renderer hangs on — see its
 * class comment.
 *
 * <p><b>Two switches, and they are not equals.</b> Each banner carries its own
 * {@link AbstractBannerBlock#ANIMATED} flag, and the client config's {@code animateBanners} is a veto
 * over all of them — so a player can still every banner in the world on their own client, but the
 * config can never animate one that its owner deliberately stilled. Both are read per frame rather
 * than cached, so either takes effect immediately with no chunk rebuild: the geometry never changes,
 * only the angles do.
 *
 * @author Mark Gottschling on Sep 12, 2026
 */
@OnlyIn(Dist.CLIENT)
public class DungeonBannerRenderer implements BlockEntityRenderer<DungeonBannerBlockEntity> {

	@Override
	public AABB getRenderBoundingBox(DungeonBannerBlockEntity blockEntity) {
		return blockEntity.getRenderBoundingBox();
	}

	/**
	 * Every variant's texture is {@code textures/entity/<block id>.png}, derived rather than
	 * declared: adding a banner is then a block registration and two PNGs, with no table here to
	 * keep in step with it.
	 *
	 * <p>Cached because {@code render} runs per banner per frame and a {@link ResourceLocation} is
	 * built from string concatenation. Only ever touched from the render thread, so a plain HashMap
	 * is enough.
	 */
	private static final Map<Block, ResourceLocation> TEXTURES = new HashMap<>();

	private static ResourceLocation textureFor(Block block) {
		return TEXTURES.computeIfAbsent(block, b -> {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(b);
			return ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "textures/entity/" + id.getPath() + ".png");
		});
	}

	/**
	 * One baked model per shape. Both are built up front rather than looked up lazily: there are two
	 * of them, they are cheap, and {@code bakeLayer} is not something to be calling from inside a
	 * render loop.
	 */
	private final Map<DungeonBannerModel.Shape, DungeonBannerModel> models =
			new EnumMap<>(DungeonBannerModel.Shape.class);

	public DungeonBannerRenderer(BlockEntityRendererProvider.Context context) {
		for (DungeonBannerModel.Shape shape : DungeonBannerModel.Shape.values()) {
			this.models.put(shape, new DungeonBannerModel(context.bakeLayer(shape.layer), shape));
		}
	}

	/**
	 * A stable 0..1 offset per block, so a row of banners on one wall waves as a row of separate
	 * banners rather than as one sheet. Vanilla seeds its banner sway the same way.
	 */
	private static float phase(BlockPos pos) {
		return Math.floorMod(pos.hashCode() * 7, 100) / 100.0F;
	}

	@Override
	public void render(DungeonBannerBlockEntity banner, float partialTicks, PoseStack poseStack,
			MultiBufferSource buffer, int packedLight, int packedOverlay) {
		Level level = banner.getLevel();
		BlockState state = banner.getBlockState();
		AbstractBannerBlock block = AbstractBannerBlock.of(state);
		if (level == null || block == null) {
			return;
		}
		Direction facing = state.getValue(AbstractBannerBlock.FACING);
		DungeonBannerModel model = this.models.get(block.isPennant()
				? DungeonBannerModel.Shape.PENNANT
				: DungeonBannerModel.Shape.TALL);

		// the blockstate decides, the config can only veto: a player who turns banners off gets still
		// banners everywhere, but the config can never start a banner its owner deliberately stilled
		boolean animate = state.getValue(AbstractBannerBlock.ANIMATED)
				&& DungeonBlocksConfig.VISUALS.animateBanners.get();
		if (animate) {
			model.wave((float) level.getGameTime() + partialTicks, phase(banner.getBlockPos()));
		} else {
			model.still();
		}

		poseStack.pushPose();
		poseStack.translate(0.5D, 0.5D, 0.5D);
		poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));
		poseStack.scale(1.0F, -1.0F, -1.0F);
		// cutout because the cloth's taper is alpha in the texture rather than geometry. CULLING IS
		// REQUIRED, not a choice: the slices are zero-thickness, so their front and back quads are
		// coincident and z-fight without it. entityCutoutNoCull here is a visible bug.
		model.render(poseStack, buffer.getBuffer(RenderType.entityCutout(textureFor(state.getBlock()))),
				packedLight, packedOverlay);
		poseStack.popPose();
	}
}
