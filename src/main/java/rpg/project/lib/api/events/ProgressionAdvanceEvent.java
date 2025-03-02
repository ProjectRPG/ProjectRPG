package rpg.project.lib.api.events;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import rpg.project.lib.api.progression.ProgressionDataType;
import rpg.project.lib.internal.util.Reference;

/**
 * This event captures changes in progression.  This event should only be fired by progression systems when a
 * user has progressed past the system's built in milestones.  For example, in vanilla this would be when the
 * player gains a level.
 */
public class ProgressionAdvanceEvent extends PlayerEvent {
    final String container;
    final ProgressionDataType prior;
    final ProgressionDataType current;

    public static final ContextKey<ProgressionDataType> PROGRESS = new ContextKey<>(Reference.resource("progress"));
    public static final ContextKey<String> CONTAINER = new ContextKey<>(Reference.resource("container"));

    /**
     * @param player the player progressing
     * @param container the name of the container reaching the milestone
     * @param prior the progression before the current change
     * @param current the progression after the change.
     */
    public ProgressionAdvanceEvent(Player player, String container, ProgressionDataType prior, ProgressionDataType current) {
        super(player);
        this.container = container;
        this.prior = prior;
        this.current = current;
    }

    public String getContainer() {return container;}
    public ProgressionDataType getPrior() {return prior;}
    public ProgressionDataType getCurrent() {return current;}
}
