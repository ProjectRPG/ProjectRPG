package rpg.project.lib.api.events;

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
		@Nullable BlockPos pos) {
	
	public static Builder build(Player player) {
		return new Builder(player);
	}
	
	public static class Builder {
		private Player actor;
		private Level level;
		private BlockPos pos = null;
		protected Builder(Player actor) {
			this.actor = actor;
			this.level = actor.getLevel();
		}
		public Builder withPos(BlockPos pos) {
			this.pos = pos;
			return this;
		}
		public Builder withLevel(Level level) {
			this.level = level;
			return this;
		}
		public EventContext build() {
			return new EventContext(actor, level, pos);
		}
	}
}
