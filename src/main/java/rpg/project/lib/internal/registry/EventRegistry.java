package rpg.project.lib.internal.registry;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.feature.Feature;
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
		EventContext context = spec.contextFactory().apply(event);
		//exit if this event is not situation-applicable for the eventID and specification.
		if (!spec.validEventContext().test(context))
			return;

		Core core = Core.get(context.getLevel());
		ResourceLocation eventID = spec.registryID();
		MsLoggy.DEBUG.log(LOG_CODE.EVENT, "Firing Event: {} with Context: {}", eventID, context);
		//Process EVENT gates
		CancellationType eventCancellationStatus = GateRegistry.isEventPermitted(core, eventID, context);
		if (eventCancellationStatus != CancellationType.NONE)
			spec.cancellationCallback().accept(event, eventCancellationStatus);

		for (Feature feature : core.getFeatures().getFeaturesForContext(core, eventID, context)) {
			float gating = GateRegistry.isFeaturePermitted(core, eventID, context, feature);
			if (gating != GateRegistry.HARD_FAIL)
				feature.execution().execute(core, eventID, context, gating);
		}
				
		//Activate event-specific abilities
		for (CompoundTag config : core.getAbility().getAbilitiesForContext(core, eventID, context)) {
			ResourceLocation abilityID = ResourceLocation.parse(config.getString(AbilityUtils.TYPE));
			float gating = GateRegistry.isAbilityPermitted(context.getActor(), core, eventID, context, abilityID);
			if (gating != GateRegistry.HARD_FAIL) {
				context.setParam(AbilityUtils.REDUCE, gating);
				core.getAbilities().executeAbility(eventID, context.getActor(), config, context);
			}
		}
		
		//Execute progression awards
		core.getProgression().getProgressionToBeAwarded(core, eventID, context).forEach(
				pair -> pair.getSecond().accept(GateRegistry.isProgressionPermitted(core, eventID, context, pair.getFirst())));
		
		//Process any event modification from features, abilities, or progress
		spec.contextCallback().accept(event, context);
	}
	
	/**This is used to add listeners at the appropriate lifecycle stage.*/
	public static <T extends Event> void registerListener(EventListenerSpecification<T> spec) {
		NeoForge.EVENT_BUS.addListener(spec.priority(), true, spec.validEventClass(), event -> internalEventLogic(event, spec));
	}

	public static final DeferredRegister<EventListenerSpecification<?>> EVENTS = DeferredRegister.create(APIUtils.GAMEPLAY_EVENTS, Reference.MODID);
	//public static final Supplier<IForgeRegistry<EventListenerSpecification<?>>> REGISTRY_SUPPLIER = EVENTS.makeRegistry(RegistryBuilder::create);
	
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<BlockEvent.BreakEvent>> BREAK = register(EventFactories.BLOCK_BREAK);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<BlockEvent.EntityPlaceEvent>> PLACE = register(EventFactories.BLOCK_PLACE);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<AnvilRepairEvent>> ANVIL_REPAIR = register(EventFactories.ANVIL_REPAIR);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<PlayerEvent.BreakSpeed>> BREAK_SPEED = register(EventFactories.BREAK_SPEED);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<LivingBreatheEvent>> BREATH_CHANGE = register(EventFactories.BREATH_CHANGE);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<PlayerEvent.ItemCraftedEvent>> ITEM_CRAFTED = register(EventFactories.CRAFT);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<EnchantmentLevelSetEvent>> ITEM_ENCHANT = register(EventFactories.ENCHANT);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<BabyEntitySpawnEvent>> BREED = register(EventFactories.BREED);
	public static final DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<LivingEntityUseItemEvent.Finish>> CONSUME = register(EventFactories.CONSUME);

	public static <T extends Event> DeferredHolder<EventListenerSpecification<?>, EventListenerSpecification<T>> register(EventFactories<T> factory) {
		return EVENTS.register(factory.id, factory::getSpec);
	}
}
