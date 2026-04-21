package rpg.project.lib.builtins;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
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
import net.neoforged.neoforge.event.entity.player.AnvilCraftEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.ItemFishedEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.events.ProgressionAdvanceEvent;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;
import rpg.project.lib.internal.util.TagUtils;

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

	//All event IDs
	public static final Identifier LEVEL_UP = Reference.resource("level_up");
	public static final Identifier ANVIL_REPAIR = Reference.resource("anvil_repair");
	public static final Identifier BREAK_BLOCK = Reference.resource("break_block");
	public static final Identifier BREAK_SPEED = Reference.resource("break_speed");
	public static final Identifier PLACE_BLOCK = Reference.resource("place_block");
	public static final Identifier BREATH_CHANGE = Reference.resource("breath_change");
	public static final Identifier BREED_ANIMAL = Reference.resource("breed_animal");
	public static final Identifier TAME_ANIMAL = Reference.resource("tame_animal");
	public static final Identifier BREW_POTION = Reference.resource("brew_potion");
	public static final Identifier CONSUME = Reference.resource("consume");
	public static final Identifier ITEM_CRAFTED = Reference.resource("item_crafted");
	public static final Identifier ON_DEATH = Reference.resource("on_death");
	public static final Identifier EFFECT_ADDED = Reference.resource("effect_added");
	public static final Identifier PLAYER_FISH = Reference.resource("player_fish");
	public static final Identifier HEAL = Reference.resource("heal");
	public static final Identifier JUMP = Reference.resource("jump");
	public static final Identifier SPRINT_JUMP = Reference.resource("sprint_jump");
	public static final Identifier CROUCH_JUMP = Reference.resource("crouch_jump");
	public static final Identifier PLAYER_ATTACK_ENTITY = Reference.resource("player_attack_entity");
	public static final Identifier DAMAGED_BY_PLAYER = Reference.resource("damaged_by_player");
	public static final Identifier ENTITY_DAMAGE_PLAYER = Reference.resource("entity_damage_player");
	public static final Identifier DAMAGE_PLAYER = Reference.resource("damage_player");
	public static final Identifier MITIGATED_DAMAGE = Reference.resource("mitigated_damage");
	public static final Identifier MITIGATED_DAMAGE_ARMOR = Reference.resource("mitigated_damage_armor");
	public static final Identifier MITIGATED_DAMAGE_ABSORB = Reference.resource("mitigated_damage_absorption");
	public static final Identifier MITIGATED_DAMAGE_EFFECT = Reference.resource("mitigated_damage_effect");
	public static final Identifier MITIGATED_DAMAGE_ENCHANT = Reference.resource("mitigated_damage_enchants");
	public static final Identifier MITIGATED_DAMAGE_BLOCK = Reference.resource("mitigated_damage_block");
	public static final Identifier SPRINTING = Reference.resource("sprinting");
	public static final Identifier SUBMERGED = Reference.resource("submerged");
	public static final Identifier SWIMMING = Reference.resource("swimming");
	public static final Identifier DIVING = Reference.resource("diving");
	public static final Identifier SURFACING = Reference.resource("surfacing");
	public static final Identifier SWIM_SPRINTING = Reference.resource("swim_sprinting");
	public static final Identifier RIDING = Reference.resource("riding");
	public static final Identifier FINISH_USE_ITEM = Reference.resource("finish_use_item");
	public static final Identifier USE_ITEM = Reference.resource("use_item");
	public static final Identifier ENCHANT_ITEM = Reference.resource("enchant_item");

	static {
		VALUES.add(new EventListenerSpecification<>(
				LEVEL_UP,
				EventPriority.LOWEST,
				ProgressionAdvanceEvent.class,
				context -> true,
				event -> EventContext.self(LEVEL_UP, event.getEntity(), event.getEntity().level())
						.withParam(ProgressionAdvanceEvent.CONTAINER, event.getContainer())
						.withParam(ProgressionAdvanceEvent.PROGRESS, event.getCurrent()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				ANVIL_REPAIR,
				EventPriority.LOWEST,
				AnvilCraftEvent.Post.class,
				context -> true,
				event -> EventContext.build(ANVIL_REPAIR, RegistryUtil.getId(event.getEntity().registryAccess(), event.getOutput()), EventContext.ITEMSTACK, event.getOutput(), event.getEntity(), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getOutput(), event.getEntity().registryAccess())).create(),
				(e, v) -> {},
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				BREAK_BLOCK,
				EventPriority.LOWEST,
				BlockEvent.BreakEvent.class,
				context -> true,
				event -> EventContext.build(BREAK_BLOCK, RegistryUtil.getId(event.getPlayer().registryAccess(), event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getPlayer(), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter())
						.withParam(EventContext.NBT, TagUtils.mergeTags(TagUtils.stateTag(event.getState()), TagUtils.tileTag(event.getPlayer().level().getBlockEntity(event.getPos())))).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				BREAK_SPEED,
				EventPriority.LOWEST,
				PlayerEvent.BreakSpeed.class,
				context -> true,
				event -> EventContext.build(BREAK_SPEED, RegistryUtil.getId(event.getEntity().registryAccess(), event.getState()), LootContextParams.BLOCK_STATE, event.getState(), event.getEntity(), event.getEntity().level())
						.withParam(LootContextParams.ORIGIN, event.getPosition().orElse(BlockPos.ZERO).getCenter())
						.withParam(EventContext.NBT, TagUtils.mergeTags(TagUtils.stateTag(event.getState()), TagUtils.tileTag(event.getEntity().level().getBlockEntity(event.getPosition().get())))).create(),
				EventFactories::fullCancel,
				(event, context) -> {
					if (context.hasParam(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE))
						event.setNewSpeed(context.getParam(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE));
				}
		));
		VALUES.add(new EventListenerSpecification<>(
				PLACE_BLOCK,
				EventPriority.LOWEST,
				BlockEvent.EntityPlaceEvent.class,
				context -> true,
				event -> EventContext.build(PLACE_BLOCK, RegistryUtil.getId(event.getEntity().registryAccess(), event.getState()), LootContextParams.BLOCK_STATE, event.getState(), orNull(event.getEntity()), event.getLevel())
						.withParam(LootContextParams.ORIGIN, event.getPos().getCenter())
						.withParam(EventContext.NBT, TagUtils.mergeTags(TagUtils.stateTag(event.getState()), TagUtils.tileTag(event.getEntity().level().getBlockEntity(event.getPos())))).create(),
				EventFactories::fullCancel,
				(event, vars) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				BREATH_CHANGE,
				EventPriority.LOWEST,
				LivingBreatheEvent.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getParam(EventContext.BREATH_CHANGE) != 0,
				event -> {
					int diff = event.canBreathe() ? event.getRefillAirAmount() : event.getConsumeAirAmount();
					return EventContext.self(BREATH_CHANGE, orNull(event.getEntity()), event.getEntity().level())
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
				BREED_ANIMAL,
				EventPriority.LOWEST,
				BabyEntitySpawnEvent.class,
				context -> true,
				event -> EventContext.build(BREED_ANIMAL,RegistryUtil.getId(event.getCausedByPlayer().registryAccess(), event.getChild()), EventContext.BABY, event.getChild(), event.getCausedByPlayer(), event.getCausedByPlayer().level())
						.withParam(EventContext.PARENT_A, event.getParentA())
						.withParam(EventContext.PARENT_B, event.getParentB())
						.withParam(EventContext.NBT, TagUtils.entityTag(event.getChild())).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				TAME_ANIMAL,
				EventPriority.LOWEST,
				AnimalTameEvent.class,
				context -> true,
				event -> EventContext.build(TAME_ANIMAL,RegistryUtil.getId(event.getEntity().registryAccess(), event.getAnimal()), LootContextParams.THIS_ENTITY, event.getAnimal(), event.getTamer(), event.getAnimal().level())
						.withParam(EventContext.NBT, TagUtils.entityTag(event.getAnimal())).create(),
				EventFactories::fullCancel,
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				BREW_POTION,
				EventPriority.LOWEST,
				PlayerBrewedPotionEvent.class,
				context -> true,
				event -> EventContext.build(BREW_POTION, RegistryUtil.getId(event.getEntity().registryAccess(), event.getStack()), EventContext.ITEMSTACK, event.getStack(), event.getEntity(), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getStack(), event.getEntity().registryAccess())).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				CONSUME,
				EventPriority.LOWEST,
				LivingEntityUseItemEvent.Finish.class,
				//TODO test that FOOD does not have a default value that would create a never-null scenario
				context -> context.getParam(EventContext.ITEMSTACK).has(DataComponents.FOOD),
				event -> EventContext.build(CONSUME, RegistryUtil.getId(event.getEntity().registryAccess(), event.getItem()), EventContext.ITEMSTACK, event.getItem(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getItem(), event.getEntity().registryAccess())).create(),
				(e,v) -> {},
				(e,v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				ITEM_CRAFTED,
				EventPriority.LOWEST,
				PlayerEvent.ItemCraftedEvent.class,
				context -> true,
				event -> EventContext.build(ITEM_CRAFTED, RegistryUtil.getId(event.getEntity().registryAccess(), event.getCrafting()), EventContext.ITEMSTACK, event.getCrafting(), event.getEntity(), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getCrafting(), event.getEntity().registryAccess())).create(),
				(e, v) -> {},
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				ON_DEATH,
				EventPriority.LOWEST,
				LivingDeathEvent.class,
				context -> true,
				event -> EventContext.build(ON_DEATH, RegistryUtil.getId(event.getEntity().registryAccess(), event.getEntity()), LootContextParams.THIS_ENTITY, event.getEntity(), orNull(event.getSource().getEntity()), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.entityTag(event.getEntity())).create(),
				EventFactories::fullCancel,
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				EFFECT_ADDED,
				EventPriority.LOWEST,
				MobEffectEvent.Applicable.class,
				context -> !context.getParam(EventContext.CANCELLED) //used to check if previous events have set the result to what is effectively cancelled.
						&& !context.getActor().hasEffect(context.getParam(EventContext.MOB_EFFECT).getEffect()),
				event -> EventContext.build(EFFECT_ADDED, RegistryUtil.getId(event.getEffectInstance().getEffect()), EventContext.MOB_EFFECT, event.getEffectInstance(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CANCELLED, event.getResult() == MobEffectEvent.Applicable.Result.DO_NOT_APPLY).create(),
				(event, c) -> event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY),
				(e, v) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				PLAYER_FISH,
				EventPriority.LOWEST,
				ItemFishedEvent.class,
				context -> true,
				event -> EventContext.build(PLAYER_FISH, RegistryUtil.getId(event.getEntity().registryAccess(), event.getDrops().getFirst()), EventContext.ITEMSTACK, event.getDrops().getFirst(), event.getEntity(), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getDrops().getFirst(), event.getEntity().registryAccess())).create(),
				EventFactories::fullCancel,
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				HEAL,
				EventPriority.LOWEST,
				LivingHealEvent.class,
				context -> Objects.equals(context.getParam(EventContext.PLAYER), context.getActor()),
				event -> EventContext.self(HEAL, orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getAmount()).create(),
				EventFactories::fullCancel,
				(event, context) -> event.setAmount(context.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
				JUMP,
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> !context.getActor().isSprinting() && !context.getActor().isCrouching(),
				event -> EventContext.self(JUMP, orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
				SPRINT_JUMP,
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> context.getActor().isSprinting(),
				event -> EventContext.self(SPRINT_JUMP, orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
				CROUCH_JUMP,
				EventPriority.LOWEST,
				LivingEvent.LivingJumpEvent.class,
				context -> context.getActor().isCrouching(),
				event -> EventContext.self(CROUCH_JUMP, orNull(event.getEntity()), event.getEntity().level())
						.withDynamicParam(EventContext.CHANGE_AMOUNT, 0f).create(),
				(e, c) -> {},
				(e, c) -> {
					c.getActor().addDeltaMovement(new Vec3(0, c.getParam(EventContext.CHANGE_AMOUNT), 0));
					c.getActor().hurtMarked = true;
				}
		));
		VALUES.add(new EventListenerSpecification<>(
			PLAYER_ATTACK_ENTITY,
			EventPriority.LOWEST,
			AttackEntityEvent.class,
			context -> true,
			event -> EventContext.build(PLAYER_ATTACK_ENTITY, RegistryUtil.getId(event.getEntity().registryAccess(), event.getTarget()), LootContextParams.THIS_ENTITY, event.getTarget(), event.getEntity(), event.getEntity().level())
					.withParam(EventContext.NBT, TagUtils.entityTag(event.getTarget())).create(),
			EventFactories::fullCancel,
			(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
			DAMAGED_BY_PLAYER,
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> true,
			event -> EventContext.build(DAMAGED_BY_PLAYER, RegistryUtil.getId(event.getEntity().registryAccess(), event.getEntity()), LootContextParams.THIS_ENTITY, event.getEntity(), orNull(event.getSource().getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage())
					.withParam(EventContext.NBT, TagUtils.entityTag(event.getEntity())).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			ENTITY_DAMAGE_PLAYER,
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> context.getParam(LootContextParams.THIS_ENTITY) != null,
			event -> EventContext.build(ENTITY_DAMAGE_PLAYER, event.getSource().getEntity() == null ? Reference.resource("null") : RegistryUtil.getId(event.getEntity().registryAccess(), event.getSource().getEntity()), LootContextParams.THIS_ENTITY, event.getSource().getEntity(), orNull(event.getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage())
					.withParam(EventContext.NBT, event.getSource().getEntity() == null ? new CompoundTag() : TagUtils.entityTag(event.getSource().getEntity())).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			DAMAGE_PLAYER,
			EventPriority.LOWEST,
			LivingDamageEvent.Pre.class,
			context -> true,
			event -> EventContext.self(DAMAGE_PLAYER, orNull(event.getEntity()), event.getEntity().level())
					.withParam(LootContextParams.DAMAGE_SOURCE, event.getSource())
					.withDynamicParam(EventContext.CHANGE_AMOUNT, event.getNewDamage()).create(),
			(e, c) -> e.setNewDamage(0),
			(e, c) -> e.setNewDamage(c.getParam(EventContext.CHANGE_AMOUNT))
		));
		VALUES.add(new EventListenerSpecification<>(
			MITIGATED_DAMAGE,
			EventPriority.LOWEST,
			LivingDamageEvent.Post.class,
			context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
			event -> EventContext.self(MITIGATED_DAMAGE, orNull(event.getEntity()), event.getEntity().level())
					.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ABSORPTION)
					+ event.getReduction(DamageContainer.Reduction.ARMOR)
					+ event.getReduction(DamageContainer.Reduction.ENCHANTMENTS)
					+ event.getReduction(DamageContainer.Reduction.MOB_EFFECTS)
					+ event.getBlockedDamage()).create(),
			(e, c) -> {},
			(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				MITIGATED_DAMAGE_ARMOR,
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(MITIGATED_DAMAGE_ARMOR, orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ARMOR)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				MITIGATED_DAMAGE_ABSORB,
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(MITIGATED_DAMAGE_ABSORB, orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ABSORPTION)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				MITIGATED_DAMAGE_EFFECT,
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(MITIGATED_DAMAGE_EFFECT, orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.MOB_EFFECTS)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				MITIGATED_DAMAGE_ENCHANT,
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(MITIGATED_DAMAGE_ENCHANT, orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getReduction(DamageContainer.Reduction.ENCHANTMENTS)).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				MITIGATED_DAMAGE_BLOCK,
				EventPriority.LOWEST,
				LivingDamageEvent.Post.class,
				context -> context.getParam(EventContext.CHANGE_AMOUNT) > 0f,
				event -> EventContext.self(MITIGATED_DAMAGE_BLOCK,orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.CHANGE_AMOUNT, event.getBlockedDamage()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				SPRINTING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0 && context.getActor().isSprinting(),
				event -> EventContext.self(SPRINTING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				SUBMERGED,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0 && context.getActor().isUnderWater(),
				event -> EventContext.self(SUBMERGED, event.getEntity(), event.getEntity().level()).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				SWIMMING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001,
				event -> EventContext.self(SWIMMING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				DIVING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().getDeltaMovement().y() < 0,
				event -> EventContext.self(DIVING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				SURFACING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().getDeltaMovement().y() > 0,
				event -> EventContext.self(SURFACING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				SWIM_SPRINTING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isUnderWater()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001
						&& context.getActor().isSprinting(),
				event -> EventContext.self(SWIM_SPRINTING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				RIDING,
				EventPriority.LOWEST,
				PlayerTickEvent.Post.class,
				context -> context.getActor().tickCount % 10 == 0
						&& context.getActor().isPassenger()
						&& context.getParam(EventContext.MAGNITUDE) > 0.001,
				event -> EventContext.self(RIDING, event.getEntity(), event.getEntity().level())
						.withParam(EventContext.MAGNITUDE, event.getEntity().moveDist).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				FINISH_USE_ITEM,
				EventPriority.LOWEST,
				LivingEntityUseItemEvent.Finish.class,
				context -> true,
				event -> EventContext.build(FINISH_USE_ITEM, RegistryUtil.getId(event.getEntity().registryAccess(), event.getItem()), EventContext.ITEMSTACK, event.getItem(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getItem(), event.getEntity().registryAccess())).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				USE_ITEM,
				EventPriority.LOWEST,
				PlayerInteractEvent.RightClickItem.class,
				context -> true,
				event -> EventContext.build(USE_ITEM, RegistryUtil.getId(event.getEntity().registryAccess(), event.getItemStack()), EventContext.ITEMSTACK, event.getItemStack(), orNull(event.getEntity()), event.getEntity().level())
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getItemStack(), event.getEntity().registryAccess())).create(),
				(e, c) -> {},
				(e, c) -> {}
		));
		VALUES.add(new EventListenerSpecification<>(
				ENCHANT_ITEM,
				EventPriority.LOWEST,
				PlayerEnchantItemEvent.class,
				context -> true,
				event -> EventContext.build(ENCHANT_ITEM, RegistryUtil.getId(event.getEntity().registryAccess(), event.getEnchantedItem()), EventContext.ITEMSTACK, event.getEnchantedItem(), event.getEntity(), event.getEntity().level())
						.withParam(LootContextParams.ENCHANTMENT_LEVEL, event.getEnchantments().stream().mapToInt(instance -> instance.level()).max().orElse(0))
						.withParam(EventContext.NBT, TagUtils.stackTag(event.getEnchantedItem(), event.getEntity().registryAccess())).create(),
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
