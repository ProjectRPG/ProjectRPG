package rpg.project.lib.internal.registry;

import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.builtins.EventFactories;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

public class EventRegistry {
	/**<p>A standardized method used by {@link EventRegistry#registerListener(EventListenerSpecification) registerListener}
	 * to register event listeners to the forge event bus.</p>
	 * <p>Internally this applies the gating, progression, ability, and feature hook logic
	 * for every registered event.  Each system is independently queried for configurations
	 * related to the eventSpecID for which this consumer is registered.</p>
	 *
	 * @param <T> a NeoForge {@link Event} sub-class
	 * @param event the event instance being consumed
	 */
	public static <T extends Event> void internalEventLogic(T event, EventListenerSpecification<T> spec) {
		//exit if this event is not situationally applicable for the eventID and specification.
		if (!spec.validEventContext().test(event))
			return;
		//TODO replace this with a context registry.
		/* The context registry stores functions for getting specific data from an event
		 * which should be read/exposed for use in other functionality.  This design is
		 * not yet defined, but conceptually works to only gather contextual data needed
		 * by downstream event consumers.  This would also mean that events with no listeners
		 * or underlying logic would effectively execute no context or listeners and thus
		 * be more performant.
		 */
		EventContext context = spec.contextFactory().apply(event);
		Core core = Core.get(context.level());
		ResourceLocation eventID = spec.registryID();
		MsLoggy.DEBUG.log(LOG_CODE.EVENT, "Firing Event: {} with Context: {}", eventID, context);
		//Process EVENT gates
		CancellationType eventCancellationStatus = GateRegistry.isEventPermitted(core, eventID, context);
		if (eventCancellationStatus != CancellationType.NONE)
			spec.cancellationCallback().accept(event, eventCancellationStatus);
		
		/*TODO Feature Gates
		 * for (Object thing : system.getThings(hub, spec.registryID(), context)) {
		 * 		if (GateRegistry.isThingPermitted(hub, spec.registryID(), context, thing))
		 * 			system.executeThing(hub, spec.registryID(), context, thing);
		 * }
		 */
				
		//Activate event-specific abilities
		for (CompoundTag config : core.getAbility().getAbilitiesForContext(core, eventID, context)) {
			ResourceLocation abilityID = ResourceLocation.parse(config.getString(AbilityUtils.TYPE));
			float gating = GateRegistry.isAbilityPermitted(context.actor(), core, eventID, context, abilityID);
			if (gating != GateRegistry.HARD_FAIL) {
				CompoundTag consolidatedInputData = config.copy().merge(context.dynamicVariables());
				consolidatedInputData.putFloat(AbilityUtils.REDUCTION, gating);
				core.getAbilities().executeAbility(eventID, context.actor(), consolidatedInputData);
			}
		}
		
		//Execute progression awards
		core.getProgression().getProgressionToBeAwarded(core, eventID, context).forEach(
				pair -> pair.getSecond().accept(GateRegistry.isProgressionPermitted(core, eventID, context, pair.getFirst())));
		
		//Process any event modificaiton from features, abilities, or progress
		spec.dynamicVariableConsumer().accept(event, context.dynamicVariables());
	}
	
	/**This is used to add listeners at the appropriate lifecycle stage.*/
	public static <T extends Event> void registerListener(EventListenerSpecification<T> spec) {
		NeoForge.EVENT_BUS.addListener(spec.priority(), true, spec.validEventClass(), event -> internalEventLogic(event, spec));
	}

	public static final DeferredRegister<EventListenerSpecification<?>> EVENTS = DeferredRegister.create(APIUtils.GAMEPLAY_EVENTS, Reference.MODID);
	//public static final Supplier<IForgeRegistry<EventListenerSpecification<?>>> REGISTRY_SUPPLIER = EVENTS.makeRegistry(RegistryBuilder::create);
	
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<BlockEvent.BreakEvent>> BREAK = EVENTS.register("break_block", () -> EventFactories.BLOCK_BREAK);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<BlockEvent.EntityPlaceEvent>> PLACE = EVENTS.register("place_block",	() -> EventFactories.BLOCK_PLACE);
}
