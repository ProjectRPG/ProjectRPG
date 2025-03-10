package rpg.project.lib.builtins.vanilla;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;

public record VanillaPartyConfigType() implements SubSystemConfigType{
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("party");
	public static final VanillaPartyConfigType IMPL = new VanillaPartyConfigType();

	@Override
	public ResourceLocation getId() {return ID;}
	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaPartyConfig.CODEC;
	}

	@Override
	public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaPartyConfig();}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.PARTY);
	}

	@Override
	public SubSystemConfig fromScript(Map<String, String> values) {
		return new VanillaPartyConfig();
	}


	public record VanillaPartyConfig() implements SubSystemConfig {
		
		public static final MapCodec<SubSystemConfig> CODEC = MapCodec.unit(new VanillaPartyConfig());

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
	}
}
