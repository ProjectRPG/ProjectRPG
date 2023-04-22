package rpg.project.lib.builtins;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.Event;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.internal.util.RegistryUtil;

/**Contains default factories for translating {@link net.minecraftforge.eventbus.api.Event Event}s 
 * to {@link EventContext}s for use by declarations in {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories {
	public static void fullCancel(Event event, EventListenerSpecification.CancellationType cancelType) {
		event.setCanceled(true);
	}
	
	public static EventContext breakBlock(BreakEvent event) {
		return EventContext.build(ObjectType.BLOCK, RegistryUtil.getId(event.getState()), event.getPlayer())
				.withPos(event.getPos()).build();
	}
	
	public static EventContext placeBlock(EntityPlaceEvent event) {
		return EventContext.build(ObjectType.BLOCK, RegistryUtil.getId(event.getState()), (Player) event.getEntity())
				.withPos(event.getPos()).build();
	}
}
