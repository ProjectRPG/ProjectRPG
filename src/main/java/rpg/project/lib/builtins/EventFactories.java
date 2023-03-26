package rpg.project.lib.builtins;

import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import rpg.project.lib.api.events.EventContext;

/**Contains default factories for translating {@link net.minecraftforge.eventbus.api.Event Event}s 
 * to {@link EventContext}s for use by declarations in {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories {
	public static EventContext breakBlock(BreakEvent event) {
		return new EventContext(event.getPlayer());
	}
}
