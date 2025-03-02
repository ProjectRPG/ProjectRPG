package rpg.project.lib.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.APIUtils;

import java.util.EnumSet;

/**A type identifier used to reference and reverse-get
 * a configuration object.
 */
public interface SubSystemConfigType {
	ResourceLocation getId();
	/**@return the codec used to parse the associated config object
	 */
	MapCodec<SubSystemConfig> getCodec();

	/**Provides a default implementation of this subsystem configuration for use in data
	 * generation.  This implementations should have the following: <ul>
	 *     <li>non-operable details, so that generating a configuration does not alter game
	 *     behavior without user input</li>
	 *     <li>include all optionals so users have a complete view of what is available
	 *     (exceptions for mutual exclusivity are appropriate)</li>
	 *     <li>If the configuration accepts entries from registries, such as progression using
	 *     events, registry access is provided for that.  However, if the registry data is
	 *     excessive, a minimalistic example to demonstrate the registry-based functionality
	 *     is more appropriate.</li>
	 * </ul>
	 *
	 * @param access registry access for generating runtime values using current registry entries
	 * @return a default implementation conforming to the above parameters.
	 */
	SubSystemConfig getDefault(RegistryAccess access);

	EnumSet<APIUtils.SystemType> applicableSystemTypes();
}
