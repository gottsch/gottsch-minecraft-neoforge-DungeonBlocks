package mod.gottsch.neo.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * @author Mark Gottschling on Nov 13, 2023
 *
 */
public class CorbelBlock extends WaterloggedNonCubeFacingBlock {
    private static final VoxelShape UP_SHAPE = Block.box(0, 0, 0, 16, 16, 16D);
    private static final VoxelShape DOWN_SHAPE = Block.box(0D, 0D, 0, 16, 16, 16D);

    private static final VoxelShape NORTH_SHAPE = Shapes.or(
            Block.box(5D, 0D, 14, 11D, 16D, 16D),
            Block.box(4D, 12, 2, 12D, 15, 16D),
            Block.box(3D, 15, 1, 13D, 16, 16D)
    );
    private static final VoxelShape EAST_SHAPE = Shapes.or(
            Block.box(0, 0, 5, 2, 16D, 11),
            Block.box(0, 12, 4, 14, 15, 12),
            Block.box(0, 15, 3, 15, 16, 13));
    private static final VoxelShape SOUTH_SHAPE = Shapes.or(
            Block.box(5D, 0D, 0D, 11D, 16D, 2D),
            Block.box(4D, 12D, 0D, 12D, 15, 14),
            Block.box(3D, 15, 0D, 13D, 16, 15)
    );
    private static final VoxelShape WEST_SHAPE = Shapes.or(
            Block.box(14, 0D, 5D, 16, 16D, 11D),
            Block.box(2, 12, 4, 14, 15, 12),
            Block.box(1, 15, 3, 15, 16, 13));

    /**
     * @param properties
     */
    public CorbelBlock(Properties properties) {
        super(properties);
    }

    /**
     *
     */
    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        Direction direction = state.getValue(FACING);

        return switch (direction) {
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case EAST -> EAST_SHAPE;
            case WEST -> WEST_SHAPE;
            default -> DOWN_SHAPE;
        };
    }
}
