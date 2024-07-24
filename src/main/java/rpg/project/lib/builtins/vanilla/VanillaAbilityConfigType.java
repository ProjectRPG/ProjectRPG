package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;

public record VanillaAbilityConfigType() implements SubSystemConfigType {
	public static final ResourceLocation ID = ResourceLocation.withDefaultNamespace("abilities");
	public static final VanillaAbilityConfigType IMPL = new VanillaAbilityConfigType();

	@Override
	public MapCodec<SubSystemConfig> getCodec() {
		return VanillaAbilityConfig.CODEC;
	}

	public record VanillaAbilityConfig(List<CompoundTag> data) implements SubSystemConfig {
		public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
				CompoundTag.CODEC.listOf().fieldOf("configurations").forGetter(ssc -> ((VanillaAbilityConfig)ssc).data())
				).apply(instance, VanillaAbilityConfig::new));

		@Override
		public MergeableData combine(MergeableData two) {
			List<CompoundTag> base = new ArrayList<>(this.data);
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

		@Override
		public SubSystemConfig getDefault() {
			return new VanillaAbilityConfig(List.of(new CompoundTag()));
		}		
	}
}
