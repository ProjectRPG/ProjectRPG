package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.Core;

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
	public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaAbilityConfig(AbilityUtils.get(access).getDefaults());}

	@Override
	public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
		return EnumSet.of(APIUtils.SystemType.ABILITY);
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
	}
}
