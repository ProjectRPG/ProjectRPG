package rpg.project.lib.internal;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.gating.GateUtils.Type;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.config.readers.MergeableCodecDataManager;
import rpg.project.lib.internal.registry.AbilityRegistry;
import rpg.project.lib.internal.registry.FeatureRegistry;
import rpg.project.lib.internal.registry.ProgressionAddonRegistry;
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
	private final DataLoader loader = new DataLoader();
	private final PartySystem party = CommonSetup.partySupplier.get();
	private final ProgressionSystem<?> progress = CommonSetup.progressionSupplier.get();
	private final AbilitySystem abilitySys = CommonSetup.abilitySupplier.get();
	private final AbilityRegistry abilities;
	private final FeatureRegistry features;
	
	private Core(LogicalSide side) {
		this.side = side;
		this.abilities = new AbilityRegistry();
		this.features = new FeatureRegistry();
	}
	
	public static Core get(final LogicalSide side) {
	    return INSTANCES.get(side).apply(side);
	}
	public static Core get(final LevelAccessor level) {
	    return get(level.isClientSide() ? LogicalSide.CLIENT : LogicalSide.SERVER);
	}
	
	public LogicalSide getSide() {return side;}
	public DataLoader getLoader() {return loader;}
	@Override
	public PartySystem getParty() {return party;}
	@Override
	public ProgressionSystem<?> getProgression() {return progress;}
	@Override
	public List<ProgressionAddon> getProgressionAddons() {
		return ProgressionAddonRegistry.getAddons();
	}
	@Override
	public AbilitySystem getAbility() {return abilitySys;}
	@Override
	public AbilityRegistry getAbilities() { return abilities; }
	@Override
	public FeatureRegistry getFeatures() {return features;}

	@Override
	public Optional<SubSystemConfig> getProgressionData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).progression().stream().filter(config -> config.getType().equals(systemType)).findFirst();
	}

	@Override
	public Optional<SubSystemConfig> getGateData(SubSystemConfigType systemType, ObjectType type, Type gateType, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).gates().getOrDefault(gateType, List.of()).stream().filter(config -> config.getType().equals(systemType)).findFirst();
	}

	@Override
	public Optional<SubSystemConfig> getAbilityData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).abilities().stream().filter(config -> config.getType().equals(systemType)).findFirst();
	}

	@Override
	public Optional<SubSystemConfig> getFeatureData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).features().stream().filter(config -> config.getType().equals(systemType)).findFirst();
	}
}
