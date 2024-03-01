package rpg.project.lib.builtins;

import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;

/**Contains default factories for translating 
 * {@link net.minecraftforge.eventbus.api.Event Event}s 
 * to {@link EventContext}s for use by declarations in 
 * {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories {	
	public static final EventListenerSpecification<BlockEvent.BreakEvent> BLOCK_BREAK = new EventListenerSpecification<>(
			Reference.resource("break_block"),
			EventPriority.LOWEST,
			BlockEvent.BreakEvent.class,
			event -> true,
			event -> EventContext.build(ObjectType.BLOCK, RegistryUtil.getId(event.getState()), event.getPlayer())
				.withPos(event.getPos()).build(),
			EventFactories::fullCancel,
			(event, vars) -> {});
	
	public static final EventListenerSpecification<BlockEvent.EntityPlaceEvent> BLOCK_PLACE = new EventListenerSpecification<>(
			Reference.resource("place_block"), 
			EventPriority.LOWEST, 
			BlockEvent.EntityPlaceEvent.class,
			event -> event.getEntity() instanceof Player,
			event -> EventContext.build(ObjectType.BLOCK, RegistryUtil.getId(event.getState()), (Player)event.getEntity())
				.withPos(event.getPos()).build(),
			EventFactories::fullCancel,
			(event, vars) -> {});
	
	public static void fullCancel(ICancellableEvent event, EventListenerSpecification.CancellationType cancelType) {
		event.setCanceled(true);
	}
}
