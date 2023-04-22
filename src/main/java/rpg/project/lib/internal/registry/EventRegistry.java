package rpg.project.lib.internal.registry;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.event.level.BlockEvent.EntityPlaceEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.builtins.EventFactories;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

public class EventRegistry {
	/**<p>A standardized method used by {@link EventListenerSpecification#registerListener()}
	 * to register event listeners to the forge event bus.</p>
	 * <p>Internally this applies the gating, progression, ability, and feature hook logic
	 * for every registered event.  Each system is independently queried for configurations
	 * related to the eventSpecID for which this consumer is registered.</p>
	 *
	 * @param <T> a Forge {@link Event} sub-class
	 * @param eventSpecID the registry name for the event this should supply to features
	 * @param event the event instance being consumed
	 * @param contextFactory creates a new {@link EventContext} from the event instance
	 */
	public static <T extends Event> void internalEventLogic(T event, EventListenerSpecification<T> spec) {
		EventContext context = spec.contextFactory().apply(event);
		Core core = Core.get(context.level());
		ResourceLocation eventID = spec.registryID();
		MsLoggy.DEBUG.log(LOG_CODE.EVENT, "Firing Event: {} with Context: {}", eventID, context);
		//Process EVENT gates
		CancellationType eventCancellationStatus = GateRegistry.isEventPermitted(core, eventID, context);
		if (eventCancellationStatus != CancellationType.NONE)
			spec.cancellationCallback().accept(event, eventCancellationStatus);
		
		/* These should follow the pattern of this pseudo
		 * 
		 * for (Object thing : system.getThings(hub, spec.registryID(), context)) {
		 * 		if (GateRegistry.isThingPermitted(hub, spec.registryID(), context, thing))
		 * 			system.executeThing(hub, spec.registryID(), context, thing);
		 * }
		 */
		//TODO Feature Gates
		//TODO Ability Gates
		core.getProgression().getProgressionToBeAwarded(core, eventID, context).forEach(pair -> {
			if (GateRegistry.isProgressionPermitted(core, eventID, context, pair.getFirst()))
				pair.getSecond().run();
		});
	}
	
	/**This is used to add listeners at the appropriate
	 * lifecycle stage.  Calling this in your own mod will create duplication.
	 */
	public static <T extends Event> void registerListener(EventListenerSpecification<T> spec) {
		MinecraftForge.EVENT_BUS.addListener(spec.priority(), true, spec.validEventClass(), event -> internalEventLogic(event, spec));
	}
	
	public static final DeferredRegister<EventListenerSpecification<?>> EVENTS = DeferredRegister.create(APIUtils.GAMEPLAY_EVENTS, Reference.MODID);
	public static final Supplier<IForgeRegistry<EventListenerSpecification<?>>> REGISTRY_SUPPLIER = EVENTS.makeRegistry(RegistryBuilder::new);
	
	public static final RegistryObject<EventListenerSpecification<BreakEvent>> BREAK = EVENTS.register("break_block",
		() -> new EventListenerSpecification<>(Reference.resource("break_block"), EventPriority.LOWEST, BreakEvent.class, EventFactories::breakBlock, EventFactories::fullCancel));
	
	public static final RegistryObject<EventListenerSpecification<EntityPlaceEvent>> PLACE = EVENTS.register("place_block", 
		() -> new EventListenerSpecification<>(Reference.resource("break_block"), EventPriority.LOWEST, EntityPlaceEvent.class, EventFactories::placeBlock, EventFactories::fullCancel));
}
