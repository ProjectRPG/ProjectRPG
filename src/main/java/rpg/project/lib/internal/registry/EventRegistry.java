package rpg.project.lib.internal.registry;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.BlockEvent.BreakEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.builtins.EventFactories;
import rpg.project.lib.internal.util.Reference;

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
		//TODO handle things like gates, progression, abilities, and feature hooks
	}
	
	/**This is used to add listeners at the appropriate 
	 * lifecycle stage.  Calling this in your own mod will create duplication.
	 */
	public static <T extends Event> void registerListener(EventListenerSpecification<T> spec) {
		MinecraftForge.EVENT_BUS.addListener(spec.priority(), true, spec.validEventClass(), event -> internalEventLogic(event, spec));
	}
	
	
	public static final DeferredRegister<EventListenerSpecification<?>> EVENTS = DeferredRegister.create(APIUtils.GAMEPLAY_EVENTS, Reference.MODID);
	public static final Supplier<IForgeRegistry<EventListenerSpecification<?>>> REGISTRY_SUPPLIER = EVENTS.makeRegistry(RegistryBuilder::new);
	
	RegistryObject<EventListenerSpecification<BreakEvent>> BREAK = EVENTS.register("break_block", () -> new EventListenerSpecification<BreakEvent>(rl("break_block"), EventPriority.LOWEST ,BreakEvent.class, EventFactories::breakBlock));
	
	private static ResourceLocation rl(String path) {return new ResourceLocation(Reference.MODID, path);}
}
