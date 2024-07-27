package rpg.project.lib.api.progression;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.events.EventContext;

/**<p>Only one progression system can be active in a loaded world.
 * This system manages all advancement within the game for each
 * player and tracks the attributes which distinguish novice 
 * players from experienced ones.</p>  
 * <p>The progression system operates under the assumption of 
 * containers.  Containers are an ambiguous concept that partitions
 * progress.  Project RPG then requests access to these partitions
 * through their respective name.  A progression system does not need
 * to partition progress.  For example, vanilla minecraft has only one
 * "experience" bucket that each player has and therefore nothing to
 * partition.  In this case, the container name is irrelevant and can
 * be ignored.  However, your implementation may use booleans as the
 * progess type where progress is handled entirely by which partitions
 * are true.  In this case values may be irrelevant and only container
 * names matter.</p>
 * <p>Regardless of your implementation, other Project RPG systems will
 * expect to supply a container name when accessing progress information.
 * For example, a gating system may be configured to prevent a player
 * from performing a dig action if they don't have X progress in Y
 * container.  whether or not Y is necessary or not, other systems will
 * provide it.</p>
 *
 * @param <T> the object used to serialize progress values
 */
public interface ProgressionSystem<T extends ProgressionDataType> {
	/**Provides the value associated with the specified container and
	 * player.  
	 * 
	 * @param playerID the unique identifier of the player being queried
	 * @param container the name of the progress container being queried
	 * @return the value associated with the container and player
	 */
	T getProgress(UUID playerID, String container);
	
	/**<p>Sets the progress for the specified player and container.</p>
	 * <p>Note: this is intended to be a hard override of the container's
	 * progress value and should not be used to increment progress.  
	 * Behavior such as increments should be implemented separately and 
	 * routed through systemic behavior such as the {@link Runnable} used
	 * by {@link #getProgressionToBeAwarded(Hub, ResourceLocation, EventContext)}.</p>
	 * 
	 * @param playerID the unique identifier of the player being modified
	 * @param container the name of the progress container being modified
	 * @param value the new value of the progress container
	 */
	void setProgress(UUID playerID, String container, T value);
	
	/**Used by the event system to award progression.  This method should
	 * interpret the applicable progression configuration data for the 
	 * provided event context to provide a list of pairs for each applicable 
	 * container with a function to commit that progress. The event system
	 * uses the returned list and queries the gating system for all containers
	 * for a percentage of permissability which is then consumed per this
	 * system according to what partial permission means.
	 * 
	 * @param core access to the {@link Hub} implementation
	 * @param eventID the event ID which this is being invoked for
	 * @param context the relevant context variables for the event
	 * @return a list of pairs containing the container name and an associated
	 * consumer which takes the permission threshold and commits progress for 
	 * that container
	 */
	List<Pair<String, Consumer<Float>>> getProgressionToBeAwarded(Hub core, ResourceLocation eventID, EventContext context);
	
	/**<p>This method is called once during server startup to set the progression
	 * commands for the entire ecosystem.  If your implementation works
	 * with the default command set, you can return null to have this 
	 * ignored in favor of keeping the defaults.</p>
	 * <p><i>NOTE: your implementation replaces "<code>/rpg progress</code>" with
	 * "<code>/rpg yourLiteralKeyword</code>"</i></p>
	 * 
	 * @return a replacement command tree for the progress literal
	 */
	LiteralArgumentBuilder<CommandSourceStack> getCommands();
	
	/**@return the data type other systems can use to access the {@link SubSystemConfig}
	 * associated with the stored data of this progression system.
	 */
	SubSystemConfigType dataType();

	/**@return a provider which supplies the side panel display for information this progression
	 * system wants to present to the player.
	 */
	SidePanelContentProvider getSidePanelProvider();
}
