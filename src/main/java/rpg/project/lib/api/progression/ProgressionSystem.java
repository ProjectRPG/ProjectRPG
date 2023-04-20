package rpg.project.lib.api.progression;

import java.util.List;
import java.util.UUID;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.datafixers.util.Pair;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;

public interface ProgressionSystem<T> {
	/**
	 * 
	 * @param playerID
	 * @param container
	 * @return
	 */
	T getProgress(UUID playerID, String container);
	
	/**
	 * 
	 * @param playerID
	 * @param container
	 * @param value
	 */
	void setProgress(UUID playerID, String container, T value);
	
	/**Used by the event system to award progression.  This method should
	 * interpret the applicable progression configuration data for the 
	 * provided event context to provide a list of pairs for each applicable 
	 * container with a function to commit that progress. The event system
	 * uses the returned list to test against the gating system and all
	 * permitted containers will execute their paired runnable
	 * 
	 * @param core access to the {@link Hub} implementation
	 * @param eventID the event ID which this is being invoked for
	 * @param context the relevant context variables for the event
	 * @return a list of pairs containing the container name and an associated
	 * runnable which commits progress for that container
	 */
	List<Pair<String, Runnable>> getProgressionToBeAwarded(Hub core, ResourceLocation eventID, EventContext context);
	
	/**
	 * 
	 * @return
	 */
	LiteralArgumentBuilder<CommandSourceStack> getCommands();
}
