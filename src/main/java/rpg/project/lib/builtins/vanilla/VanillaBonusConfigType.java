package rpg.project.lib.builtins.vanilla;

import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.conditions.ConditionWrapper;
import rpg.project.lib.api.progression.ProgressionDataType;
import rpg.project.lib.internal.util.Reference;

public record VanillaBonusConfigType() implements SubSystemConfigType {
	public static final Identifier ID = Identifier.withDefaultNamespace("bonus");
	public static final VanillaBonusConfigType IMPL = new VanillaBonusConfigType();

	@Override
	public Identifier getId() {return ID;}
	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaBonusConfig.CODEC;
	}

	@Override
	public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaBonusConfig(Map.of(Reference.resource("placeholder"), List.of()));}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.PROGRESSION);
	}

	@Override
	public SubSystemConfig fromScript(Map<String, String> values) {
		//TODO get bonus scripting. which should be fun since i'm not sure what this does to begin with.
		return new VanillaBonusConfig(Map.of());
	}

	public record VanillaBonusConfig(Map<Identifier, List<Bonus>> values) implements SubSystemConfig {
		public record Bonus(ProgressionDataType.Modification modifier, ProgressionDataType value, Optional<ConditionWrapper> conditions) {
			public static final Codec<Bonus> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.STRING.xmap(ProgressionDataType.Modification::valueOf, ProgressionDataType.Modification::name).fieldOf("modifier").forGetter(Bonus::modifier),
					APIUtils.getDispatchCodec().dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec).fieldOf("value").forGetter(Bonus::value),
					ConditionWrapper.CODEC.optionalFieldOf("conditions").forGetter(Bonus::conditions)
			).apply(instance, (m,t,c) -> new Bonus(m, (ProgressionDataType) t,c)));
		}
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			Codec.unboundedMap(Identifier.CODEC, Bonus.CODEC.listOf()).fieldOf("values").forGetter(e -> ((VanillaBonusConfig)e).values())
		).apply(instance, VanillaBonusConfig::new));

		@Override
		public boolean isPriorityData() {return false;} //TODO make not false

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaBonusConfig other = (VanillaBonusConfig) two;
			Map<Identifier,List<Bonus>> combined = other.values();
			for (Map.Entry<Identifier, List<Bonus>> entry : values().entrySet()) {
				if (combined.containsKey(entry.getKey())) {
					List<Bonus> mergedList = Stream.concat(combined.get(entry.getKey()).stream(), entry.getValue().stream())
						.distinct().toList();
					combined.put(entry.getKey(), mergedList);
				}
				else
					combined.put(entry.getKey(), entry.getValue());
			}				
			return new VanillaBonusConfig(combined);
		}

		@Override
		public boolean isUnconfigured() {
			return values().isEmpty();
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
