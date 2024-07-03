package rpg.project.lib.builtins.vanilla;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;

public record VanillaBonusConfigType() implements SubSystemConfigType {
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("bonus");
	public static final VanillaBonusConfigType IMPL = new VanillaBonusConfigType();

	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaBonusConfig.CODEC;
	}

	public record VanillaBonusConfig(Map<String, List<SubSystemConfig>> values) implements SubSystemConfig {
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.unboundedMap(Codec.STRING, Codec.list(APIUtils.getDispatchCodec().dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec))
					).fieldOf("bonuses").forGetter(ssc -> ((VanillaBonusConfig)ssc).values())
				).apply(instance, VanillaBonusConfig::new));

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaBonusConfig other = (VanillaBonusConfig) two;
			Map<String,List<SubSystemConfig>> combined = other.values();
			for (Map.Entry<String, List<SubSystemConfig>> entry : values().entrySet()) {
				if (combined.containsKey(entry.getKey())) {
					List<SubSystemConfig> mergedList = Stream.concat(combined.get(entry.getKey()).stream(), entry.getValue().stream())
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

		@Override
		public SubSystemConfig getDefault() {
			return new VanillaBonusConfig(new HashMap<>());
		}
	}
}
