package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.events.EventContext;

@FunctionalInterface
public interface TickFunction {
    void tick(Player player, CompoundTag settings, EventContext context, int ticksElapsed);
    TickFunction NOOP = (player , settings, context, ticks) -> {};
}
