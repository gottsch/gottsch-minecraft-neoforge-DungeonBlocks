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
package mod.gottsch.forge.dungeonblocks.core.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.gottsch.forge.dungeonblocks.DungeonBlocks;
import mod.gottsch.forge.dungeonblocks.core.entity.TomeVariant;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.util.Locale;

/**
 * A tome lying on its back, in texels of its texture - {@link TomeRenderer} draws it at 32 to the
 * block. One layer per {@link TomeVariant.Shape}: the boxes are sized from it, and so are their
 * places on the texture. Unlike every other model here it is built y-UP, as it lies, and is drawn
 * without the usual (-1, -1, 1) flip; so a box's side faces take the top row of their texture
 * region at their BOTTOM edge (the texture generator, tools/gen_tome_textures.py, allows for it).
 *
 * <p>The spine runs along z at x 0, and every part hinges on the line along the spine at the height
 * of the page tops. The back half (lid, a block of pages, half the spine) lies still; the front
 * half is the same shapes mirrored into -x, and closes by turning half a turn over onto it.
 * Two loose pages, drawn only while they turn, sweep from the back pages over to the front ones.
 */
public class TomeModel extends Model {
	private static final int LID = TomeVariant.Shape.LID;

	private final TomeVariant.Shape shape;
	private final ModelPart book;
	private final ModelPart front;
	private final ModelPart[] turningPages;

	public TomeModel(ModelPart root, TomeVariant.Shape shape) {
		super(RenderType::entityCutout);
		this.shape = shape;
		this.book = root.getChild("book");
		this.front = this.book.getChild("front");
		this.turningPages = new ModelPart[] {this.book.getChild("page_1"), this.book.getChild("page_2")};
	}

	public static ModelLayerLocation layer(TomeVariant.Shape shape) {
		return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DungeonBlocks.MOD_ID, "tome"),
				shape.name().toLowerCase(Locale.ROOT));
	}

	public static LayerDefinition createBodyLayer(TomeVariant.Shape shape) {
		int w = shape.width, h = shape.height, p = shape.pages;
		// each box's texture region below the last: lid, pages, spine, loose page (a box W x H x D
		// unwraps to 2D + 2W by D + H)
		int pagesV = h + LID;
		int spineV = pagesV + (h - 1) + p;
		int pageV = spineV + h + p;
		int textureHeight = Integer.highestOneBit(pageV + h - 1 - 1) << 1;

		MeshDefinition mesh = new MeshDefinition();
		PartDefinition book = mesh.getRoot().addOrReplaceChild("book", CubeListBuilder.create(), PartPose.ZERO);
		float bottom = -(LID + p);
		book.addOrReplaceChild("back", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0.0F, bottom, 0.0F, w, LID, h)
				.texOffs(0, pagesV).addBox(1.0F, -p, 0.5F, w - 2, p, h - 1)
				.texOffs(0, spineV).addBox(0.0F, -p, 0.0F, 1.0F, p, h), PartPose.ZERO);
		book.addOrReplaceChild("front", CubeListBuilder.create()
				.texOffs(0, 0).addBox(-w, bottom, 0.0F, w, LID, h)
				.texOffs(0, pagesV).addBox(1 - w, -p, 0.5F, w - 2, p, h - 1)
				.texOffs(0, spineV).addBox(-1.0F, -p, 0.0F, 1.0F, p, h), PartPose.ZERO);
		// a hair above the pages, so it does not fight them as it starts to lift
		CubeListBuilder page = CubeListBuilder.create()
				.texOffs(0, pageV).addBox(1.0F, 0.05F, 0.5F, w - 2, 0.0F, h - 1);
		book.addOrReplaceChild("page_1", page, PartPose.ZERO);
		book.addOrReplaceChild("page_2", page, PartPose.ZERO);
		return LayerDefinition.create(mesh, 64, textureHeight);
	}

	/**
	 * @param openness  0 closed, 1 lying open
	 * @param pageTurns how far each loose page is through its turn, 0 to 1, or negative when at rest
	 */
	public void setupAnim(float openness, float... pageTurns) {
		// the hinge sits at the page tops, the lid's underside on the ground; closed, the book is
		// centred on its cover, open on its spine
		this.book.x = -this.shape.width / 2.0F * (1.0F - openness);
		this.book.y = LID + this.shape.pages;
		this.book.z = -this.shape.height / 2.0F;
		// negative, so the front half swings up and over rather than down through the ground
		this.front.zRot = -Mth.PI * (1.0F - openness);
		for (int i = 0; i < this.turningPages.length; i++) {
			float turn = pageTurns[i];
			this.turningPages[i].visible = openness >= 1.0F && turn >= 0.0F;
			// eased, so a page lifts quickly and settles slowly
			this.turningPages[i].zRot = Mth.PI * (1.0F - (1.0F - turn) * (1.0F - turn));
		}
	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		this.book.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
	}
}
