package rpg.project.lib.internal.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.Ability;

public class RegistryUtil {
	public static <T> Identifier getId(RegistryAccess access, ResourceKey<Registry<T>> registry, T source) {
		return access.lookupOrThrow(registry).getKey(source);
	}

	public static Identifier getId(RegistryAccess access, Item item) {
		return getId(access, Registries.ITEM, item);
	}
	public static Identifier getId(RegistryAccess access, ItemStack stack) {return getId(access, stack.getItem());}

	public static Identifier getId(RegistryAccess access, Block block) {
		return getId(access, Registries.BLOCK, block);
	}
	public static Identifier getId(RegistryAccess access, BlockState state) {return getId(access, state.getBlock());}

	public static Identifier getId(RegistryAccess access, EntityType<?> entity) {
		return getId(access, Registries.ENTITY_TYPE, entity);
	}
	public static Identifier getId(RegistryAccess access, Entity entity) {return getId(access, entity.getType());}

	public static Identifier getId(Holder<?> biome) {
		return biome.unwrapKey().get().identifier();
	}

	public static Identifier getId(SoundEvent sound) {
		return BuiltInRegistries.SOUND_EVENT.getKey(sound);
	}

	public static Identifier getDimension(LevelAccessor level) {
		return level.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).getKey(level.dimensionType());
	}
}
