/**
 * 
 */
package mod.gottsch.neo.dungeonblocks.core.state.properties;

import net.minecraft.util.StringRepresentable;

/**
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
