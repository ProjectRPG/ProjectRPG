package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.events.EventContext;

/**When a duration is set on an ability, this interface executes every tick for the full
 * duration.  For abilities with no on-tick behavior, this can be a NOOP impl.
 */
@FunctionalInterface
public interface TickFunction {
    /**While an ability is active this is invoked by the event system for each tick the
     * associated ability is active.
     *
     * @param player the player executing this ability
     * @param settings the config from data
     * @param context the event context which initiated this ability
     * @param ticksElapsed the number of ticks this event has been ticking for.  the limit
     *                     is set by the DURATION value in the configuration.
     */
    void tick(Player player, CompoundTag settings, EventContext context, int ticksElapsed);
    TickFunction NOOP = (player , settings, context, ticks) -> {};
}
