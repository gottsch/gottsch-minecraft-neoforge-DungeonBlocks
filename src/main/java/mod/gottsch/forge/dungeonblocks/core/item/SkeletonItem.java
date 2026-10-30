package mod.gottsch.forge.dungeonblocks.core.item;

import mod.gottsch.forge.dungeonblocks.core.block.SkeletonBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

/**
 * @author Mark Gottschling on Feb 2, 2019
 *
 */
public class SkeletonItem extends BlockItem {

	public SkeletonItem(Block block, Properties properties) {
		super(block, properties); //.stacksTo(MAX_STACK_SIZE));
	}

	@Override
	protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
		BlockPos blockPos = context.getClickedPos().relative(state.getValue(SkeletonBlock.FACING).getOpposite());
		BlockState target = context.getLevel().getBlockState(blockPos);
		if (target.isAir() || target.canBeReplaced()) {
			return context.getLevel().setBlock(context.getClickedPos(), state, 26);
		}
		return false;
	}
}