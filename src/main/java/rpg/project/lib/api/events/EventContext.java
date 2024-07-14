package rpg.project.lib.api.events;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import org.jetbrains.annotations.Nullable;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.Reference;

/**Contains a key-value pair system for storing, retrieving, and modifying values related to events.  Interacting
 * with this class is the preferred method for operating on event-based values.  Event implementations construct
 * instances of this class as part of their {@link EventListenerSpecification} which conform to how their event
 * expects to provide and callback specific values.*/
public class EventContext{
	/**The object type and id for the subject of this event.  Note that other objects can be passed in context,
	 * however this pair specifically refers to the subject of this event and is used by sub systems to isolate
	 * relevant parameters*/
	private final Pair<ObjectType, ResourceLocation> subjectObject;
	/**Populated by {@link ContextBuilder} as immutable properties of the context.  Values added to this map are
	 * expected to never change and that doing so would create instability in the event sequence.*/
	private final Map<LootContextParam<?>, Object> contextParams;
	/**May be populated with initial values via {@link ContextBuilder}, however this map contains mutable values
	 * that subsystems can add and modify with {@link #setParam(LootContextParam, Object)}.
	 * Values in this map are typically used for event callbacks where the event exposes properties to a subsystem
	 * expecting to then re-consume that value at some point in the event.
	 * <h4>Note: {@link LootContextParam} keys in {@link #contextParams} will always be returned instead of the same
	 * key in this map.  Therefore, it is important to use unique keys in event implementations with similar values.</h4>*/
	private final Map<LootContextParam<?>, Object> dynamicParams;

	protected EventContext(Pair<ObjectType, ResourceLocation> subjectObject, Map<LootContextParam<?>, Object> contextParams, Map<LootContextParam<?>, Object> dynamicParams) {
		this.subjectObject = subjectObject;
		this.contextParams = contextParams;
		this.dynamicParams = dynamicParams;
	}
	public static final LootContextParam<Player> PLAYER = new LootContextParam<>(Reference.resource("actor"));
	public static final LootContextParam<LevelAccessor> LEVEL = new LootContextParam<>(Reference.resource("event_level"));
	public static final LootContextParam<ItemStack> ITEMSTACK = new LootContextParam<>(Reference.resource("itemstack"));
	public static final LootContextParam<Integer> BREATH_CHANGE = new LootContextParam<>(Reference.resource("breath_change"));
	public static final LootContextParam<AgeableMob> BABY = new LootContextParam<>(Reference.resource("baby"));
	public static final LootContextParam<Mob> PARENT_A = new LootContextParam<>(Reference.resource("parent_a"));
	public static final LootContextParam<Mob> PARENT_B = new LootContextParam<>(Reference.resource("parent_b"));
	public static final LootContextParam<MobEffectInstance> MOB_EFFECT = new LootContextParam<>(Reference.resource("mob_effect"));
	public static final LootContextParam<Boolean> CANCELLED = new LootContextParam<>(Reference.resource("event_cancelled"));

	public ObjectType getSubjectType() {return subjectObject.getFirst();}
	public ResourceLocation getSubjectID() {return subjectObject.getSecond();}

	public boolean hasParam(LootContextParam<?> param) {
		return contextParams.containsKey(param) || dynamicParams.containsKey(param);
	}

	/**Return the value associated with the {@link LootContextParam} key.  If a key is associated with
	 * both an immutable value and a mutable value, the immutable one will be returned, else if no value
	 * is present, null is returned.
	 *
	 * @param param the key associated with the desired value
	 * @return the value if present, else null
	 * @param <T> the object class related to this key.
	 */
	@Nullable
	@SuppressWarnings("unchecked") //public setters enforce key-value type parity
	public <T> T getParam(LootContextParam<T> param) {
		return (T) (contextParams.containsKey(param)
                        ? contextParams.get(param)
                        : dynamicParams.get(param));
	}

	/**Updates and existing parameter to the <b>mutable</b> internal map or adds if not previously assigned.
	 *
	 * @param key the key associated with this entry.
	 * @param value the value being added/updated
	 * @param <T> the object class of the value
	 */
	public <T> void setParam(LootContextParam<T> key, T value) {
		dynamicParams.put(key, value);
	}

	/**A helper method for accessing the player associated with this context.
	 *
	 * @return the player associated with this event.
	 */
	public Player getActor() {return (Player) contextParams.get(PLAYER);}

	/**A helper method for access the level associated with this context.
	 *
	 * @return the level associated with this event.
	 */
	public LevelAccessor getLevel() {return (LevelAccessor) contextParams.get(LEVEL);}

	/**Creates a new {@link ContextBuilder} with the required initial properties.  Every event represents a specific
	 * scenario in which the player interacts with a subject object.  This builder ensures the required objects for
	 * referencing that relationship exist for reference by the subsystems.
	 *
	 * @param subjectID the registry ID for the subject object
	 * @param subjectParam the parameter key used to recall the subject object from the context
	 * @param subject the subject object itself.
	 * @param actor the player this event is related to.
	 * @param level the level/dimension this event is taking place within.
	 * @return a new {@link ContextBuilder}
	 * @param <T> the object class of the subject object.
	 */
	public static <T> ContextBuilder build(ResourceLocation subjectID, LootContextParam<T> subjectParam, T subject, Player actor, LevelAccessor level) {
		return new ContextBuilder(subjectID, subjectParam, subject)
				.withParam(PLAYER, actor)
				.withParam(LEVEL, level);
	}

	/**Creates a special {@link ContextBuilder} where the player is also the subject of the event.  This builder
	 * is ideal for player behavior such as jumping, sprinting, breathing, healing, swimming, etc, where there is
	 * no worldly object interacting with the player to predicate or facilitate the behavior.
	 *
	 * @param actor the player
	 * @param level the level from the event.
	 * @return a new {@link ContextBuilder}
	 */
	public static ContextBuilder self(Player actor, LevelAccessor level) {
		return new ContextBuilder(ResourceLocation.withDefaultNamespace("player"), PLAYER, actor)
				.withParam(LEVEL, level);
	}

	@Override
	public String toString() {
		return "EventContext{" +
				"subjectObject=" + subjectObject +
				", contextParams=" + MsLoggy.mapToString(contextParams) +
				", dynamicParams=" + MsLoggy.mapToString(dynamicParams) +
				'}';
	}

	public static class ContextBuilder {
		private final Pair<ObjectType, ResourceLocation> subjectReference;
		private final Map<LootContextParam<?>, Object> contextParams = new HashMap<>();
		private final Map<LootContextParam<?>, Object> dynamicParams = new HashMap<>();
		protected <T> ContextBuilder(ResourceLocation subjectID, LootContextParam<T> subjectParam, T subject) {
			this.subjectReference = Pair.of(getType(subject), subjectID);
			contextParams.put(subjectParam, subject);
		}

		/**Adds an immutable value to the context.  These values cannot be changed by subsystems.
		 *
		 * @param paramKey the identifying key to obtain the associated value
		 * @param param the value associated with the key
		 * @return this {@link ContextBuilder}
		 * @param <T> the object class for the value
		 */
		public <T> ContextBuilder withParam(LootContextParam<T> paramKey, T param) {
			contextParams.put(paramKey, param);
			return this;
		}

		/**Adds a mutable value to the context.  These values are expected to be changed by subsystems either
		 * to inform other subsystems or as callbacks to the event itself.
		 *
		 * @param paramKey the identifying key to obtain the associated value
		 * @param param the value associated with the key
		 * @return this {@link ContextBuilder}
		 * @param <T> the object class for the value
		 */
		public <T> ContextBuilder withDynamicParam(LootContextParam<T> paramKey, T param) {
			dynamicParams.put(paramKey, param);
			return this;
		}

		/**@return a fully constructed {@link EventContext}*/
		public EventContext create() {return new EventContext(subjectReference, contextParams, dynamicParams);}

		/**Grabs the {@link ObjectType} from the object itself.  This functionality relies on mixins which inject
		 * the {@link DataObject} interface onto vanilla classes representing superclasses of registry objects.
		 * Each mixin implements the {@link DataObject#prpg$getObjectType()} method with the correct enum value for
		 * the injected class and allows this method to provide that value.
		 *
		 * @param subject the object being checked for the interface and associated type.
		 * @return the associated type or the PLAYER fallback.
		 */
		private static ObjectType getType(Object subject) {
			if (subject instanceof DataObject obj) return obj.prpg$getObjectType();
			return ObjectType.PLAYER; //default to the least likely used data type to avoid null but not load real data.
		}
	}
}
