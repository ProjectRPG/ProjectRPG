package rpg.project.lib.builtins;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.registries.ForgeRegistries;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.enums.AbilitySide;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;
import rpg.project.lib.internal.util.TagBuilder;

public class Abilities {
	public static void init() {
		AbilityUtils.registerAbility(Reference.resource("break_speed"), BREAK_SPEED, AbilitySide.BOTH);
		AbilityUtils.registerAbility(Reference.resource("effect"), EFFECT, AbilitySide.SERVER);
	}

	private static final Set<ToolAction> DIG_ACTIONS = Set.of(ToolActions.PICKAXE_DIG, ToolActions.AXE_DIG,
			ToolActions.SHOVEL_DIG, ToolActions.HOE_DIG, ToolActions.SHEARS_DIG, ToolActions.SWORD_DIG);
	
	private static Ability BREAK_SPEED = Ability.begin().addDefaults(getBreakSpeedDefaults()).setStart((player, compoundTag) -> {
		float speedIn = compoundTag.contains(AbilityUtils.BREAK_SPEED_INPUT_VALUE)
				? compoundTag.getFloat(AbilityUtils.BREAK_SPEED_INPUT_VALUE)
				: player.getMainHandItem().getDestroySpeed(Blocks.OBSIDIAN.defaultBlockState());
		float speedBonus = getRatioForTool(player.getMainHandItem(), compoundTag);
		if (speedBonus == 0) {
			return new CompoundTag();
		}

		float newSpeed = speedIn * Math.max(0, 1 + compoundTag.getInt(AbilityUtils.PROGRESS_LEVEL) * speedBonus);
		return TagBuilder.start().withFloat(AbilityUtils.BREAK_SPEED_OUTPUT_VALUE, newSpeed).build();
	}).setStatus((player, settings) -> {
		List<MutableComponent> lines = new ArrayList<>();
		// int skillLevel = settings.getInt(APIUtils.SKILL_LEVEL);
		// DIG_ACTIONS.stream()
		// .filter(action -> settings.getFloat(action.name()) > 0)
		// .forEach(action ->
		// lines.add(LangProvider.PERK_BREAK_SPEED_STATUS_1.asComponent(action.name(),
		// settings.getFloat(action.name()) * (float) skillLevel)));
		lines.add(Component.literal("TEST"));
		return lines;
	}).build();

	private static float getRatioForTool(ItemStack tool, CompoundTag nbt) {
		float ratio = 0f;
		for (ToolAction action : DIG_ACTIONS) {
			if (tool.canPerformAction(action)) {
				ratio += nbt.getFloat(action.name());
			}
		}
		return ratio;
	}

	private static CompoundTag getBreakSpeedDefaults() {
		TagBuilder builder = TagBuilder.start();
		for (ToolAction action : DIG_ACTIONS) {
			builder.withFloat(action.name(), 0);
		}
		return builder.build();
	}
	
	public static BiFunction<Player, CompoundTag, CompoundTag> EFFECT_SETTER = (player, nbt) -> {
		MobEffect effect;
		if ((effect = ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("effect")))) != null) {
			int configDuration = nbt.getInt(AbilityUtils.DURATION);
			int duration = player.hasEffect(effect) && player.getEffect(effect).getDuration() > configDuration 
					? player.getEffect(effect).getDuration() 
					: configDuration;
			int perLevel = nbt.getInt(AbilityUtils.PER_LEVEL);
			int amplifier = nbt.getInt(AbilityUtils.MODIFIER);
			boolean ambient = nbt.getBoolean(AbilityUtils.AMBIENT);
			boolean visible = nbt.getBoolean(AbilityUtils.VISIBLE);
			player.addEffect(new MobEffectInstance(effect, perLevel * duration, amplifier, ambient, visible));
		}
		return new CompoundTag();
	};
	
	public static Ability EFFECT = Ability.begin()
			.addDefaults(TagBuilder.start().withString("effect", "modid:effect")
					.withInt(AbilityUtils.DURATION, 100)
					.withInt(AbilityUtils.PER_LEVEL, 1)
					.withInt(AbilityUtils.MODIFIER, 0)
					.withBool(AbilityUtils.AMBIENT, false)
					.withBool(AbilityUtils.VISIBLE, true).build())
			.setStart(EFFECT_SETTER)
			.setTick((player, nbt, ticks) -> EFFECT_SETTER.apply(player, nbt))
			.setDescription(LangProvider.PERK_EFFECT_DESC.asComponent())
			.setStatus((player, nbt) -> List.of(
					LangProvider.PERK_EFFECT_STATUS_1.asComponent(Component.translatable(ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(nbt.getString("effect"))).getDescriptionId())),
					LangProvider.PERK_EFFECT_STATUS_2.asComponent(nbt.getInt(AbilityUtils.MODIFIER), nbt.getInt(AbilityUtils.DURATION))))
			.build();
}
