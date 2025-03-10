package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.conditions.ConditionWrapper;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.api.events.conditions.EventConditionAnd;
import rpg.project.lib.internal.registry.EventRegistry;

public record VanillaProgressionConfigType() implements SubSystemConfigType{
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("progression");
	public static final VanillaProgressionConfigType IMPL = new VanillaProgressionConfigType();

	@Override
	public ResourceLocation getId() {return ID;}
	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaProgressionConfig.CODEC;
	}

	@Override
	public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaProgressionConfig(access.lookupOrThrow(APIUtils.GAMEPLAY_EVENTS).keySet()
			.stream().collect(Collectors.toMap(id -> id, id -> List.of(new VanillaProgressionConfig.ExpData(0, Optional.empty())))));}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.PROGRESSION);
	}

	@Override
	public SubSystemConfig fromScript(Map<String, String> values) {
		ResourceLocation id = ResourceLocation.parse(values.getOrDefault("event", "invalid_event"));
		int xp = Integer.parseInt(values.getOrDefault("xp", "0"));
		Optional<ConditionWrapper> conditionWrapper = Optional.of(ConditionWrapper.fromScripting(values));
		return new VanillaProgressionConfig(Map.of(id, List.of(new VanillaProgressionConfig.ExpData(xp, conditionWrapper))));
	}


	public record VanillaProgressionConfig(Map<ResourceLocation, List<ExpData>> eventToXp) implements SubSystemConfig {
		public static class ExpData {
			private int xp;
			private Optional<ConditionWrapper> conditions;
			public ExpData(int xp, Optional<ConditionWrapper> conditions) {
				this.xp = xp;
				this.conditions = conditions;
			}
			public ExpData(int xp) {this(0, Optional.empty());}
			public int xp() {return xp;}
			public void setXp(int xp) {this.xp = xp;}
			public Optional<ConditionWrapper> conditions() {return conditions;}

			public static final MapCodec<ExpData> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
					Codec.INT.fieldOf("xp").forGetter(ExpData::xp),
					ConditionWrapper.CODEC.optionalFieldOf("conditions").forGetter(ExpData::conditions)
			).apply(instance, ExpData::new));

			public static ExpData combine(ExpData a, ExpData b) {
				int xp = Integer.max(a.xp, b.xp);
				Optional<ConditionWrapper> wrapper = a.conditions.orElse(new ConditionWrapper()).combine(b.conditions());
				return new ExpData(xp, wrapper);
			}
		}
		
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.unboundedMap(ResourceLocation.CODEC, ExpData.CODEC.codec().listOf()).fieldOf("events").forGetter(e -> ((VanillaProgressionConfig)e).eventToXp)
				).apply(instance, VanillaProgressionConfig::new));

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaProgressionConfig t = (VanillaProgressionConfig) two;
			var map = new HashMap<>(this.eventToXp());
			t.eventToXp().forEach((key, value) -> map.getOrDefault(key, new ArrayList<>()).addAll(value));
			return new VanillaProgressionConfig(map);
		}

		@Override
		public boolean isUnconfigured() {
			return this.eventToXp().values().stream().map(list -> list.stream().allMatch(data -> data.xp == 0)).allMatch(v -> v);
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
