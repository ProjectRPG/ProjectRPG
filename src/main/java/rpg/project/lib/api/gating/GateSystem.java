package rpg.project.lib.api.gating;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.events.EventListenerSpecification.CancellationType;

/**<p>A gate system controls whether specific content is
 * accessible to the player.</p>
 * <p>There are four types of gate system which are registered
 * in {@link GateUtils}.  They are:<table border=1px>
 * <tr><th>Type</th><th>Description</th></tr>
 * <tr><td>EVENT</td><td> allows for event gating.  This type of gating
 * is typically used to prevent interactions and world modification</td></tr>
 * <tr><td>PROGRESS</td><td> allows for preventing progression.  This type
 * of gating allows for features like classes which prevent progress
 * in certain containers for certain classes.</td></tr>
 * <tr><td>FEATURE</td><td> allows for gating custom features added by addons.  
 * This is intended to allow linking progress to features.</td></tr>
 * <tr><td>ABILITY</td><td> allows for gating abilities.  This is independent of
 * activation conditions defined by the ability system.</td></tr>
 * </table></p>
 *
 * @param <T> gate object type
 */
public interface GateSystem {
	/**<p>Consumes the provided contextual variables to evaluate if
	 * that context is permissible for the player provided.</p>
	 * 
	 * @param context event variables provided by the event.  This
	 * includes the player associated with this event proc.
	 * @param core reference to the internal system logic
	 * @param event event ID which triggered this check
	 * @param reference an instance of the applicable object being gated
	 * @return false if the action should be prevented.
	 */
	boolean isActionPermitted(EventContext context, Hub core, ResourceLocation event, String reference);
	
	/**<p>Used specifically by the EVENT gating type, this method
	 * tells the event listener spec which type of cancellation
	 * should apply if the event is not permitted.</p>
	 * <p>If there are no Item or Block sub-cancellation types for
	 * an applicable event, there is no need to override this method</p>
	 * 
	 * @param context event variables provided by the event.  This
	 * includes the player associated with this event proc.
	 * @param core reference to the internal system logic
	 * @param event event ID which triggered this check
	 * @param reference an instance of the applicable object being gated
	 * @return the type of cancellation behavior
	 */
	default CancellationType getCancellationResult(EventContext context, Hub core, ResourceLocation event, String reference) {
		return isActionPermitted(context, core, event, reference) 
				? CancellationType.NONE
				: CancellationType.EVENT;
	}
}
