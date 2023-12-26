package mod.gottsch.neo.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mark Gottschling on Nov 6, 2023
 *
 */
public class LedgeBlock extends FacadeShapeBlock {
    private static final VoxelShape NORTH_FACING_SHAPE = Block.box(0.0D, 12D, 12.0D, 16D, 16.0D, 16D);
    private static final VoxelShape EAST_FACING_SHAPE = Block.box(0D, 12D, 0.0D, 4D, 16.0D, 16D);
    private static final VoxelShape SOUTH_FACING_SHAPE = Block.box(0.0D, 12D, 0D, 16D, 16.0D, 4D);
    private static final VoxelShape WEST_FACING_SHAPE = Block.box(12D, 12D, 0.0D, 16D, 16.0D, 16D);


    // inner corners
    private static final VoxelShape TOP_LEFT_INNER_SHAPE = Shapes.or(
            SOUTH_FACING_SHAPE,
            EAST_FACING_SHAPE
    );
    private static final VoxelShape TOP_RIGHT_INNER_SHAPE = Shapes.or(
            SOUTH_FACING_SHAPE,
            WEST_FACING_SHAPE
    );

    private static final VoxelShape BOTTOM_LEFT_INNER_SHAPE = Shapes.or(
            NORTH_FACING_SHAPE,
            EAST_FACING_SHAPE
    );

    private static final VoxelShape BOTTOM_RIGHT_INNER_SHAPE = Shapes.or(
            NORTH_FACING_SHAPE,
            WEST_FACING_SHAPE
    );

    // outer corners
    private static final VoxelShape TOP_LEFT_OUTER_SHAPE = Shapes.or(
            Block.box(12, 12, 12, 16, 16, 16)
    );
    private static final VoxelShape TOP_RIGHT_OUTER_SHAPE = Shapes.or(
            Block.box(0, 12, 12, 4, 16, 16)
    );

    /*
     * the bottom left model actually corresponds to the top right area of the block,
     * but to keep the naming consistent, this is named bottom_left
     */
    private static final VoxelShape BOTTOM_LEFT_OUTER_SHAPE = Shapes.or(
            Block.box(12, 12, 0, 16, 16, 4)
    );

    private static final VoxelShape BOTTOM_RIGHT_OUTER_SHAPE = Shapes.or(
            Block.box(0, 12, 0, 4, 16, 4)
    );

    // SWNE = 0,1,2,3
    private VoxelShape voxelShapes[] = {
            // straight
            SOUTH_FACING_SHAPE, WEST_FACING_SHAPE, NORTH_FACING_SHAPE, EAST_FACING_SHAPE,

            // inner left
            TOP_LEFT_INNER_SHAPE, // 4
            BOTTOM_LEFT_INNER_SHAPE, // 5

            // inner right
            TOP_RIGHT_INNER_SHAPE, // 6
            BOTTOM_RIGHT_INNER_SHAPE, // 7

            // outer left
            TOP_LEFT_OUTER_SHAPE, BOTTOM_LEFT_OUTER_SHAPE,

            // outer right
            TOP_RIGHT_OUTER_SHAPE, BOTTOM_RIGHT_OUTER_SHAPE };


    public LedgeBlock(Properties properties) {
        super(properties);
    }

//    @Override
//    public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
//        Direction direction = state.getValue(FACING);
//
//        return switch (direction) {
//            case UP -> UP_SHAPE;
//            case NORTH -> NORTH_SHAPE;
//            case SOUTH -> SOUTH_SHAPE;
//            case EAST -> EAST_SHAPE;
//            case WEST -> WEST_SHAPE;
//            default -> DOWN_SHAPE;
//        };
//    }
    /**
     * Returns the VoxelShape (ie bounding box) of the block in the correctposition.
     * NOTE if shape != STRAIGHT, then facing index can only == North || South
     */
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        int shapeIndex = getBlockShapeIndex(state, getter, pos, context);
        return voxelShapes[shapeIndex];
    }

    /**
     * Checks if a block is same as LedgeBlock
     */
    @Override
    public boolean isBlockInstanceOf(Block block) {
        return block instanceof LedgeBlock;
    }

}
