package rpg.project.lib.internal.registry;

import java.util.Optional;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.util.Reference;

public class SubSystemCodecRegistry {
	private static final BiMap<ResourceLocation, SubSystemConfigType> TYPES = HashBiMap.create();
	
	public static Codec<SubSystemConfigType> CODEC = ResourceLocation.CODEC.xmap(rl -> TYPES.getOrDefault(rl, DefaultType.IMPL), type -> TYPES.inverse().getOrDefault(type, DefaultType.ID));
	
	public static void registerSubSystem(ResourceLocation identity, SubSystemConfigType type) {
		Preconditions.checkNotNull(identity);
		Preconditions.checkNotNull(type);
		TYPES.put(identity, type);
	}
	
	public static record DefaultType() implements SubSystemConfigType {
		public static final ResourceLocation ID = new ResourceLocation(Reference.MODID, "placeholder");
		public static final DefaultType IMPL = new DefaultType();

		@Override
		public Codec<SubSystemConfig> getCodec() { return DefaultConfig.CODEC;
		}
		
		public static record DefaultConfig() implements SubSystemConfig {
			public static final Codec<SubSystemConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
					Codec.BOOL.optionalFieldOf("placeholder").forGetter(o -> Optional.of(true))
					).apply(instance, bool -> new DefaultConfig()));

			@Override
			public MergeableData combine(MergeableData two) {return this;}

			@Override
			public boolean isUnconfigured() {return false;}

			@Override
			public SubSystemConfigType getType() {return IMPL;}

			@Override
			public Codec<SubSystemConfig> getCodec() {return CODEC;}
			
		}
	}
}
