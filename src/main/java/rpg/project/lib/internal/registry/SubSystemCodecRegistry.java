package rpg.project.lib.internal.registry;

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
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.util.Reference;

public class SubSystemCodecRegistry {
	public enum SystemType{ABILITY, FEATURE, GATE, PARTY, PROGRESSION, PROGRESSION_DATA}

	private static final BiMap<ResourceLocation, SubSystemConfigType> TYPES = HashBiMap.create();
	private static final LinkedListMultimap<SystemType, SubSystemConfigType> RELATIONS = LinkedListMultimap.create();
	
	public static Codec<SubSystemConfigType> CODEC = ResourceLocation.CODEC.xmap(rl -> TYPES.getOrDefault(rl, DefaultType.IMPL), type -> TYPES.inverse().getOrDefault(type, DefaultType.ID));
	
	public static void registerSubSystem(ResourceLocation identity, SubSystemConfigType type, SystemType relation) {
		Preconditions.checkNotNull(identity);
		Preconditions.checkNotNull(type);
		if (!TYPES.containsKey(identity)) {
			TYPES.put(identity, type);
			RELATIONS.put(relation, type);
		}
	}

	public static ResourceLocation lookup(SubSystemConfigType type) {return TYPES.inverse().get(type);}
	public static List<SubSystemConfig> getDefaults(SystemType relation, RegistryAccess access) {
		return RELATIONS.get(relation).stream().map(config -> config.getDefault(access)).toList();
	}
	
	public record DefaultType() implements SubSystemConfigType {
		public static final ResourceLocation ID = Reference.resource("placeholder");
		public static final DefaultType IMPL = new DefaultType();

		@Override
		public MapCodec<SubSystemConfig> getCodec() { return DefaultConfig.CODEC;
		}

		@Override
		public SubSystemConfig getDefault(RegistryAccess access) {return new DefaultConfig();}
		
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

			@Override
			public SubSystemConfig getDefault() {
				return new DefaultConfig();
			}			
		}
	}
}
