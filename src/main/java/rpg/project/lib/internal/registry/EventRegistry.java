package rpg.project.lib.internal.registry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.api.events.conditions.EventConditionAnd;
import rpg.project.lib.api.events.conditions.EventConditionAny;
import rpg.project.lib.api.events.conditions.EventConditionEntityMatches;
import rpg.project.lib.api.events.conditions.EventConditionNot;
import rpg.project.lib.api.events.conditions.EventConditionType;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.builtins.EventFactories;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

public class EventRegistry {
	/**<p>A standardized method used by {@link EventRegistry#registerListener(EventProvider) registerListener}
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
		if (context.getActor() == null || !spec.validEventContext().test(context))
			return;

		Core core = Core.get(context.getLevel());
		ResourceLocation eventID = spec.registryID();
		MsLoggy.DEBUG.log(LOG_CODE.EVENT, "Firing Event: {} with Context: {}", eventID, context);
		//Process EVENT gates
		CancellationType eventCancellationStatus = GateRegistry.isEventPermitted(core, eventID, context);
		if (eventCancellationStatus != CancellationType.NONE)
			spec.cancellationCallback().accept(event, eventCancellationStatus);

		for (Feature feature : core.getFeaturesForContext(eventID, context)) {
			float gating = GateRegistry.isFeaturePermitted(core, eventID, context, feature);
			if (gating != GateRegistry.HARD_FAIL)
				feature.execution().execute(core, eventID, context, gating);
		}
				
		//Activate event-specific abilities
		for (CompoundTag config : core.getAbility().getAbilitiesForContext(core, eventID, context)) {
			ResourceLocation abilityID = ResourceLocation.parse(config.getStringOr(AbilityUtils.TYPE, ""));
			float gating = GateRegistry.isAbilityPermitted(context.getActor(), core, eventID, context, abilityID);
			if (gating != GateRegistry.HARD_FAIL) {
				context.setParam(AbilityUtils.REDUCE, gating);
				core.executeAbility(abilityID, context.getActor(), config, context, eventID);
			}
		}
		
		//Execute progression awards
		core.getProgression().getProgressionToBeAwarded(core, eventID, context).forEach(
				pair -> pair.getSecond().accept(GateRegistry.isProgressionPermitted(core, eventID, context, pair.getFirst())));
		
		//Process any event modification from features, abilities, or progress
		spec.contextCallback().accept(event, context);
	}
	
	/**This is used to add listeners at the appropriate lifecycle stage.*/
	public static <T extends Event> void registerListener(EventProvider<T> provider) {
		provider.getListenerSpecifications().forEach(spec ->
			NeoForge.EVENT_BUS.addListener(spec.priority(), true, spec.validEventClass(), event -> internalEventLogic(event, spec))
		);
	}

	public static final DeferredRegister<EventProvider<?>> EVENTS = DeferredRegister.create(APIUtils.GAMEPLAY_EVENTS, Reference.MODID);

	static {
		EventFactories.registerEvents(EVENTS);
	}
}
