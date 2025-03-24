package rpg.project.lib.builtins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.brewing.PlayerBrewedPotionEvent;
import net.neoforged.neoforge.event.entity.living.AnimalTameEvent;
import net.neoforged.neoforge.event.entity.living.BabyEntitySpawnEvent;
import net.neoforged.neoforge.event.entity.living.LivingBreatheEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.entity.living.LivingHealEvent;
import net.neoforged.neoforge.event.entity.living.MobEffectEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.DelegatedEventListenerSpecification;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.events.ProgressionAdvanceEvent;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**Contains default factories for translating 
 * {@link net.neoforged.bus.api.Event Event}s
 * to {@link EventContext}s for use by declarations in 
 * {@link rpg.project.lib.internal.registry.EventRegistry EventRegistry}
 * for relevant ecosystem events.
 */
public class EventFactories {
	private static final List<EventProvider<?>> VALUES = new ArrayList<>();

	public static void registerEvents(DeferredRegister<EventProvider<?>> registry) {
		VALUES.forEach(provider -> registry.register(provider.registryPath(), () -> provider));
	}

	static {
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("level_up"),
				EventPriority.LOWEST,
				ProgressionAdvanceEvent.class,
				context -> true,
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(ProgressionAdvanceEvent.CONTAINER, event.getContainer())
						.withParam(ProgressionAdvanceEvent.PROGRESS, event.getCurrent()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("anvil_repair"),
				EventPriority.LOWEST,
				AnvilRepairEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getOutput()), EventContext.ITEMSTACK, event.getOutput(), event.getEntity(), event.getEntity().level()).create(),
				(e, v) -> {},
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("break_block"),
				EventPriority.LOWEST,
				BlockEvent.BreakEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getPlayer(), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("break_speed"),
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
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("place_block"),
				EventPriority.LOWEST,
				BlockEvent.EntityPlaceEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getState()), LootContextParams.BLOCK_STATE, event.getState(), orNull(event.getEntity()), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter()).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("breath_change"),
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
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("breed_animal"),
				EventPriority.LOWEST,
				BabyEntitySpawnEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getChild()), EventContext.BABY, event.getChild(), event.getCausedByPlayer(), event.getCausedByPlayer().level())
						.withParam(EventContext.PARENT_A, event.getParentA())
						.withParam(EventContext.PARENT_B, event.getParentB()).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("tame_animal"),
				EventPriority.LOWEST,
				AnimalTameEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getAnimal()), LootContextParams.THIS_ENTITY, event.getAnimal(), event.getTamer(), event.getAnimal().level()).create(),
				EventFactories::fullCancel,
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("brew_potion"),
				EventPriority.LOWEST,
				PlayerBrewedPotionEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getStack()), EventContext.ITEMSTACK, event.getStack(), event.getEntity(), event.getEntity().level()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("consume"),
				EventPriority.LOWEST,
				LivingEntityUseItemEvent.Finish.class,
				//TODO test that FOOD does not have a default value that would create a never-null scenario
				context -> context.getParam(EventContext.ITEMSTACK).get(DataComponents.FOOD) != null,
				event -> EventContext.build(RegistryUtil.getId(event.getItem()), EventContext.ITEMSTACK, event.getItem(), orNull(event.getEntity()), event.getEntity().level()).create(),
				(e,v) -> {},
				(e,v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("item_crafted"),
				EventPriority.LOWEST,
				PlayerEvent.ItemCraftedEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getCrafting()), EventContext.ITEMSTACK, event.getCrafting(), event.getEntity(), event.getEntity().level()).create(),
				(e, v) -> {},
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("on_death"),
				EventPriority.LOWEST,
				LivingDeathEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getEntity()), LootContextParams.THIS_ENTITY, event.getEntity(), orNull(event.getSource().getEntity()), event.getEntity().level()).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("effect_added"),
				EventPriority.LOWEST,
				MobEffectEvent.Applicable.class,
				context -> !context.getParam(EventContext.CANCELLED) //used to check if previous events have set the result to what is effectively cancelled.
						&& !context.getActor().hasEffect(context.getParam(EventContext.MOB_EFFECT).getEffect()),
				event -> EventContext.build(RegistryUtil.getId(event.getEffectInstance().getEffect()), EventContext.MOB_EFFECT, event.getEffectInstance(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CANCELLED, event.getResult() == MobEffectEvent.Applicable.Result.DO_NOT_APPLY).create(),
				(event, c) -> event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY),
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("player_fish"),
				EventPriority.LOWEST,
				ItemFishedEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getDrops().getFirst()), EventContext.ITEMSTACK, event.getDrops().getFirst(), event.getEntity(), event.getEntity().level()).create(),
				EventFactories::fullCancel,
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("heal"),
				EventPriority.LOWEST,
				LivingHealEvent.class,
				context -> Objects.equals(context.getParam(EventContext.PLAYER), context.getActor()),
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getAmount()).create(),
				EventFactories::fullCancel,
				(event, context) -> event.setAmount(context.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("jump"),
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> !context.getActor().isSprinting() && !context.getActor().isCrouching(),
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("sprint_jump"),
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> context.getActor().isSprinting(),
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("crouch_jump"),
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> context.getActor().isCrouching(),
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
			Reference.resource("player_attack_entity"),
			EventPriority.LOWEST,
			AttackEntityEvent.class,
			context -> true,
			event -> EventContext.build(RegistryUtil.getId(event.getTarget()), LootContextParams.THIS_ENTITY, event.getTarget(), event.getEntity(), event.getEntity().level()).create(),
			EventFactories::fullCancel,
			(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
			Reference.resource("damaged_by_player"),
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> true,
			event -> EventContext.build(RegistryUtil.getId(event.getEntity()), LootContextParams.THIS_ENTITY, event.getEntity(), orNull(event.getSource().getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage()).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			Reference.resource("entity_damage_player"),
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> context.getParam(LootContextParams.THIS_ENTITY) != null,
			event -> EventContext.build(event.getSource().getEntity() == null ? Reference.resource("null") : RegistryUtil.getId(event.getSource().getEntity()), LootContextParams.THIS_ENTITY, event.getSource().getEntity(), orNull(event.getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage()).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			Reference.resource("damage_player"),
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> true,
			event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage()).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			Reference.resource("mitigated_damage"),
			EventPriority.LOWEST,
			LivingDamageEvent.Post.class,
			context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
			event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
					.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ABSORPTION)
					+ event.getReduction(DamageContainer.Reduction.ARMOR)
					+ event.getReduction(DamageContainer.Reduction.ENCHANTMENTS)
					+ event.getReduction(DamageContainer.Reduction.MOB_EFFECTS)
					+ event.getBlockedDamage()).create(),
			(e, c) -> {},
			(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("mitigated_damage_armor"),
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ARMOR)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("mitigated_damage_absorption"),
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ABSORPTION)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("mitigated_damage_effect"),
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.MOB_EFFECTS)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("mitigated_damage_enchants"),
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ENCHANTMENTS)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("mitigated_damage_block"),
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getBlockedDamage()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("sprinting"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0 && context.getActor().isSprinting(),
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("submerged"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0 && context.getActor().isUnderWater(),
				event -> EventContext.self(event.getEntity(), event.getEntity().level()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("swimming"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001,
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("diving"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().getDeltaMovement().y() < 0,
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("surfacing"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().getDeltaMovement().y() > 0,
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("swim_sprinting"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().isSprinting(),
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("riding"),
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isPassenger()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001,
				event -> EventContext.self(event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("use_item"),
				EventPriority.LOWEST,
				LivingEntityUseItemEvent.Finish.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getItem()), EventContext.ITEMSTACK, event.getItem(), orNull(event.getEntity()), event.getEntity().level()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				Reference.resource("enchant_item"),
				EventPriority.LOWEST,
				PlayerEnchantItemEvent.class,
				context -> true,
				event -> EventContext.build(RegistryUtil.getId(event.getEnchantedItem()), EventContext.ITEMSTACK, event.getEnchantedItem(), event.getEntity(), event.getEntity().level())
						.withParam(LootContextParams.ENCHANTMENT_LEVEL, event.getEnchantments().stream().mapToInt(instance -> instance.level).max().orElse(0)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
	}

	//Events that don't exist and require hacks to work
//	ENCHANT
	//Events for an addon that require storing player action data or tracking the world.
//	SMELT("smithing", null),
//	GROW("farming", null),
//	ENTITY("charisma", null),

	private static Player orNull(Entity entity) {
		return entity instanceof Player player ? player : null;
	}

	public static void fullCancel(ICancellableEvent event, EventListenerSpecification.CancellationType cancelType) {
		event.setCanceled(true);
	}
}
