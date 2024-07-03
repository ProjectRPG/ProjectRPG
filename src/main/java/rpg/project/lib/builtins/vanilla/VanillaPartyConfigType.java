package rpg.project.lib.builtins.vanilla;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;

public record VanillaPartyConfigType() implements SubSystemConfigType{
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("party");
	public static final VanillaPartyConfigType IMPL = new VanillaPartyConfigType();

	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaPartyConfig.CODEC;
	}

	public static record VanillaPartyConfig() implements SubSystemConfig {
		
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("placeholder").forGetter(vpc -> Optional.of(true)))
				.apply(instance, a -> new VanillaPartyConfig()));

		@Override
		public MergeableData combine(MergeableData two) {
			return this;
		}

		@Override
		public boolean isUnconfigured() {
			return false;
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
			return new VanillaPartyConfig();
		}		
	}
}
