package mod.gottsch.neo.dungeonblocks.core.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author Mark Gottschling on Nov 6, 2023
 *
 */
public class _KeystoneSlabBlock extends NonCubeFacingBlock {
    private static final VoxelShape AABB = Block.box(0.0D, 8D, 0D, 16.0D, 16.0D, 16.0D);

    public _KeystoneSlabBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }
}
