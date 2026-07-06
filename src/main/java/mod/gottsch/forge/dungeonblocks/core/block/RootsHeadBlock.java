package mod.gottsch.forge.dungeonblocks.core.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

/**
 * @author by Mark Gottschling on 10/29/2025
 */
public class RootsHeadBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<RootsHeadBlock> CODEC = simpleCodec(RootsHeadBlock::new);
    protected static final VoxelShape SHAPE = Block.box(4.0D, 9.0D, 4.0D, 12.0D, 16.0D, 12.0D);

    public RootsHeadBlock(Properties properties) {
        super(properties, Direction.DOWN, SHAPE, false, 0.1D);
    }

    @Override
    protected MapCodec<RootsHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        double d0 = 1.0D;

        int i;
        for(i = 0; random.nextDouble() < d0; ++i) {
            d0 *= 0.826D;
        }
        return i;
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return ModBlocks.ROOTS_BODY.get();
    }
}
