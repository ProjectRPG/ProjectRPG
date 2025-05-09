package rpg.project.lib.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.gating.GateUtils.Type;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.config.readers.MergeableCodecDataManager;
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
	
	private Core(LogicalSide side) {this.side = side;}
	
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
		return CommonSetup.PROGRESSION_ADDONS.getRegistry().get().stream().toList();
	}
	@Override
	public AbilitySystem getAbility() {return abilitySys;}

	@Override
	public Optional<SubSystemConfig> getProgressionData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).progression().stream()
				.filter(config -> config.getType().equals(systemType))
				.reduce((a, b) -> (SubSystemConfig) a.combine(b));
	}

	@Override
	public Optional<SubSystemConfig> getGateData(SubSystemConfigType systemType, ObjectType type, Type gateType, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).gates().getOrDefault(gateType, List.of()).stream()
				.filter(config -> config.getType().equals(systemType))
				.reduce((a, b) -> (SubSystemConfig) a.combine(b));
	}

	@Override
	public Optional<SubSystemConfig> getAbilityData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		return loader.getData(objectID).abilities().stream()
				.filter(config -> config.getType().equals(systemType))
				.reduce((a, b) -> (SubSystemConfig) a.combine(b));
	}

	@Override
	public List<SubSystemConfig> getFeatureData(ObjectType type, ResourceLocation objectID, ResourceLocation eventID) {
		MergeableCodecDataManager<?> loader = getLoader().getLoader(type);
		MainSystemConfig eventData = (MainSystemConfig) getLoader().getLoader(ObjectType.EVENT).getData(eventID).combine(loader.getData(objectID));
		return eventData.features();
	}

	//=========ABILITIES METHODS=================
	//<editor-fold>
	public void executeAbility(ResourceLocation abilityID, Player player, CompoundTag dataIn, EventContext context, ResourceLocation eventID) {
		if (player == null) return;

		Ability ability = AbilityUtils.get(player.level().registryAccess()).getAbility(abilityID);
		CompoundTag config = ability.propertyDefaults().copy().merge(dataIn);
		if (ability.start(player, config, context)) {
			this.getAbility().abilityActivationCallback(ability, config.copy(), player, context, eventID);
			tickTracker.add(new TickSchedule(ability, player, config, context, new AtomicInteger(0)));

			if (config.contains(AbilityUtils.COOLDOWN)) {
				coolTracker.add(new AbilityCooldown(abilityID, player, config, player.level().getGameTime()));
			}
		}
	}

	private record TickSchedule(Ability ability, Player player, CompoundTag src, EventContext context, AtomicInteger ticksElapsed) {
		public boolean shouldTick() {
			return src.contains(AbilityUtils.DURATION) && ticksElapsed.get() <= src.getIntOr(AbilityUtils.DURATION, 0);
		}

		public void tick() {
			ticksElapsed().getAndIncrement();
			ability.tick(player, src, context, ticksElapsed.get());
		}
	}

	private record AbilityCooldown(ResourceLocation abilityID, Player player, CompoundTag src, long lastUse) {
		public boolean cooledDown(Level level) {
			return level.getGameTime() > lastUse + src.getIntOr(AbilityUtils.COOLDOWN, 0);
		}
	}

	private final List<TickSchedule> tickTracker = new ArrayList<>();
	private final List<AbilityCooldown> coolTracker = new ArrayList<>();

	public void executeAbilityTicks(LevelTickEvent.Pre event) {
		coolTracker.removeIf(tracker -> tracker.cooledDown(event.getLevel()));
		new ArrayList<>(tickTracker).forEach(schedule -> {
			if (schedule.shouldTick()) {
				schedule.tick();
			} else {
				schedule.ability().stop(schedule.player(), schedule.src(), schedule.context());
			}
			tickTracker.remove(schedule);
		});
	}

	public boolean isAbilityCooledDown(Player player, CompoundTag src) {
		ResourceLocation abilityID = ResourceLocation.parse(src.getStringOr("ability", ""));
		return coolTracker.stream().noneMatch(cd -> cd.player().equals(player) && cd.abilityID().equals(abilityID));
	}
	//</editor-fold>

	//=========
}
