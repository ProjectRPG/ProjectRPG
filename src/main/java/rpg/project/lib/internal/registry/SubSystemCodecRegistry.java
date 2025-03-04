package rpg.project.lib.internal.registry;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.LinkedListMultimap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

public class SubSystemCodecRegistry {
	public static Codec<SubSystemConfigType> CODEC = ResourceLocation.CODEC.xmap(
			rl -> CommonSetup.CODECS.getRegistry().get().getOptional(rl).orElse(DefaultType.IMPL),
			SubSystemConfigType::getId);

	public static List<SubSystemConfig> getDefaults(APIUtils.SystemType relation, RegistryAccess access) {
		return access.lookupOrThrow(APIUtils.SUBSYSTEM_CODECS).stream()
				.filter(system -> system.applicableSystemTypes().contains(relation))
				.map(config -> config.getDefault(access)).toList();
	}
	
	public record DefaultType() implements SubSystemConfigType {
		public static final ResourceLocation ID = Reference.resource("missing_subsystem_config");
		public static final DefaultType IMPL = new DefaultType();

		@Override
		public ResourceLocation getId() {return ID;}
		@Override
		public MapCodec<SubSystemConfig> getCodec() { return DefaultConfig.CODEC;
		}

		@Override
		public SubSystemConfig getDefault(RegistryAccess access) {return new DefaultConfig();}

		@Override
		public EnumSet<APIUtils.SystemType> applicableSystemTypes() {
			return EnumSet.noneOf(APIUtils.SystemType.class);
		}

		public record DefaultConfig() implements SubSystemConfig {
			public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
					Codec.BOOL.optionalFieldOf("placeholder").forGetter(o -> Optional.of(true))
					).apply(instance, bool -> new DefaultConfig()));

			@Override
			public MergeableData combine(MergeableData two) {return this;}

			@Override
			public boolean isUnconfigured() {return false;}

			@Override
			public SubSystemConfigType getType() {return IMPL;}

			@Override
			public MapCodec<SubSystemConfig> getCodec() {return CODEC;}
		}
	}
}
