/*
 * This file is part of  Dungeon Blocks.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Dungeon Blocks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Dungeon Blocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Dungeon Blocks.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.dungeonblocks.core.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * The collision shapes of every {@link FacadeShapeBlock} kind. Each kind has three canonical
 * shapes - straight, inner corner, outer corner - all authored for a NORTH-facing block, ie the
 * orientation the models are drawn in at y-rotation 0. {@link IFacadeShapeBlock#buildShapeTable}
 * turns out the other three facings and both handednesses of each corner.
 */
final class FacadeShapes {
    private FacadeShapes() {
    }

    // facade: half a block deep
    private static final VoxelShape FACADE_STRAIGHT = Block.box(0.0D, 0.0D, 8.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape[] FACADE = IFacadeShapeBlock.buildShapeTable(
            FACADE_STRAIGHT,
            Shapes.or(FACADE_STRAIGHT, Block.box(8.0D, 0D, 0D, 16.0D, 16.0D, 8.0D)),
            Block.box(8.0D, 0D, 8.0D, 16.0D, 16.0D, 16.0D));

    // quarter facade: a quarter block deep
    private static final VoxelShape QUARTER_STRAIGHT = Block.box(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
    private static final VoxelShape[] QUARTER_FACADE = IFacadeShapeBlock.buildShapeTable(
            QUARTER_STRAIGHT,
            Shapes.or(QUARTER_STRAIGHT, Block.box(12.0D, 0D, 0D, 16.0D, 16.0D, 12.0D)),
            Block.box(12.0D, 0D, 12.0D, 16.0D, 16.0D, 16.0D));

    // cornice: a half-deep wall with a deeper lip along the top
    private static final VoxelShape CORNICE_STRAIGHT = Shapes.or(
            Block.box(0.0D, 0.0D, 8.0D, 16.0D, 12.0D, 16.0D),
            Block.box(0.0D, 12.0D, 3.0D, 16.0D, 16.0D, 16.0D));
    private static final VoxelShape[] CORNICE = IFacadeShapeBlock.buildShapeTable(
            CORNICE_STRAIGHT,
            Shapes.or(CORNICE_STRAIGHT,
                    Block.box(8.0D, 0D, 0D, 16.0D, 12.0D, 8.0D), Block.box(3D, 12D, 0D, 16D, 16D, 3D)),
            Shapes.or(
                    Block.box(8.0D, 0D, 8.0D, 16.0D, 12.0D, 16.0D),
                    Block.box(3.0D, 12D, 3.0D, 16.0D, 16.0D, 16.0D)));

    // crown molding
    private static final VoxelShape CROWN_STRAIGHT = Shapes.or(
            Block.box(0.0D, 0.0D, 12.0D, 16.0D, 3.0D, 16.0D), // bottom (16x3x4)
            Block.box(0.0D, 9.0D, 10.0D, 16.0D, 16.0D, 16.0D), // top (16x7x6)
            Block.box(0.0D, 8.0D, 12.0D, 16.0D, 9.0D, 14.0D), // notch (16x1x2)
            Block.box(0.0D, 3.0D, 14.0D, 16.0D, 9.0D, 16.0D)); // middle (16x6x2)
    private static final VoxelShape[] CROWN_MOLDING = IFacadeShapeBlock.buildShapeTable(
            CROWN_STRAIGHT,
            Shapes.or(CROWN_STRAIGHT,
                    Block.box(10, 9, 0, 16, 16, 10), Block.box(14, 3, 0, 16, 9, 14),
                    Block.box(12, 8, 0, 16, 9, 12), Block.box(12, 0, 0, 16, 3, 12)),
            Shapes.or(Block.box(10, 9, 10, 16, 16, 16), // top
                    Block.box(14, 3, 14, 16, 9, 16), // middle
                    Block.box(12, 8, 12, 16, 9, 14), // notch
                    Block.box(12, 8, 14, 14, 9, 16), // notch2
                    Block.box(12, 0, 12, 16, 3, 16))); // bottom - 4x4, matching the model element

    // fluted facade
    private static final VoxelShape FLUTED_N1_PART = Block.box(0, 0, 8, 4, 16, 12);
    private static final VoxelShape[] FLUTED_FACADE = IFacadeShapeBlock.buildShapeTable(
            Shapes.or(Block.box(2.0D, 0.0D, 10.0D, 14.0D, 16.0D, 16.0D), FLUTED_N1_PART,
                    Block.box(12.0D, 0.0D, 8.0D, 16.0D, 16.0D, 12.0D)),
            Shapes.or(
                    Block.box(2, 0, 10, 16, 16, 16), FLUTED_N1_PART, Block.box(10, 0, 2, 16, 16, 10),
                    Block.box(8, 0, 0, 12, 16, 4)),
            Block.box(10, 0, 10, 16, 16, 16));

    // ledge: a quarter-by-quarter strip along the top back edge
    private static final VoxelShape LEDGE_STRAIGHT = Block.box(0.0D, 12D, 12.0D, 16D, 16.0D, 16D);
    private static final VoxelShape[] LEDGE = IFacadeShapeBlock.buildShapeTable(
            LEDGE_STRAIGHT,
            Shapes.or(LEDGE_STRAIGHT, Block.box(12D, 12D, 0.0D, 16D, 16.0D, 16D)),
            Block.box(12, 12, 12, 16, 16, 16));

    /** The shape table of a facade-shaped kind; any other DecorType has none. */
    static VoxelShape[] of(DecorType kind) {
        return switch (kind) {
            case FACADE -> FACADE;
            case QUARTER_FACADE -> QUARTER_FACADE;
            case CORNICE -> CORNICE;
            case CROWN_MOLDING -> CROWN_MOLDING;
            case FLUTED_FACADE -> FLUTED_FACADE;
            case LEDGE -> LEDGE;
            default -> throw new IllegalArgumentException(kind + " is not facade-shaped");
        };
    }
}
