package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
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
import rpg.project.lib.internal.util.Functions;

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
			.stream().collect(Collectors.toMap(id -> id, id -> List.of(new VanillaProgressionConfig.ExpData(0, Optional.empty())))), Optional.empty());}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.PROGRESSION);
	}

	@Override
	public SubSystemConfig fromScript(Map<String, String> values) {
		ResourceLocation id = ResourceLocation.parse(values.getOrDefault("for_event", "invalid_event"));
		int xp = Integer.parseInt(values.getOrDefault("xp", "0"));
		Optional<ConditionWrapper> conditionWrapper = Optional.of(ConditionWrapper.fromScripting(values));
		Optional<Boolean> priority = Optional.of(values.containsKey("override"));
		return new VanillaProgressionConfig(Map.of(id, List.of(new VanillaProgressionConfig.ExpData(xp, conditionWrapper))), priority);
	}


	public record VanillaProgressionConfig(Map<ResourceLocation, List<ExpData>> eventToXp, Optional<Boolean> isPriority) implements SubSystemConfig {
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

			@Override
			public String toString() {
				return "ExpData{xp=%s, conditions=%s}".formatted(this.xp, this.conditions.isEmpty() || this.conditions.get().isEmpty() ? "Empty" : conditions.toString());
			}
		}
		
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.unboundedMap(ResourceLocation.CODEC, ExpData.CODEC.codec().listOf()).fieldOf("events").forGetter(e -> ((VanillaProgressionConfig)e).eventToXp),
				Codec.BOOL.optionalFieldOf("override").forGetter(e -> Optional.of(e.isPriorityData()))
				).apply(instance, VanillaProgressionConfig::new));

		@Override
		public boolean isPriorityData() {return this.isPriority.orElse(false);}

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaProgressionConfig t = (VanillaProgressionConfig) two;
			Map<ResourceLocation, List<ExpData>> map = new HashMap<>();

			BiConsumer<VanillaProgressionConfig, VanillaProgressionConfig> bothOrNeither = (pri, sec) -> {
				pri.eventToXp().forEach((key, list) -> map.put(key, new ArrayList<>(list)));
				sec.eventToXp().forEach((key, value) -> map.computeIfAbsent(key, event -> new ArrayList<>()).addAll(value));
			};


			Functions.biPermutation(this, t, this.isPriorityData(), t.isPriorityData(), (pri, sec) -> {
				sec.eventToXp().forEach((key, list) -> map.put(key, new ArrayList<>(list)));
				pri.eventToXp().forEach((key, list) -> map.put(key, list));
			},
			bothOrNeither,
			bothOrNeither);

			return new VanillaProgressionConfig(map, Optional.of(this.isPriorityData() || two.isPriorityData()));
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
