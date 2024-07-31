package rpg.project.lib.api.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.config.readers.MergeableCodecDataManager;
import rpg.project.lib.internal.util.MsLoggy;

/**
 * Members of this represent categories of objects within the game.  Each type
 * is connected to a data loader within the datapack.
 */
public enum ObjectType implements StringRepresentable{
	/**represents items in inventories.  Note that block items change form based
	 * on this detail and configurations will need to factor in this context.*/
	ITEM("prpg/items", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.ITEM)),
	/**represents blocks in the world.  Note that blocks in the inventory are items.*/
	BLOCK("prpg/blocks", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.BLOCK)),
	/**Entities include mobs, but also projectiles, item entities, and players.  Note:
	 * when referencing players as an entity type and not a specific player by name, they
	 * are captures via the entity id "minecraft:player", not as the PLAYER value of this.*/
	ENTITY("prpg/entities", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.ENTITY_TYPE)),
	/**Represents dimensions/levels in the world.*/
	DIMENSION("prpg/dimensions", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, null)),
	/**Represents the unique biomes in the world.*/
	BIOME("prpg/biomes", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.BIOME)),
	/**Represents enchantments that can be applied to items.*/
	ENCHANTMENT("prpg/enchantments", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.ENCHANTMENT)),
	/**Represents MobEffects that can be applied to entities.*/
	EFFECT("prpg/effects", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, Registries.MOB_EFFECT)),
	/**Represents event specifications registered to this library.  Since all behavior of
	 * Project RPG begins at the invocation of an event, configurations associated with
	 * events will have a global effect that applied independent of the subject object.*/
	EVENT("prpg/events", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, APIUtils.GAMEPLAY_EVENTS)),
	/**Represents specific users, NOT the player entity as a category.  Configurations for
	 * specific players is available for special behavior such as admin exemptions and
	 * handicaps.  Configuration files load using the player's UUID and are account-unique.*/
	PLAYER("prpg/players", id -> new MergeableCodecDataManager<>(id, DataLoader.DATA_LOGGER, ObjectType::mergeLoaderData, ObjectType::printData, null));
	private final String dataPath;
	private final Function<String, MergeableCodecDataManager<?>> loaderProvider;
	ObjectType(String dataPath, Function<String, MergeableCodecDataManager<?>> loaderProvider) {
		this.dataPath = dataPath;
		this.loaderProvider = loaderProvider;
	}
	public String getPath() {return dataPath;}
	public MergeableCodecDataManager<?> createLoader() {return loaderProvider.apply(dataPath);}

	private static final Logger DATA_LOGGER = LogManager.getLogger();
	private static MainSystemConfig mergeLoaderData(final List<MainSystemConfig> raws) {
		MainSystemConfig out = raws.stream().reduce((existing, element) -> (MainSystemConfig)existing.combine(element)).get();
		return out.isUnconfigured() ? null : out;
	}

	public static void printData(Map<ResourceLocation, ? extends Record> data) {
		data.forEach((id, value) -> {
			if (id == null || value == null) return;
			MsLoggy.INFO.log(MsLoggy.LOG_CODE.DATA, "Object: {} with Data: {}", id.toString(), value.toString());
		});
	}
	
	public static final Codec<ObjectType> CODEC = StringRepresentable.fromEnum(ObjectType::values);
	private static final Map<String, ObjectType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(ObjectType::getSerializedName, s -> s));
	public static ObjectType byName(String name) {return BY_NAME.get(name);}
	
	@Override
	public String getSerializedName() {return this.name();}
	public static ObjectType create(String name) {throw new IllegalStateException("Enum not extended");}
}
