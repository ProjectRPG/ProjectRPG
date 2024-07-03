package rpg.project.lib.builtins.vanilla;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.EventRegistry;

public record VanillaProgressionConfigType() implements SubSystemConfigType{
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("progression");
	public static final VanillaProgressionConfigType IMPL = new VanillaProgressionConfigType();

	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaProgressionConfig.CODEC;
	}

	public static record VanillaProgressionConfig(Map<ResourceLocation, Integer> eventToXp) implements SubSystemConfig {
		
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("events").forGetter(ssc -> ((VanillaProgressionConfig)ssc).eventToXp())
				).apply(instance, VanillaProgressionConfig::new));

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaProgressionConfig t = (VanillaProgressionConfig) two;
			var map = new HashMap<>(this.eventToXp());
			t.eventToXp().forEach((key, value) -> map.merge(key, value, Integer::max));
			return new VanillaProgressionConfig(map);
		}

		@Override
		public boolean isUnconfigured() {
			return this.eventToXp().isEmpty();
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
			return new VanillaProgressionConfig(EventRegistry.EVENTS.getRegistry().get()
					.keySet().stream().collect(Collectors.toMap(rl -> rl, rl -> 0)));
		}		
	}
}
