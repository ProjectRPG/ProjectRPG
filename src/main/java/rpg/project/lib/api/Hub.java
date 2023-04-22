package rpg.project.lib.api;

import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.internal.registry.GateRegistry;

/**Implementations of this provide access to shared
 * features of the library.  Project RPG provides an
 * implementation internally and expects you to access
 * all shared resources through this object when
 * supplied.
 */
public interface Hub {
	Optional<SubSystemConfig> getProgressionData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID);
	Optional<SubSystemConfig> getGateData(SubSystemConfigType systemType, ObjectType type, GateRegistry.Type gateType, ResourceLocation objectID);
	Optional<SubSystemConfig> getAbilityData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID);
	Optional<SubSystemConfig> getFeatureData(SubSystemConfigType systemType, ObjectType type, ResourceLocation objectID);
	/**@return the active {@link PartySystem} implementation for this instance
	 */
	PartySystem getParty();
	/**@return the active {@link ProgressionSystem} implementation for this instance
	 */
	ProgressionSystem<?> getProgression();
}
