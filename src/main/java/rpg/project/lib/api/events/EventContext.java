package rpg.project.lib.api.events;

import java.util.HashMap;
import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.DataObject;
import rpg.project.lib.internal.util.Reference;

/**<p>This translates a specific event into the attributes needed by all
 * consuming systems.</p>  
 */
public record EventContext(
		/**The object type and id for the subject of this event.  Note 
		 * that other objects can be passed in context, however this 
		 * pair specifically refers to the subject of this event and 
		 * is used by sub systems to isolate relevant parameters */
		Pair<ObjectType, ResourceLocation> subjectObject,
		/**The player responsible for triggering the event and for whom
		 * any progress should be applied to.*/
		Map<LootContextParam<?>, Object> contextParams,
		Map<LootContextParam<?>, Object> dynamicParams) {
	public static final LootContextParam<LevelAccessor> LEVEL = new LootContextParam<>(Reference.resource("event_level"));
	public static final LootContextParam<ItemStack> ITEMSTACK = new LootContextParam<>(Reference.resource("itemstack"));

	public boolean hasParam(LootContextParam<?> param) {
		return contextParams().containsKey(param) || dynamicParams().containsKey(param);
	}

	@SuppressWarnings("unchecked") //public setters enforce key-value type parity
	public <T> T getParam(LootContextParam<T> param) {
		return (T) (contextParams().containsKey(param)
                        ? contextParams().get(param)
                        : dynamicParams().get(param));
	}
	public <T> void addParam(LootContextParam<T> key, T value) {
		dynamicParams().put(key, value);
	}
	public <T> void setParam(LootContextParam<T> key, T newValue) {
		addParam(key, newValue);
	}
	public Player getActor() {return (Player) contextParams().get(LootContextParams.THIS_ENTITY);}
	public static <T> ContextBuilder build(ResourceLocation subjectID, LootContextParam<T> subjectParam, T subject, Player actor, LevelAccessor level) {
		return new ContextBuilder(subjectID, subjectParam, subject, actor, level);
	}

	public static class ContextBuilder {
		private final Pair<ObjectType, ResourceLocation> subjectReference;
		private final Map<LootContextParam<?>, Object> contextParams = new HashMap<>();
		protected <T> ContextBuilder(ResourceLocation subjectID, LootContextParam<T> subjectParam, T subject, Player actor, LevelAccessor level) {
			this.subjectReference = Pair.of(getType(subject), subjectID);
			contextParams.put(subjectParam, subject);
			contextParams.put(LootContextParams.THIS_ENTITY, actor);
			contextParams.put(LEVEL, level);
		}
		public <T> ContextBuilder withParam(LootContextParam<T> paramKey, T param) {
			contextParams.put(paramKey, param);
			return this;
		}
		public EventContext create() {return new EventContext(subjectReference, contextParams, new HashMap<>());}

		private static ObjectType getType(Object subject) {
			if (subject instanceof DataObject obj) return obj.prpg$getObjectType();
			return ObjectType.PLAYER; //default to the least likely used data type to avoid null but not load real data.
		}
	}
}
