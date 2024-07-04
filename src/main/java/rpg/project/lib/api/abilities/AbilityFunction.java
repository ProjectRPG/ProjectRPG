package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.events.EventContext;

@FunctionalInterface
public interface AbilityFunction {
    void start(Player player, CompoundTag settings, EventContext context);
    AbilityFunction NOOP = (player, settings, context) -> {};
}
