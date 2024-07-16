package rpg.project.lib.builtins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.enchanting.EnchantmentLevelSetEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**Contains default factories for translating 
 * {@link net.neoforged.bus.api.Event Event}s
 * to {@link EventContext}s for use by declarations in 
 * {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories {
	private static final Map<String, Function<String, EventListenerSpecification<?>>> VALUES = new HashMap<>();

	public static void registerEvents(DeferredRegister<EventListenerSpecification<?>> registry) {
		VALUES.forEach((id, func) -> registry.register(id, () -> func.apply(id)));
	}

	static {
		VALUES.put("anvil_repair", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				AnvilRepairEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getOutput()), EventContext.ITEMSTACK, event.getOutput(), event.getEntity(), event.getEntity().level()).create(),
				(e, v) -> {},
				(event, vars) -> {}
		));
		VALUES.put("break_block", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				BlockEvent.BreakEvent.class,
				context -> context.getParam(LootContextParams.THIS_ENTITY) instanceof Player,
				event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getPlayer(), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.put("break_speed", id -> new EventListenerSpecification<>(
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
		VALUES.put("place_block", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				BlockEvent.EntityPlaceEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), orNull(event.getEntity()), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.put("breath_change", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				LivingBreatheEvent.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getParam(EventContext.BREATH_CHANGE) != 0,
				event -> {
					int diff = event.canBreathe() ? event.getRefillAirAmount() : event.getConsumeAirAmount();
					return EventContext.self(orNull(event.getEntity()), event.getEntity().level())
							.withDynamicParam(EventContext.BREATH_CHANGE, diff).create();
				},
				(e, v) -> {},
				(event, context) -> {
					int change = context.getParam(EventContext.BREATH_CHANGE);
					if (event.canBreathe()) event.setRefillAirAmount(change);
					else event.setConsumeAirAmount(change);
				}
		));
		VALUES.put("breed_animal", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				BabyEntitySpawnEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getChild()), EventContext.BABY, event.getChild(), event.getCausedByPlayer(), event.getCausedByPlayer().level())
						.withParam(EventContext.PARENT_A, event.getParentA())
						.withParam(EventContext.PARENT_B, event.getParentB()).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		VALUES.put("brew_potion", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				PlayerBrewedPotionEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getStack()), EventContext.ITEMSTACK, event.getStack(), event.getEntity(), event.getEntity().level()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.put("consume", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				LivingEntityUseItemEvent.Finish.class,
				context -> context.getParam(EventContext.ITEMSTACK).getFoodProperties(context.getActor()) != null,
				event -> EventContext.build(RegistryUtil.getId(event.getItem()), EventContext.ITEMSTACK, event.getItem(), orNull(event.getEntity()), event.getEntity().level()).create(),
				(e,v) -> {},
				(e,v) -> {}
		));
		VALUES.put("item_crafted", id -> new EventListenerSpecification<>(
				Reference.resource("item_crafted"),
				EventPriority.LOWEST,
				PlayerEvent.ItemCraftedEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getCrafting()), EventContext.ITEMSTACK, event.getCrafting(), event.getEntity(), event.getEntity().level()).create(),
				(e, v) -> {},
				(e, v) -> {}
		));
		VALUES.put("on_death", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				LivingDeathEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getEntity()), LootContextParams.THIS_ENTITY, event.getEntity(), orNull(event.getSource().getEntity()), event.getEntity().level()).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		//TODO replace this shit with something better.  This might be a candidate for an addon that can add the mixin/patch, unless NF adds it first.
//		VALUES.put("enchant_item", id -> new EventListenerSpecification<>(
//				Reference.resource(id),
//				EventPriority.LOWEST,
//				EnchantmentLevelSetEvent.class,
//				context -> true,
//				event -> EventContext.build(RegistryUtil.getId(event.getItem()), EventContext.ITEMSTACK, event.getItem(),
//						event.getLevel().getEntitiesOfClass(Player.class,
//										AABB.ofSize(event.getPos().getCenter(), 5, 5, 5),
//										p -> p.hasContainerOpen() && p.containerMenu.getType().equals(MenuType.ENCHANTMENT))
//								.stream().findFirst().orElse(null), event.getLevel()).create(),
//				(e,v) -> {},
//				(e,v) -> {}
//		));
		VALUES.put("effect_added", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				MobEffectEvent.Applicable.class,
				context -> !context.getParam(EventContext.CANCELLED), //used to check if previous events have set the result to what is effectively cancelled.
				event -> EventContext.build(RegistryUtil.getId(event.getEffectInstance().getEffect()), EventContext.MOB_EFFECT, event.getEffectInstance(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CANCELLED, event.getResult() == MobEffectEvent.Applicable.Result.DO_NOT_APPLY).create(),
				(event, c) -> event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY),
				(e, v) -> {}
		));
		VALUES.put("player_fish", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				ItemFishedEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getDrops().getFirst()), EventContext.ITEMSTACK, event.getDrops().getFirst(), event.getEntity(), event.getEntity().level()).create(),
				EventFactories::fullCancel,
				(e, c) -> {}
		));
		VALUES.put("heal", id -> new EventListenerSpecification<>(
				Reference.resource(id),
				EventPriority.LOWEST,
				LivingHealEvent.class,
				context -> Objects.equals(context.getParam(EventContext.PLAYER), context.getActor()),
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.HEALTH_CHANGE, event.getAmount()).create(),
				EventFactories::fullCancel,
				(event, context) -> event.setAmount(context.getParam(EventContext.HEALTH_CHANGE))
		));
	}
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
//	RECEIVE_DAMAGE("endurance", null),
//	DEAL_DAMAGE("combat", null),
//	MITIGATE_DAMAGE("combat", null),
//	SMELT("smithing", null),
//	GROW("farming", null),

	private static Player orNull(Entity entity) {
		return entity instanceof Player player ? player : null;
	}

	public static void fullCancel(ICancellableEvent event, EventListenerSpecification.CancellationType cancelType) {
		event.setCanceled(true);
	}
}
