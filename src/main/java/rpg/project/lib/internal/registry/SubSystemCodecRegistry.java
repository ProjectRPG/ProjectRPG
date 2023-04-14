package rpg.project.lib.internal.registry;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;

public class SubSystemCodecRegistry {
	private static final BiMap<ResourceLocation, SubSystemConfigType> TYPES = HashBiMap.create();
	
	public static Codec<SubSystemConfigType> CODEC = ResourceLocation.CODEC.xmap(TYPES::get, TYPES.inverse()::get);
	
	public static void registerSubSystem(ResourceLocation identity, SubSystemConfigType type) {
		Preconditions.checkNotNull(identity);
		Preconditions.checkNotNull(type);
		TYPES.put(identity, type);
	}
}
