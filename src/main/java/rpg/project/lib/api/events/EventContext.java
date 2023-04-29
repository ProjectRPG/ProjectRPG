package rpg.project.lib.api.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import rpg.project.lib.api.data.ObjectType;

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
		Player actor,
		/**A specified level for the event.  The {@link Builder} defaults 
		 * this to the level held by the player instance.  Events may 
		 * specify another level if different in the context of the event.*/
		Level level,
		/**Applicable locations for the event.*/
		List<BlockPos> pos,
		/**Applicable entities for the event*/
		List<Entity> entities,
		/**Applicable Itemstacks for the event*/
		List<ItemStack> items,
		/**<p>An object containing otherwise unspecified values.  This allows
		 * events to provide data to context consumers that is irrelevant
		 * or otherwise too niche to be declared as a standard EventContext
		 * attribute.</p>
		 * <p>It is the burden of consumers to test for the presence of 
		 * properties in this tag if needed.  Additionally, if the consumer
		 * is inserting properties into this map, they should convey this
		 * intent to users of the consumer feature.</p>*/
		CompoundTag dynamicVariables) {
	
	/**@see #dynamicVariables*/
	public CompoundTag dynamicVariables() {
		return dynamicVariables.copy();
	}
	
	/**Creates a new builder instance for easily creating an 
	 * {@link EventContext}.  The builder provides default values
	 * for most parameters to prevent null cases.
	 * 
	 * @param subjectType the object type this event is centered on
	 * @param subjectID the id of the subject object
	 * @param player the player relevant to this event
	 * @return a new {@link Builder}
	 */
	public static Builder build(ObjectType subjectType, ResourceLocation subjectID, Player player) {
		return new Builder(subjectType, subjectID, player);
	}
	
	/**A helper class for creating an {@link EventContext}*/
	public static class Builder {
		private Pair<ObjectType, ResourceLocation> subject;
		private Player actor;
		private Level level;
		private List<BlockPos> posList = new ArrayList<>();
		private List<Entity> entityList = new ArrayList<>();
		private List<ItemStack> stackList = new ArrayList<>();
		private CompoundTag dynamicVars = new CompoundTag();
		
		protected Builder(ObjectType subjectType, ResourceLocation subjectID, Player actor) {
			subject = Pair.of(subjectType, subjectID);
			this.actor = actor;
			this.level = actor.getLevel();
		}
		/**Includes a position value for this context.
		 * 
		 * @param pos a world position applicable to this event
		 * @return the builder instance
		 */
		public Builder withPos(BlockPos pos) {
			this.posList.add(pos);
			return this;
		}
		/**Includes multiple positions in this context.  This
		 * is useful for providing locations for events with 
		 * multiple affected locations, such as explosions.
		 * 
		 * @param pos a collection of positions
		 * @return the builder instance
		 */
		public Builder withPos(Collection<BlockPos> pos) {
			this.posList.addAll(pos);
			return this;
		}
		/**Includes multiple entity instances in this context.
		 * This should be used to provide the actual entity 
		 * instance associated with the subject object ID.
		 * 
		 * @param entity a collection of entities
		 * @return the builder instance
		 */
		public Builder withEntity(Collection<Entity> entity) {
			this.entityList.addAll(entity);
			return this;
		}
		/**Includes multiple item stack instances in this
		 * context.  This should be used to provide actual
		 * stack instances associated with the subject object
		 * ID.
		 * 
		 * @param stack a collection of {@link ItemStack}s
		 * @return the builder instance
		 */
		public Builder withStacks(Collection<ItemStack> stack) {
			this.stackList.addAll(stack);
			return this;
		}
		/**The level is obtained from the player object by
		 * default.  If this is invoked another level instance
		 * can be used instead.  
		 * 
		 * @param level a specific level instance
		 * @return the builder instance
		 */
		public Builder withLevel(Level level) {
			this.level = level;
			return this;
		}
		/**Sets the dynamic variables provided by this event.
		 * 
		 * @param nbt the object containing the dynamic values
		 * @return the builder instance
		 */
		public Builder withDynamicVariables(CompoundTag nbt) {
			this.dynamicVars.merge(nbt);
			return this;
		}
		/**@return a fully formed {@link EventContext} with the 
		 * constructed values */
		public EventContext build() {
			return new EventContext(subject, actor, level, posList, entityList, stackList, dynamicVars);
		}
	}
}
