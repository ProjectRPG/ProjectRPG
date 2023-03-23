package rpg.project.lib.internal;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import rpg.project.lib.internal.util.Functions;

/**<p>This class bridges the gap between various systems within Project RPG.
 * Methods within this class connect these distinct systems without 
 * polluting the features themselves with content that is not true to their
 * purpose.</p>  
 * <p>This class also allows for client and server to have their own copies
 * of both the data itself and the logic.  Using this approach Core can
 * be invoked in side-sensitive contexts and not violate any cross-side
 * boundaries.</p>
 */
public class Core {
	private static final Map<LogicalSide, Function<LogicalSide, Core>> INSTANCES = Map.of(
			LogicalSide.CLIENT, Functions.memoize(Core::new), 
			LogicalSide.SERVER, Functions.memoize(Core::new));
	private final LogicalSide side;
	
	private Core(LogicalSide side) {
		this.side = side;
	}
	
	public static Core get(final LogicalSide side) {
	    return INSTANCES.get(side).apply(side);
	}
	public static Core get(final Level level) {
	    return get(level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
	}
	
	public LogicalSide getSide() {return side;}
}