package rpg.project.lib.internal;

import java.util.Map;
import java.util.function.Function;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.internal.registry.AbilityRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
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
public class Core implements Hub {
	private static final Map<LogicalSide, Function<LogicalSide, Core>> INSTANCES = Map.of(
			LogicalSide.CLIENT, Functions.memoize(Core::new), 
			LogicalSide.SERVER, Functions.memoize(Core::new));
	private final LogicalSide side;
	private final PartySystem party = CommonSetup.partySupplier.get();
	private final ProgressionSystem<?> progress = CommonSetup.progressionSupplier.get();
	private final AbilityRegistry abilities;
	
	private Core(LogicalSide side) {
		this.side = side;
		this.abilities = new AbilityRegistry();
	}
	
	//TODO find a way to prevent these from being called too early.
	// perhaps a lifecycle state check or some sort of "finished setup"
	// flag that gets set by prerequisite behavior.
	public static Core get(final LogicalSide side) {
	    return INSTANCES.get(side).apply(side);
	}
	public static Core get(final Level level) {
	    return get(level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
	}
	
	public LogicalSide getSide() {return side;}
	@Override
	public PartySystem getParty() {return party;}
	@Override
	public ProgressionSystem<?> getProgression() {return progress;}
	public AbilityRegistry getAbilityRegistry() { return abilities; }
}
