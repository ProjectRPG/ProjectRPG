package rpg.project.lib.api.progression;

import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;

/**<p>Progression addons provide third-party input into
 * the progression system.  Addons do not care about
 * the underlying implementation.  The purpose of a
 * progression addon is to modify the input before it
 * is committed.</p>
 * <p>Examples of progression addons include providing 
 * contextual bonuses to experience or transfering progress
 * to another feature such as an item that clones progress
 * while held.</p>
 * <p>While addons have the ability to reduce progress to
 * zero and have the effect of working like gates, an explicit
 * distinction should be made in their functionality to 
 * prevent this duplication. An addon must assume that the 
 * progression was permitted.</p>
 *
 */
public interface ProgressionAddon {
	/**<p>This is provided the original (or delegated) value that
	 * the progression system expects to commit.  Internally,
	 * the progression addon determines what to do with that 
	 * information.  a "modified" data value is returned.</p>
	 * <p>Note: an addon does not need to modify the value.  
	 * Additionally, since {@link ProgressionDataType#modify(rpg.project.lib.api.progression.ProgressionDataType.Modification, ProgressionDataType) ProgressionDataType#modify} 
	 * takes an enum to clarify the modification behavior,
	 * the configuration for this addon should factor into
	 * the configuration</p>
	 * 
	 * @param core gives access to shared resources
	 * @param context information about the circumstances of this progression
	 * @param dataIn the data value being modified
	 * @return a new modified value
	 */
    ProgressionDataType modifyProgression(Hub core, EventContext context, ProgressionDataType dataIn);
}
