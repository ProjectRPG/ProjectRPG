package rpg.project.lib.api.abilities;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.events.EventContext;

/**Determines the specific behavior for how and when abilities
 * can be executed.  The abilities themselves can be provided 
 * by any addon, but only one addon can provide an ability system.
 */
public interface AbilitySystem {
	/**Provides the config {@link CompoundTag} for this 
	 * {@link Ability} that this system determines is 
	 * applicable for the event and context.
	 * 
	 * @param core the {@link rpg.project.lib.api.Hub} implementation
	 * @param eventID the ID of the event in which this ability is invoked
	 * @param context the event context for the invoking event
	 * @return a list of Ability configurations applicable, per this system.
	 */
	List<CompoundTag> getAbilitiesForContext(Hub core, ResourceLocation eventID, EventContext context);

	/**Abilities have some control over when they activate.  Because of this, the {@link AbilitySystem}
	 * does not know whether an ability it provided via {@link #getAbilitiesForContext(Hub, ResourceLocation, EventContext)}
	 * activated or not.  This method is called in the core library's ability execution logic to provide
	 * the AbilitySystem with this detail.
	 *
	 * @param ability The ability instance that activated
	 * @param data The configuration and context data supplied to the ability
	 * @param player The player associated with the ability activation
	 * @param context The context for the encapsulating event
	 * @param eventID The event this ability activated during
	 */
	void abilityActivationCallback(Ability ability, CompoundTag data, Player player, EventContext context, ResourceLocation eventID);

	/**@return a provider which supplies the side panel display for information this ability
	 * system wants to present to the player.
	 */
	SidePanelContentProvider getSidePanelProvider();
}
