package rpg.project.lib.internal.registry;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.IExtensibleEnum;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.gating.GateSystem;

/**This class stores and supplies a runtime map of all 
 * {@link GateSystem} implementations.  
 * 
 * TODO determine if this is viable as a static class.
 * It might make more sense as an instance class in Core
 * such that state variables for each system can be 
 * maintianed in a side-safe way.  This may not be necessary
 * but should be considered before removing this comment. 
 * 
 * @author Caltinor
 *
 */
public class GateRegistry{
	/**==INTERNAL USE ONLY==
	 * This is meant only to be called by API methods in a way that 
	 * checks the type safety of the {@link GateSystem} sub-type 
	 * before invoking this.
	 * 
	 * @param system a GateSystem implementation
	 * @param type the applicable gating type for the system.
	 */
	public static void register(GateSystem<?> system, Type type) {
		Preconditions.checkNotNull(system);
		registeredSystems.put(type, system);
	}
	
	/**Gameplay gating is divided into four types.  {@link GateSystem}
	 * registrations specify their applicable {@link Type} so the 
	 * internal gating processor can apply them accordingly.  
	 */
	public static enum Type implements StringRepresentable, IExtensibleEnum{
		/**Specifies gates that cancel or alter events.*/
		EVENT,
		/**Specifies gates that police progression advancement.*/
		PROGRESS,
		/**Specifies gates that permit/deny feature usage*/
		FEATURE,
		/**Specifies gates that permit/deny ability usage*/
		ABILITY;
		
		public static final Codec<Type> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(Type::values, Type::create);
		private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getSerializedName, s -> s));
		public static Type create(String name) {return BY_NAME.get(name);} 
		
		@Override
		public String getSerializedName() {return this.name();}
	}
	private static final HashMultimap<Type, GateSystem<?>> registeredSystems = HashMultimap.create();

	/**Returns an event-specific result for this event.  This is used
	 * by {@link EventRegistry} to inform event systems whether to 
	 * cancel the event and under what circumstances.  This function will
	 * iterate through every gating system for this {@link Type} and 
	 * return a consolidated result per the logic of {@link CancellationType#resolve(java.util.Set)}
	 * 
	 * @param core the {@link Hub} instance to be used by consumers
	 * @param event the event ID used to get the correct callbacks
	 * @param context event information provded to the gate system for evaluation
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
	 * @param context event information provded to the gate system for evaluation
	 * @param container the progression container id this event is expected to 
	 * apply progression to.  
	 * @return whether progression can be committed or not.
	 */
	@SuppressWarnings("unchecked")
	public static boolean isProgressionPermitted(Hub core, ResourceLocation event, EventContext context, String container) {		
		return registeredSystems.get(Type.PROGRESS).isEmpty() || registeredSystems.get(Type.PROGRESS).stream()
				.allMatch(system -> ((GateSystem<String>)system).isActionPermitted(context, core, event, container));
	}

	/**TODO finish docs
	 * 
	 * @param core
	 * @param event
	 * @param context
	 * @param featureReferenceWIP
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isFeaturePermitted(Hub core, ResourceLocation event, EventContext context,	Object featureReferenceWIP) {
		return registeredSystems.get(Type.FEATURE).isEmpty() || registeredSystems.get(Type.FEATURE).stream()
				.allMatch(system -> ((GateSystem<Object>)system).isActionPermitted(context, core, event, featureReferenceWIP));
	}

	/**TODO finish docs
	 * 
	 * @param player
	 * @param core
	 * @param event
	 * @param context
	 * @param ability
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isAbilityPermitted(Player player, Hub core, ResourceLocation event, EventContext context,	Ability ability) {
		return registeredSystems.get(Type.ABILITY).isEmpty() || registeredSystems.get(Type.ABILITY).stream()
				.allMatch(system -> ((GateSystem<Ability>)system).isActionPermitted(context, core, event, ability));
	}
}
