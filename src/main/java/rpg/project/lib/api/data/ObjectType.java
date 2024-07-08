package rpg.project.lib.api.data;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.util.StringRepresentable;

/**
 * Members of this represent categories of objects within the game.  Each type
 * is connected to a data loader within the datapack.
 */
public enum ObjectType implements StringRepresentable{
	/**represents items in inventories.  Note that block items change form based
	 * on this detail and configurations will need to factor in this context.*/
	ITEM,
	/**represents blocks in the world.  Note that blocks in the inventory are items.*/
	BLOCK,
	/**Entities include mobs, but also projectiles, item entities, and players.  Note:
	 * when referencing players as an entity type and not a specific player by name, they
	 * are captures via the entity id "minecraft:player", not as the PLAYER value of this.*/
	ENTITY,
	/**Represents dimensions/levels in the world.*/
	DIMENSION,
	/**Represents the unique biomes in the world.*/
	BIOME,
	/**Represents enchantments that can be applied to items.*/
	ENCHANTMENT,
	/**Represents MobEffects that can be applied to entities.*/
	EFFECT,
	/**Represents event specifications registered to this library.  Since all behavior of
	 * Project RPG begins at the invocation of an event, configurations associated with
	 * events will have a global effect that applied independent of the subject object.*/
	EVENT,
	/**Represents specific users, NOT the player entity as a category.  Configurations for
	 * specific players is available for special behavior such as admin exemptions and
	 * handicaps.  Configuration files load using the player's UUID and are account-unique.*/
	PLAYER;
	
	public static final Codec<ObjectType> CODEC = StringRepresentable.fromEnum(ObjectType::values);
	private static final Map<String, ObjectType> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(ObjectType::getSerializedName, s -> s));
	public static ObjectType byName(String name) {return BY_NAME.get(name);}
	
	@Override
	public String getSerializedName() {return this.name();}
	public static ObjectType create(String name) {throw new IllegalStateException("Enum not extended");}
}
