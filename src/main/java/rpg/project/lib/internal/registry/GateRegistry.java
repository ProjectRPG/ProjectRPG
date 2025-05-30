package rpg.project.lib.internal.registry;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.api.gating.GateSystem;
import rpg.project.lib.api.gating.GateUtils.Type;
import rpg.project.lib.internal.setup.CommonSetup;

/**This class stores and supplies a runtime map of all 
 * {@link GateSystem} implementations.*/
public class GateRegistry{
	public static final float HARD_PASS = 1f;
	public static final float HARD_FAIL = 0f;

	/**Internal method to obtain a copy of the value set for registered gate types;
	 *
	 * @param type the gate type being queried
	 * @return a new collection of the systems.
	 */
	private static Set<GateSystem> getSystems(Type type, RegistryAccess access) {
		return new HashSet<>(access.lookupOrThrow(type.key).stream().toList());
	}

	/**Returns an event-specific result for this event.  This is used
	 * by {@link EventRegistry} to inform event systems whether to 
	 * cancel the event and under what circumstances.  This function will
	 * iterate through every gating system for this {@link Type} and 
	 * return a consolidated result per the logic of {@link CancellationType#resolve(java.util.Set)}
	 * 
	 * @param core the {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @return the cancellation result
	 */
	public static CancellationType isEventPermitted(Hub core, ResourceLocation event, EventContext context) {
		var systems = getSystems(Type.EVENT, context.getLevel().registryAccess());
		if (systems.isEmpty())
			return CancellationType.NONE;
		return CancellationType.resolve(
				systems.stream()
				.map(system -> system.getCancellationResult(context, core, event, null))
				.collect(Collectors.toSet()));
	}

	/**Returns whether the event is allowed to commit progression
	 * to the progression system.  If this returns false, the 
	 * progression system will not be informed the event occurred
	 * and no progression will be tracked.
	 * 
	 * @param core the {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @param container the progression container id this event is expected to 
	 * apply progression to.  
	 * @return whether progression can be committed or not.
	 */
	public static float isProgressionPermitted(Hub core, ResourceLocation event, EventContext context, String container) {
		var systems = getSystems(Type.PROGRESS, context.getLevel().registryAccess());
		if (systems.isEmpty())
			return HARD_PASS;
		return systems.stream()
				.map(system -> system.isActionPermitted(context, core, event, container))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}

	/**Returns whether the event is valid for activating a feature.
	 * If this returns falls, the feature system will not be notified
	 * the event occurred and the feature will not execute.
	 * 
	 * @param core the {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @param featureReference the feature specification being invoked
	 * @return whether a feature is allowed to perform its function
	 */
	public static float isFeaturePermitted(Hub core, ResourceLocation event, EventContext context, Feature featureReference) {
		var systems = getSystems(Type.FEATURE, context.getLevel().registryAccess());
		if (systems.isEmpty())
			return HARD_PASS;
		return systems.stream()
				.map(system -> system.isActionPermitted(context, core, event, featureReference.featureID().toString()))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}

	/**Returns whether the event and context is valid for activating 
	 * an ability.  This occurs after the ability system has decided
	 * which abilities are applicable and evaluates each ability from
	 * that collection independently.
	 * 
	 * @param player the player using the ability
	 * @param core the {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @param ability the ability instance being activated
	 * @return whether an ability is allowed to perform its function
	 */
	public static float isAbilityPermitted(Player player, Hub core, ResourceLocation event, EventContext context, ResourceLocation ability) {
		var systems = getSystems(Type.ABILITY, context.getLevel().registryAccess());
		if (systems.isEmpty())
			return HARD_PASS;
		return systems.stream()
				.map(system -> system.isActionPermitted(context, core, event, ability.toString()))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}
}
