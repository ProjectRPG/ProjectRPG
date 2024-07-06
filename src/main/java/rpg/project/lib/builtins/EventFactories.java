package rpg.project.lib.builtins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.function.Function;

/**Contains default factories for translating 
 * {@link net.minecraftforge.eventbus.api.Event Event}s 
 * to {@link EventContext}s for use by declarations in 
 * {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories<T extends Event> {
	public static final EventFactories<AnvilRepairEvent> ANVIL_REPAIR = new EventFactories<>("anvil_repair", id -> new EventListenerSpecification<>(
			Reference.resource(id),
			EventPriority.LOWEST,
			AnvilRepairEvent.class,
			context -> true,
			event -> EventContext.build(RegistryUtil.getId(event.getOutput()), EventContext.ITEMSTACK, event.getOutput(), event.getEntity(), event.getEntity().level()).create(),
			(e, v) -> {},
			(event, vars) -> {}
	));
	public static final EventFactories<BlockEvent.BreakEvent> BLOCK_BREAK = new EventFactories<>("break_block", id -> new EventListenerSpecification<>(
			Reference.resource(id),
			EventPriority.LOWEST,
			BlockEvent.BreakEvent.class,
			context -> context.getParam(LootContextParams.THIS_ENTITY) instanceof Player,
			event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getPlayer(), event.getLevel())
					.withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
			EventFactories::fullCancel,
			(event, vars) -> {}
	));
	public static final EventFactories<PlayerEvent.BreakSpeed> BREAK_SPEED = new EventFactories<>("break_speed", id -> new EventListenerSpecification<>(
			Reference.resource(id),
			EventPriority.LOWEST,
			PlayerEvent.BreakSpeed.class,
			context -> true,
			event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getEntity(), event.getEntity().level())
					.withParam(LootContextParams.ORIGIN, event.getPosition().orElse(BlockPos.ZERO).getCenter()).create(),
			EventFactories::fullCancel,
			(event, context) -> {
				if (context.hasParam(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE))
					event.setNewSpeed(context.getParam(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE));
			}
	));
	public static final EventFactories<BlockEvent.EntityPlaceEvent> BLOCK_PLACE = new EventFactories<>("place_block", id -> new EventListenerSpecification<>(
            Reference.resource(id),
            EventPriority.LOWEST,
            BlockEvent.EntityPlaceEvent.class,
            context -> context.getParam(LootContextParams.THIS_ENTITY) instanceof Player,
            event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), (Player) event.getEntity(), event.getLevel())
                    .withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
            EventFactories::fullCancel,
            (event, vars) -> {}
	));
//	BREATH_CHANGE("swimming", null),
//	BREED("taming", null),
//	BREW("alchemy", null),
//	CONSUME("cooking", null),
//	CRAFT("crafting", null),
//	RECEIVE_DAMAGE("endurance", null),
//	FROM_MOBS("endurance", null),
//	FROM_PLAYERS("endurance", null),
//	FROM_ANIMALS("endurance", null),
//	FROM_PROJECTILES("endurance", null),
//	FROM_MAGIC("endurance", null),
//	FROM_ENVIRONMENT("endurance", null),
//	FROM_IMPACT("endurance", null),
//	DEAL_MELEE_DAMAGE("combat", null),
//	MELEE_TO_MOBS("combat", null),
//	MELEE_TO_PLAYERS("combat", null),
//	MELEE_TO_ANIMALS("combat", null),
//	DEAL_RANGED_DAMAGE("archery", null),
//	RANGED_TO_MOBS("archery", null),
//	RANGED_TO_PLAYERS("archery", null),
//	RANGED_TO_ANIMALS("archery", null),
//	DEATH("endurance", null),
//	ENCHANT("magic", null),
//	EFFECT("magic", null),
//	FISH("fishing", null),
//	SMELT("smithing", null),
//	GROW("farming", null),
//	HEALTH_CHANGE("", null),
//	JUMP("agility", null),
//	SPRINT_JUMP("agility", null),
//	CROUCH_JUMP("agility", null),
//	WORLD_CONNECT("", null),
//	WORLD_DISCONNECT("", null),
//	HIT_BLOCK("dexterity", null),
//	ACTIVATE_BLOCK("dexterity", null),
//	ACTIVATE_ITEM("dexterity", null),
//	ENTITY("charisma", null),
//	RESPAWN("", null),
//	RIDING("taming", null),
//	SHIELD_BLOCK("combat", null),
//	SKILL_UP("", null),
//	SLEEP("endurance", null),
//	SPRINTING("agility", null),
//	SUBMERGED("swimming", null),
//	SWIMMING("swimming", null),
//	DIVING("swimming", null),
//	SURFACING("swimming", null),
//	SWIM_SPRINTING("swimming", null),
//	TAMING("taming", null);

	public EventListenerSpecification<T> factory;
	public String id;
	protected EventFactories(String id, Function<String, EventListenerSpecification<T>> factory) {
		this.id = id;
		this.factory = factory.apply(id);
	}
	public EventListenerSpecification<T> getFactory() {return factory;}
	
	public static void fullCancel(ICancellableEvent event, EventListenerSpecification.CancellationType cancelType) {
		event.setCanceled(true);
	}
}
