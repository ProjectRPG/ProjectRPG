package rpg.project.lib.api.events;

import java.util.function.Function;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import rpg.project.lib.internal.registry.EventRegistry;

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
		Function<T, EventContext> contextFactory) {
	
	/**===INTERNAL ONLY===
	 * 
	 * this method is used internally to add listeners at the appropriate 
	 * lifecycle stage.  Calling this in your own mod will create duplication.
	 */
	public void registerListener() {
		MinecraftForge.EVENT_BUS.addListener(priority(), true, validEventClass(), event -> EventRegistry.internalEventLogic(registryID, event, contextFactory()));
	}
}
