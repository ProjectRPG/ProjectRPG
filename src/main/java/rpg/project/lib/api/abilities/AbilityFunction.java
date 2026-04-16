package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.events.EventContext;

/**When an ability starts or stops, this interface is invoked to handle the behavior
 */
@FunctionalInterface
public interface AbilityFunction {
    /**Executes an ability behavior once per ability invocation.
     *
     * @param player the player executing the ability
     * @param settings the config settings from data
     * @param context the event context provided for the event that triggered the ability
     */
    void start(Player player, CompoundTag settings, EventContext context);
    AbilityFunction NOOP = (player, settings, context) -> {};
}
