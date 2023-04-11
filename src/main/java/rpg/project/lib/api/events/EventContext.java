package rpg.project.lib.api.events;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/** TODO: <p>populate this with things we need for processing gates,
 * progression, abilities, and feature hooks.  The idea is that
 * an {@link rpg.project.lib.api.events.EventListenerSpecification} 
 * translates a specific event into the attributes we need.</p>  
 * <p>For example: suppose Event A uses <code>event.getPlayer()</code>
 * to obtain the relevant player who performed the action but Event B
 * is entity generic and so uses <code>event.getEntityLiving()</code>.
 * <br>We only care that we get a player instance in the end that we 
 * can check permissions for and award progression.</p>
 * <p>In addition to standard context variables, we also have properties
 * of events that are unique to the event but might be relevant to a 
 * feature.  For example, in PMMO the damage events send the damage 
 * value for Perks to use.  Only these events have this value and any 
 * perk dependent on this value has to account for the fact it might
 * not receive this value if the user configures the perk for an event
 * that does not supply it.  PMMO uses a CompoundTag to get a "dictionary"
 * of values, but that implementation is up for debate.
 */
public record EventContext(
		/**The player responsible for triggering the event and for whom
		 * any progress should be applied to.*/
		Player actor,
		/**A specified level for the event.  The {@link Builder} defaults 
		 * this to the level held by the player instance.  Events may 
		 * specify another level if different in the context of the event.*/
		Level level,
		/**An applicable location for the event.*/
		@Nullable List<BlockPos> pos) {
	
	public static Builder build(Player player) {
		return new Builder(player);
	}
	
	public static class Builder {
		private Player actor;
		private Level level;
		private List<BlockPos> posList = new ArrayList<>();
		
		protected Builder(Player actor) {
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
		/**@return a fully formed {@link EventContext} with the 
		 * constructed values */
		public EventContext build() {
			return new EventContext(actor, level, posList);
		}
	}
}
