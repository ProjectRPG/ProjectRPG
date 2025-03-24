package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.conditions.ConditionWrapper;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.MsLoggy;

public record VanillaAbilityConfigType() implements SubSystemConfigType {
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("abilities");
	public static final VanillaAbilityConfigType IMPL = new VanillaAbilityConfigType();

	@Override
	public ResourceLocation getId() {return ID;}
	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaAbilityConfig.CODEC;
	}

	@Override
	public SubSystemConfig getDefault(RegistryAccess access) {return
			new VanillaAbilityConfig(AbilityUtils.get(access).getDefaults().stream()
					.map(compound -> new VanillaAbilityConfig.ConditionalAbility(compound, Optional.empty()))
					.toList());}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.ABILITY);
	}

	@Override
	public SubSystemConfig fromScript(Map<String, String> values) {
		CompoundTag abilitySetting = tagFromValueMap(values);
		ConditionWrapper wrapper = ConditionWrapper.fromScripting(values);
		Optional<ConditionWrapper> conditions = wrapper.isEmpty() ? Optional.empty() : Optional.of(wrapper);
		return new VanillaAbilityConfig(List.of(new VanillaAbilityConfig.ConditionalAbility(abilitySetting, conditions)));
	}

	private CompoundTag tagFromValueMap(Map<String, String> values) {
		CompoundTag outTag = new CompoundTag();
		values.entrySet().stream()
				.filter(entry -> !entry.getKey().startsWith("and_if"))
				.filter(entry -> !entry.getKey().startsWith("or_if"))
				.forEach(entry -> {
			try {
				Tag tag = new TagParser(new StringReader(entry.getValue())).readValue();
				outTag.put(entry.getKey(), tag);
			}
			catch (CommandSyntaxException e) {
				MsLoggy.ERROR.log(MsLoggy.LOG_CODE.DATA, "unable to parse perk value %s", entry.getValue());
			}
		});
		return outTag;
	}


	public record VanillaAbilityConfig(List<ConditionalAbility> data) implements SubSystemConfig {
		public static record ConditionalAbility(CompoundTag ability, Optional<ConditionWrapper> conditions) {
			public static final Codec<ConditionalAbility> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					CompoundTag.CODEC.fieldOf("ability").forGetter(ConditionalAbility::ability),
					ConditionWrapper.CODEC.optionalFieldOf("conditions").forGetter(ConditionalAbility::conditions)
			).apply(instance, ConditionalAbility::new));

			@Override
			public boolean equals(Object o) {
				if (this == o) return true;
				if (o == null || getClass() != o.getClass()) return false;
				ConditionalAbility that = (ConditionalAbility) o;
				return Objects.equals(ability, that.ability) && Objects.equals(conditions, that.conditions);
			}

			@Override
			public int hashCode() {
				return Objects.hash(ability, conditions);
			}
		}
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				ConditionalAbility.CODEC.listOf().fieldOf("configurations").forGetter(ssc -> ((VanillaAbilityConfig)ssc).data())
				).apply(instance, VanillaAbilityConfig::new));

		@Override
		public boolean isPriorityData() {return false;}

		@Override
		public MergeableData combine(MergeableData two) {
			List<ConditionalAbility> base = new ArrayList<>(this.data);
			VanillaAbilityConfig t = (VanillaAbilityConfig) two;
			t.data().stream().filter(Predicate.not(base::contains)).forEach(base::add);
			return new VanillaAbilityConfig(base);
		}

		@Override
		public boolean isUnconfigured() {
			return this.data().isEmpty();
		}

		@Override
		public SubSystemConfigType getType() {
			return IMPL;
		}

		@Override
		public MapCodec<SubSystemConfig> getCodec() {
			return CODEC;
		}
	}
}
