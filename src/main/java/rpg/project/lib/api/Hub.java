package rpg.project.lib.api;

import java.util.List;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.gating.GateUtils.Type;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.internal.registry.AbilityRegistry;
import rpg.project.lib.internal.registry.FeatureRegistry;

/**Implementations of this provide access to shared
 * features of the library.  Project RPG provides an
 * implementation internally and expects you to access
 * all shared resources through this object when
 * supplied.
 */
public interface Hub {
	/**<p>Accesses the Hub's internal configuration data and returns 
	 * the configuration object associated with the in-game object
	 * specified and for the configuration type.</p>
	 * <p>Note: if no configuration for the supplied type is present
	 * in the user's configuration files, this will return an empty
	 * optional.</p>  
	 * 
	 * @param systemType the config object type being requested
	 * @param type the in-game object type.  This helps distinguish 
	 * between objects with the same ID such as blocks and items
	 * @param objectID the id of the object
	 * @return an optional containing the configuration, if present
	 */
	Optional<SubSystemConfig> getProgressionData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID);
	/**<p>Accesses the Hub's internal configuration data and returns 
	 * the configuration object associated with the in-game object
	 * specified and for the configuration type.</p>
	 * <p>Note: if no configuration for the supplied type is present
	 * in the user's configuration files, this will return an empty
	 * optional.</p>  
	 * 
	 * @param systemType the config object type being requested
	 * @param type the in-game object type.  This helps distinguish 
	 * between objects with the same ID such as blocks and items
	 * @param objectID the id of the object
	 * @return an optional containing the configuration, if present
	 */
	Optional<SubSystemConfig> getGateData(SubSystemConfigType systemType, ObjectType type, Type gateType, ResourceLocation objectID);
	/**<p>Accesses the Hub's internal configuration data and returns 
	 * the configuration object associated with the in-game object
	 * specified and for the configuration type.</p>
	 * <p>Note: if no configuration for the supplied type is present
	 * in the user's configuration files, this will return an empty
	 * optional.</p>  
	 * 
	 * @param systemType the config object type being requested
	 * @param type the in-game object type.  This helps distinguish 
	 * between objects with the same ID such as blocks and items
	 * @param objectID the id of the object
	 * @return an optional containing the configuration, if present
	 */
	Optional<SubSystemConfig> getAbilityData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID);
	/**<p>Accesses the Hub's internal configuration data and returns 
	 * the configuration object associated with the in-game object
	 * specified and for the configuration type.</p>
	 * <p>Note: if no configuration for the supplied type is present
	 * in the user's configuration files, this will return an empty
	 * list.</p>
	 *
	 * @param type the in-game object type.  This helps distinguish 
	 * between objects with the same ID such as blocks and items
	 * @param objectID the id of the subject object
	 * @param eventID the event this feature data is being invoked for
	 * @return an optional containing the configuration, if present
	 */
	List<SubSystemConfig> getFeatureData(ObjectType type, ResourceLocation objectID, ResourceLocation eventID);
	/**@return the active {@link PartySystem} implementation for this instance
	 */
	PartySystem getParty();
	/**@return the active {@link ProgressionSystem} implementation for this instance
	 */
	ProgressionSystem<?> getProgression();
	/**@return all active {@link ProgressionAddon}s for this instance
	 */
	List<ProgressionAddon> getProgressionAddons();
	/**@return the active {@link AbilitySystem} implementation for this instance
	 */
	AbilitySystem getAbility();
	/**@return the registry containing {@link Ability} types from all addons
	 */
	AbilityRegistry getAbilities();
	/**@return the registry containing all the {@link rpg.project.lib.api.feature.Feature}s from all addons.*/
	FeatureRegistry getFeatures();
}
