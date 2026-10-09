package mod.gottsch.forge.dungeonblocks.core.state.properties;

import net.minecraft.util.StringRepresentable;

/**
 * The shape a facade-family block takes where a wall run turns a corner.
 *
 * <p>LEFT and RIGHT are <b>relative to the block's FACING</b>, as vanilla
 * {@code StairsShape} defines them: RIGHT is the clockwise side of the facing
 * direction, LEFT the counter-clockwise side. Before 3.0.0 they named absolute
 * compass sides, which only worked for north/south-facing runs.
 *
 * @author Mark Gottschling on Jan 12, 2020
 *
 */
public enum FacadeShape implements StringRepresentable {
    STRAIGHT("straight"),
    INNER_LEFT("inner_left"),
    INNER_RIGHT("inner_right"),
    OUTER_LEFT("outer_left"),
    OUTER_RIGHT("outer_right");
    private final String name;

    private FacadeShape(String name) {
        this.name = name;
    }

    @Override
	public String toString() {
        return this.name;
    }

	public String getName() {
        return this.name;
    }

	@Override
	public String getSerializedName() {
		return getName();
	}
}
