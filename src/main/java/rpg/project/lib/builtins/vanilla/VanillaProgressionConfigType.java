package rpg.project.lib.builtins.vanilla;

import java.util.HashMap;
import java.util.Map;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;

public record VanillaProgressionConfigType() implements SubSystemConfigType{
	public static final ResourceLocation ID = new ResourceLocation("minecraft:progression");
	public static final VanillaProgressionConfigType IMPL = new VanillaProgressionConfigType();

	@Override
	public Codec<SubSystemConfig> getCodec() {
		return VanillaProgressionConfig.CODEC;
	}

	public static record VanillaProgressionConfig(Map<ResourceLocation, Integer> eventToXp) implements SubSystemConfig {
		
		public static final Codec<SubSystemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).fieldOf("events").forGetter(ssc -> ((VanillaProgressionConfig)ssc).eventToXp())
				).apply(instance, VanillaProgressionConfig::new));

		@Override
		public MergeableData combine(MergeableData two) {
			VanillaProgressionConfig t = (VanillaProgressionConfig) two;
			var map = new HashMap<>(this.eventToXp());
			t.eventToXp().entrySet().forEach(entry -> map.merge(entry.getKey(), entry.getValue(), Integer::max));
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
		public Codec<SubSystemConfig> getCodec() {
			return CODEC;
		}
		
	}
}
