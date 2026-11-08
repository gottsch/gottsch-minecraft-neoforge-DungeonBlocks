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
package mod.gottsch.forge.dungeonblocks.core.item;

import mod.gottsch.forge.dungeonblocks.core.entity.ModEntityTypes;
import mod.gottsch.forge.dungeonblocks.core.entity.SkeletonRemainsEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

/**
 * Places {@link SkeletonRemainsEntity Skeleton Remains}. On a floor (or a slab, a bed, a bench) it
 * lies on its back where you click, its feet toward you; on a wall it sits slumped against it,
 * facing out; on a ceiling it hangs by its wrists, facing you, if there are two blocks of room
 * beneath. It turns in 22.5-degree steps; all but the hanging one settle onto whatever is beneath.
 *
 * @author Mark Gottschling on 11/04/2026
 */
public class SkeletonRemainsItem extends Item {
    /** How far a sitting skeleton's centre is from the wall: its back and skull just touch it. */
    private static final double WALL_GAP = 0.25D;

    public SkeletonRemainsItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Direction face = context.getClickedFace();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        if (!level.isClientSide) {
            SkeletonRemainsEntity remains = ModEntityTypes.SKELETON_REMAINS.get().create(level);
            if (remains == null) {
                return InteractionResult.PASS;
            }
            if (face == Direction.DOWN) {
                // hung from the ceiling by its wrists, where clicked, its raised hands touching it
                Vec3 at = context.getClickLocation();
                float yaw = (player != null ? player.getYRot() : 0.0F) + 180.0F;
                remains.setPosture(SkeletonRemainsEntity.Posture.HANGING);
                remains.setNoGravity(true);
                remains.setPos(at.x, at.y - SkeletonRemainsEntity.HANGING_REACH - 0.01D, at.z);
                remains.setYRot(Math.round(yaw / 22.5F) * 22.5F);
                // room for the whole body, not only the small box at its feet
                if (!level.noCollision(new AABB(at.x - 0.2D, at.y - SkeletonRemainsEntity.HANGING_REACH, at.z - 0.2D,
                        at.x + 0.2D, at.y - 0.02D, at.z + 0.2D))) {
                    return InteractionResult.FAIL;
                }
            } else if (face == Direction.UP) {
                // its feet toward the player who lays it out
                float yaw = (player != null ? player.getYRot() : 0.0F) + 180.0F;
                remains.setPosture(SkeletonRemainsEntity.Posture.LYING);
                remains.setPos(context.getClickLocation());
                remains.setYRot(Math.round(yaw / 22.5F) * 22.5F);
            } else {
                Vec3 cell = Vec3.atBottomCenterOf(context.getClickedPos().relative(face));
                remains.setPosture(SkeletonRemainsEntity.Posture.SITTING);
                remains.setPos(cell.subtract(face.getStepX() * (0.5D - WALL_GAP), 0.0D, face.getStepZ() * (0.5D - WALL_GAP)));
                remains.setYRot(face.toYRot());
            }
            if (!level.noCollision(remains.getBoundingBox().deflate(1.0E-4D))) {
                return InteractionResult.FAIL;
            }
            level.addFreshEntity(remains);
            level.playSound(null, remains.getX(), remains.getY(), remains.getZ(), SoundEvents.BONE_BLOCK_PLACE,
                    SoundSource.BLOCKS, 1.0F, 0.9F);
            level.gameEvent(player, GameEvent.ENTITY_PLACE, remains.position());
        }
        if (player == null || !player.getAbilities().instabuild) {
            context.getItemInHand().shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
