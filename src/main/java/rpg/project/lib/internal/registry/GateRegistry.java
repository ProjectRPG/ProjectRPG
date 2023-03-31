package rpg.project.lib.internal.registry;

import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.ibm.icu.impl.locale.XCldrStub.HashMultimap;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.gating.GateSystem;

public class GateRegistry{
	public static void register(GateSystem<?> system, Type type) {
		Preconditions.checkNotNull(system);
		registeredSystems.put(type, system);
	}
	
	/**Gameplay gating is divided into four types.  {@link GateSystem}
	 * registrations specify their applicable {@link Type} so the 
	 * internal gating processor can apply them accordingly.  
	 */
	public static enum Type{
		/**Specifies gates that cancel or alter events.*/
		EVENT,
		/**Specifies gates that police progression advancement.*/
		PROGRESS,
		/**Specifies gates that permit/deny feature usage*/
		FEATURE,
		/**Specifies gates that permit/deny ability usage*/
		ABILITY;
	}
	private static final HashMultimap<Type, GateSystem<?>> registeredSystems = HashMultimap.create();

	public static CancellationType isEventPermitted(Hub core, ResourceLocation event, EventContext context) {
		return CancellationType.resolve(
				registeredSystems.get(Type.EVENT).stream()
				.map(system -> system.getCancellationResult(context, core, event, null))
				.collect(Collectors.toSet()));
	}

	@SuppressWarnings("unchecked")
	public static boolean isProgressionPermitted(Hub core, ResourceLocation event, EventContext context, String container) {
		return registeredSystems.get(Type.PROGRESS).stream()
				.allMatch(system -> ((GateSystem<String>)system).isActionPermitted(context, core, event, container));
	}

	@SuppressWarnings("unchecked")
	public static boolean isFeaturePermitted(Hub core, ResourceLocation event, EventContext context,	Object featureReferenceWIP) {
		return registeredSystems.get(Type.FEATURE).stream()
				.allMatch(system -> ((GateSystem<Object>)system).isActionPermitted(context, core, event, featureReferenceWIP));
	}

	@SuppressWarnings("unchecked")
	public static boolean isAbilityPermitted(Player player, Hub core, ResourceLocation event, EventContext context,	Ability ability) {
		return registeredSystems.get(Type.ABILITY).stream()
				.allMatch(system -> ((GateSystem<Ability>)system).isActionPermitted(context, core, event, ability));
	}
}
