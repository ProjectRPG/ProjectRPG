package rpg.project.lib.internal.registry;

import java.util.Comparator;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.gating.GateSystem;
import rpg.project.lib.api.gating.GateUtils.Type;

/**This class stores and supplies a runtime map of all 
 * {@link GateSystem} implementations.*/
public class GateRegistry{
	public static final float HARD_PASS = 1f;
	public static final float HARD_FAIL = 0f;
	/**==INTERNAL USE ONLY==
	 * This is meant only to be called by API methods in a way that 
	 * checks the type safety of the {@link GateSystem} sub-type 
	 * before invoking this.
	 * 
	 * @param system a GateSystem implementation
	 * @param type the applicable gating type for the system.
	 */
	public static void register(GateSystem system, Type type) {
		Preconditions.checkNotNull(system);
		registeredSystems.put(type, system);
	}
	
	private static final HashMultimap<Type, GateSystem> registeredSystems = HashMultimap.create();

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
		if (registeredSystems.get(Type.EVENT).isEmpty())
			return CancellationType.NONE;
		return CancellationType.resolve(
				registeredSystems.get(Type.EVENT).stream()
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
		if (registeredSystems.get(Type.PROGRESS).isEmpty())
			return HARD_PASS;
		return registeredSystems.get(Type.PROGRESS).stream()
				.map(system -> system.isActionPermitted(context, core, event, container))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}

	/**Returns whether the event is valid for activating a feature.
	 * If this returns falls, the feature system will not be notified
	 * the event occurred and the feature will not execute.
	 * 
	 * @param corethe {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @param featureReferenceWIP
	 * @return whether a feature is allowed to perform its function
	 */
	public static float isFeaturePermitted(Hub core, ResourceLocation event, EventContext context, String featureReference) {
		if (registeredSystems.get(Type.FEATURE).isEmpty())
			return HARD_PASS;
		return registeredSystems.get(Type.FEATURE).stream()
				.map(system -> system.isActionPermitted(context, core, event, featureReference))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}

	/**Returns whether the event is valid for activating an ability.
	 * If this returns falls, the ability system will not be notified
	 * the event occurred and the ability will not activate.
	 * 
	 * @param player the player using the ability
	 * @param corethe {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provided to the gate system for evaluation
	 * @param ability the ability instance being activated
	 * @return whether an ability is allowed to perform its function
	 */
	public static float isAbilityPermitted(Player player, Hub core, ResourceLocation event, EventContext context,	ResourceLocation ability) {
		if (registeredSystems.get(Type.ABILITY).isEmpty())
			return HARD_PASS;
		return registeredSystems.get(Type.ABILITY).stream()
				.map(system -> system.isActionPermitted(context, core, event, ability.toString()))
				.min(Comparator.naturalOrder())
				.orElse(HARD_PASS);
	}
}
