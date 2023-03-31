package rpg.project.lib.api.events;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;

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
		BiConsumer<T, CancellationType> cancellationCallback) {
	
	public static enum CancellationType {
		/**Passed to indicate no cancelation should apply.*/
		NONE,
		/**Cancels the entire event.*/
		EVENT,
		/**Cancels any item-specific behavior but not the entire event.
		 * An exmaple of this would be a right-click action where the
		 * item activation is being prevented but not the block activation.*/
		ITEM,
		/**Cancels any block-specific behavior but not the entire event.
		 * An exmaple of this woudl be a right-click action where the 
		 * block activation is being prevented but not the item activation.*/
		BLOCK;
		
		public static CancellationType resolve(Set<CancellationType> set) {
			return set.contains(CancellationType.EVENT) ? CancellationType.EVENT
					: set.contains(CancellationType.ITEM) ? CancellationType.ITEM
						: set.contains(CancellationType.BLOCK) ? CancellationType.BLOCK
							: CancellationType.NONE;
		}
	}
}
