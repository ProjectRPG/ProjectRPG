package rpg.project.lib.builtins;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilityFunction;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.enums.RegistrationSide;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.ProgressionAdvanceEvent;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.RegistryUtil;
import rpg.project.lib.internal.util.TagBuilder;
import rpg.project.lib.internal.util.TagUtils;

public class Abilities {
	public static void init() {
		CommonSetup.ABILITIES.register("break_speed", () -> BREAK_SPEED);
		CommonSetup.ABILITIES.register("effect", () -> EFFECT);
		CommonSetup.ABILITIES.register("attribute", () -> ATTRIBUTE);
		CommonSetup.ABILITIES.register("command", () -> COMMAND);
		CommonSetup.ABILITIES.register("modify", () -> MODIFY_VALUE);
	}

	private static final Set<ItemAbility> DIG_ACTIONS = Set.of(ItemAbilities.PICKAXE_DIG, ItemAbilities.AXE_DIG,
			ItemAbilities.SHOVEL_DIG, ItemAbilities.HOE_DIG, ItemAbilities.SHEARS_DIG, ItemAbilities.SWORD_DIG);
	
	private static final Ability BREAK_SPEED = Ability.begin(RegistrationSide.BOTH).addDefaults(getBreakSpeedDefaults()).setStart((player, settings, context) -> {
		float speedIn = context.hasParam(AbilityUtils.BREAK_SPEED_INPUT_VALUE)
				? context.getParam(AbilityUtils.BREAK_SPEED_INPUT_VALUE)
				: player.getMainHandItem().getDestroySpeed(Blocks.OBSIDIAN.defaultBlockState());
		float speedBonus = getRatioForTool(player.getMainHandItem(), settings);
		if (speedBonus == 0)
			return ;

		float newSpeed = speedIn * Math.max(0, 1 + settings.getInt(AbilityUtils.PROGRESS_LEVEL) * speedBonus);
		context.setParam(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE, newSpeed);
	}).setStatus((player, settings, context) -> {
		List<MutableComponent> lines = new ArrayList<>();
		 int skillLevel = settings.getInt(AbilityUtils.PROGRESS_LEVEL);
		 DIG_ACTIONS.stream()
		 .filter(action -> settings.getFloat(action.name()) > 0)
		 .forEach(action ->
		 lines.add(LangProvider.BREAK_SPEED_ABILITY_STATUS1.asComponent(action.name(),
		 settings.getFloat(action.name()) * (float) skillLevel)));
		return lines;
	}).build();

	private static float getRatioForTool(ItemStack tool, CompoundTag nbt) {
		float ratio = 0f;
		for (ItemAbility action : DIG_ACTIONS) {
			if (tool.canPerformAction(action)) {
				ratio += nbt.getFloat(action.name());
			}
		}
		return ratio;
	}

	private static CompoundTag getBreakSpeedDefaults() {
		TagBuilder builder = TagBuilder.start();
		for (ItemAbility action : DIG_ACTIONS) {
			builder.withFloat(action.name(), 0);
		}
		return builder.build();
	}
	
	public static AbilityFunction EFFECT_SETTER = (player, nbt, context) -> {
		Optional<Holder.Reference<MobEffect>> effectHolder = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(nbt.getString("effect")));
		effectHolder.ifPresent(effect -> {
			int configDuration = nbt.getInt(AbilityUtils.DURATION);
			int duration = player.hasEffect(effect) && player.getEffect(effect).getDuration() > configDuration
					? player.getEffect(effect).getDuration() 
					: configDuration;
			int perLevel = nbt.getInt(AbilityUtils.PER_LEVEL);
			int amplifier = nbt.getInt(AbilityUtils.MODIFIER);
			boolean ambient = nbt.getBoolean(AbilityUtils.AMBIENT);
			boolean visible = nbt.getBoolean(AbilityUtils.VISIBLE);
			player.addEffect(new MobEffectInstance(effect, perLevel * duration, amplifier, ambient, visible));
		});
	};
	
	public static final Ability EFFECT = Ability.begin(RegistrationSide.SERVER)
			.addDefaults(TagBuilder.start().withString("effect", "modid:effect")
					.withInt(AbilityUtils.DURATION, 100)
					.withInt(AbilityUtils.PER_LEVEL, 1)
					.withInt(AbilityUtils.MODIFIER, 0)
					.withBool(AbilityUtils.AMBIENT, false)
					.withBool(AbilityUtils.VISIBLE, true).build())
			.setStart(EFFECT_SETTER)
			.setTick((player, nbt, context, ticks) -> EFFECT_SETTER.start(player, nbt, context))
			.setDescription(LangProvider.PERK_EFFECT_DESC.asComponent())
			.setStatus((player, nbt, context) -> List.of(
					LangProvider.PERK_EFFECT_STATUS_1.asComponent(Component.translatable(BuiltInRegistries.MOB_EFFECT.getValue(ResourceLocation.parse(nbt.getString("effect"))).getDescriptionId())),
					LangProvider.PERK_EFFECT_STATUS_2.asComponent(nbt.getInt(AbilityUtils.MODIFIER), nbt.getInt(AbilityUtils.DURATION))))
			.build();

	private static final Map<String, Holder.Reference<Attribute>> attributeCache = new HashMap<>();
	private static Holder.Reference<Attribute> getAttribute(CompoundTag nbt, RegistryAccess access) {
		return attributeCache.computeIfAbsent(nbt.getString(AbilityUtils.ATTRIBUTE),
				name -> access.lookupOrThrow(Registries.ATTRIBUTE).get(ResourceLocation.parse(name)).orElse(null));
	}

	public static final Ability ATTRIBUTE = Ability.begin(RegistrationSide.SERVER)
			.addConditions((player, tag, context) -> getAttribute(tag, player.level().registryAccess()) != null)
			.addDefaults(TagBuilder.start()
					.withString(AbilityUtils.ATTRIBUTE, "null:null")
					.withDouble(AbilityUtils.BASE, 0d)
					.withDouble(AbilityUtils.PER_LEVEL, 0d)
					.withDouble(AbilityUtils.MAX_BOOST, 0d)
					.withString(AbilityUtils.CONTAINER_NAME, "exp")
					.withBool(AbilityUtils.MULTIPLICATIVE, true).build())
			.setStart(((player, settings, context) -> {
				double perLevel = settings.getDouble(AbilityUtils.PER_LEVEL);
				double maxBoost = settings.getDouble(AbilityUtils.MAX_BOOST);
				String container = settings.getString(AbilityUtils.CONTAINER_NAME);
				long progress = Core.get(player.level()).getProgression().getProgress(player.getUUID(), container).getProgressAsNumber();
				AttributeInstance instance = player.getAttribute(getAttribute(settings, player.level().registryAccess()));
				if (instance == null) return;
				double boost = Math.min((perLevel * (double)progress) + settings.getDouble(AbilityUtils.BASE), maxBoost);
				AttributeModifier.Operation operation = settings.getBoolean(AbilityUtils.MULTIPLICATIVE) ? AttributeModifier.Operation.ADD_MULTIPLIED_BASE :  AttributeModifier.Operation.ADD_VALUE;

				ResourceLocation attributeID = Reference.resource("ability/"+settings.getString(AbilityUtils.ATTRIBUTE).replace(':','_')+"/"+container);
				AttributeModifier modifier = new AttributeModifier(attributeID, boost, operation);
				if (instance.hasModifier(attributeID))
					instance.removeModifier(attributeID);
				instance.addPermanentModifier(modifier);
			}))
			.setDescription(LangProvider.ATTRIBUTE_DESC.asComponent())
			.setStatus((player, settings, context) -> {
				double perLevel = settings.getDouble(AbilityUtils.PER_LEVEL);
				double maxBoost = settings.getDouble(AbilityUtils.MAX_BOOST);
				String container = settings.getString(AbilityUtils.CONTAINER_NAME);
				long progress = Core.get(player.level()).getProgression().getProgress(player.getUUID(), container).getProgressAsNumber();
				double boost = Math.min((perLevel * (double)progress) + settings.getDouble(AbilityUtils.BASE), maxBoost);
				String attribute = player.level().registryAccess()
						.lookupOrThrow(Registries.ATTRIBUTE).getValue(ResourceLocation.parse(settings.getString(AbilityUtils.ATTRIBUTE))).getDescriptionId();
				return List.of(
					LangProvider.ATTRIBUTE_STATUS1.asComponent(
						Component.translatable(attribute),
						boost
					)
				);
			}).build();

	private static final String CMD = "command";
	private static final String FNC = "function";
	public static final Ability COMMAND = Ability.begin(RegistrationSide.SERVER)
			.addConditions((p, n, e) -> n.contains(CMD) || n.contains(FNC))
			.setStart((p, nbt, context) -> {
				if (p instanceof ServerPlayer player ) {
					if (nbt.contains(FNC)) {
						player.getServer().getFunctions().execute(
								player.getServer().getFunctions().get(ResourceLocation.parse(nbt.getString(FNC))).get(),
								player.createCommandSourceStack().withSuppressedOutput().withMaximumPermission(2));
					} else if (nbt.contains(CMD)) {
						player.getServer().getCommands().performPrefixedCommand(
								player.createCommandSourceStack().withSuppressedOutput().withMaximumPermission(2),
								nbt.getString(CMD));
					}
				}
			})
			.setDescription(LangProvider.COMMAND_DESC.asComponent())
			.setStatus((player, nbt, context) -> List.of(
					LangProvider.COMMAND_STATUS_1.asComponent(
							nbt.contains(CMD) ? LangProvider.COMMMAND_COMMAND.asComponent() : LangProvider.COMMMAND_FUNCTION.asComponent(),
							nbt.contains(CMD) ? nbt.getString(CMD) : nbt.getString(FNC))
			)).build();

	//Can be used to reduce damage as well as increase jump amount
	public static final Ability MODIFY_VALUE = Ability.begin(RegistrationSide.BOTH)
			.addDefaults(TagBuilder.start()
					.withFloat(AbilityUtils.PER_LEVEL, 0f)
					.withFloat(AbilityUtils.BASE, 0f)
					.withFloat(AbilityUtils.MAX_BOOST, Float.MAX_VALUE)
					.withString(AbilityUtils.CONTAINER_NAME, "exp")
					.withList(AbilityUtils.DAMAGE_TYPES, new ListTag()).build())
			.setStart(((player, settings, context) -> {
				if (context.hasParam(EventContext.CHANGE_AMOUNT)
						&& (settings.getList(AbilityUtils.DAMAGE_TYPES, StringTag.TAG_STRING).isEmpty()
						|| (context.hasParam(LootContextParams.DAMAGE_SOURCE)
							&& tagContains(settings, AbilityUtils.DAMAGE_TYPES, context, LootContextParams.DAMAGE_SOURCE)))) {
					float change = context.getParam(EventContext.CHANGE_AMOUNT);
					String container = settings.getString(AbilityUtils.CONTAINER_NAME);
					long progressLevel = Core.get(context.getLevel()).getProgression().getProgress(player.getUUID(), container).getProgressAsNumber();
					change += settings.getFloat(AbilityUtils.BASE) + (settings.getFloat(AbilityUtils.PER_LEVEL) * (float)progressLevel);
					context.setParam(EventContext.CHANGE_AMOUNT, change);
				}
			}))
			.setStatus((player, settings, context) -> {
				float change = context.getParam(EventContext.CHANGE_AMOUNT);
				String container = settings.getString(AbilityUtils.CONTAINER_NAME);
				long progressLevel = Core.get(context.getLevel()).getProgression().getProgress(player.getUUID(), container).getProgressAsNumber();
				change += settings.getFloat(AbilityUtils.BASE) + (settings.getFloat(AbilityUtils.PER_LEVEL) * (float)progressLevel);
				return List.of(
					LangProvider.MODIFY_STATUS.asComponent(change)
				);
			}).build();

	private static boolean tagContains(CompoundTag tag, String key, EventContext context, ContextKey<DamageSource> param) {
		ListTag list = tag.getList(key, StringTag.TAG_STRING);
		DamageSource source = context.getParam(param);
		StringTag strTag = StringTag.valueOf(RegistryUtil.getId(source.typeHolder()).toString());
		return list.contains(strTag);
	}
}
