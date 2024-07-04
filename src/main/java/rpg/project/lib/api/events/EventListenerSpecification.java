package rpg.project.lib.api.events;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;

/**Used by the gameplay event system to register event listeners for various
 * events watched by the library.  
 *
 * @param <T> The event type this listener is applicable for
 */
public record EventListenerSpecification<T extends Event>(
		/**A Unique identifier used for querying features for relevant logic.*/
		ResourceLocation registryID,
		/**@see EventPriority*/
		EventPriority priority,
		/**The event class this applies to.*/
		Class<T> validEventClass,
		/**Fired first when an event is invoked, this predicate determines
		 * if ProjectRPG should internally consider this event as having fired.
		 * This discrimination allows two ProjectRPG events which listen to the
		 * same Forge event to execute RPG logic under different circumstances.*/
		Predicate<EventContext> validEventContext,
		/**This consumes the valid event and returns the necessary context 
		 * values needed to perform standard behavior within the gameplay
		 * event system*/
		Function<T, EventContext> contextFactory,
		/**<p>Used by other systems (specifically gating) to specify a context
		 * of cancellation so that specific aspects of an event can be 
		 * cancelled without affecting unintended elements.</p>
		 * <p>NOTE: the event will always be provided by the event registry in
		 * a safe manner and never by the sub-system.  Type checking of the
		 * event in your consumer will be redundant.*/
		BiConsumer<T, CancellationType> cancellationCallback,
		/**<p>After other systems have executed their event-specific behavior,
		 * there may be information about the event which features expect to
		 * modify. The original values should be supplied by the contextFactory
		 * at the event's firing and are subsequently consumed here.</p>*/
		BiConsumer<T, EventContext> contextCallback) {
	
	public static enum CancellationType {
		/**Passed to indicate no cancellation should apply.*/
		NONE,
		/**Cancels the entire event.*/
		EVENT,
		/**Cancels any item-specific behavior but not the entire event.
		 * An example of this would be a right-click action where the
		 * item activation is being prevented but not the block activation.*/
		ITEM,
		/**Cancels any block-specific behavior but not the entire event.
		 * An example of this would be a right-click action where the 
		 * block activation is being prevented but not the item activation.*/
		BLOCK;
		
		/**<p>Accepts a set of {@link CancellationType}s and returns the type
		 * with the highest internal priority. Priority Order:</p><ol>
		 * <li>{@link CancellationType#EVENT EVENT}</li>
		 * <li>{@link CancellationType#ITEM ITEM}</li>
		 * <li>{@link CancellationType#BLOCK BLOCK}</li>
		 * <li>{@link CancellationType#NONE NONE}</li></ol>
		 * 
		 * @param set collection of types to be priority filtered
		 * @return the highest order priority.
		 */
		public static CancellationType resolve(Set<CancellationType> set) {
			return set.contains(CancellationType.EVENT) ? CancellationType.EVENT
					: set.contains(CancellationType.ITEM) ? CancellationType.ITEM
						: set.contains(CancellationType.BLOCK) ? CancellationType.BLOCK
							: CancellationType.NONE;
		}
	}
}
