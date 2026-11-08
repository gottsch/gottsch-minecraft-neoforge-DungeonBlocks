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
import mod.gottsch.forge.dungeonblocks.core.block.ChandelierBlock;
import mod.gottsch.forge.dungeonblocks.core.block.LargeChandelierBlock;
import mod.gottsch.forge.dungeonblocks.core.blockentity.LargeChandelierBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.minecraft.core.registries.BuiltInRegistries;

/**
 * Draws a {@link LargeChandelierBlock} from its nine part models,
 * {@code block/<block>[_lit]_<x>_<z>}: tools/bbmodel_to_block_models.py splits the chandelier by
 * where each element lies across x and z and moves the outer parts a block in, so each fits a block
 * model's -16..32. Part (x, z) is drawn moved back x - 1 blocks and z - 1.
 *
 * <p>Faces are shaded per side as the chunk renderer shades a baked block (the tomb door's and the
 * sarcophagus lid's method), and lit with the core block's light, which is 15 when the candles burn.
 * Cutout, for the chain's and the candles' see-through pixels.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class LargeChandelierRenderer implements BlockEntityRenderer<LargeChandelierBlockEntity> {
    private static final Direction[] SIDES = {Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, null};

    public LargeChandelierRenderer(BlockEntityRendererProvider.Context context) {
    }

    /** One part's model, for the block with this registry id. ClientSetup registers all eighteen per block. */
    public static ModelResourceLocation partModel(ResourceLocation block, boolean lit, int x, int z) {
        return ModelResourceLocation.standalone(ResourceLocation.fromNamespaceAndPath(block.getNamespace(), "block/" + block.getPath() + (lit ? "_lit" : "") + "_" + x + "_" + z));
    }

    @Override
    public void render(LargeChandelierBlockEntity be, float partialTick, PoseStack pose, MultiBufferSource buffers,
                       int light, int overlay) {
        BlockState state = be.getBlockState();
        if (be.getLevel() == null || !(state.getBlock() instanceof LargeChandelierBlock)) {
            return;
        }
        ResourceLocation id = BuiltInRegistries.BLOCK.getKey(state.getBlock());
        boolean lit = state.getValue(ChandelierBlock.LIT);
        ModelManager models = Minecraft.getInstance().getModelManager();
        VertexConsumer consumer = buffers.getBuffer(RenderType.cutout());
        RandomSource random = RandomSource.create(42L);
        for (int x = 0; x < 3; x++) {
            for (int z = 0; z < 3; z++) {
                BakedModel part = models.getModel(partModel(id, lit, x, z));
                pose.pushPose();
                pose.translate(x - 1, 0, z - 1);
                for (Direction side : SIDES) {
                    for (BakedQuad quad : part.getQuads(state, side, random, ModelData.EMPTY, RenderType.cutout())) {
                        float shade = be.getLevel().getShade(quad.getDirection(), quad.isShade());
                        consumer.putBulkData(pose.last(), quad, shade, shade, shade, 1F, light, overlay);
                    }
                }
                pose.popPose();
            }
        }
    }

    @Override
    public AABB getRenderBoundingBox(LargeChandelierBlockEntity blockEntity) {
        return blockEntity.getRenderBoundingBox();
    }
}
