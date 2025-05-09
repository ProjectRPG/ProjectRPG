package rpg.project.lib.api.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

/**Implemented to define an object which can be parsed from
 * a configuration file.
 */
public interface SubSystemConfig extends MergeableData {
	/**Obtains the associated type for this config.  this serves
	 * as an internal reverse lookup of the type from this instance.
	 * 
	 * @return the linked {@link SubSystemConfigType}
	 */
    SubSystemConfigType getType();
	
	/**@return The codec used to encode/decode this object
	 */
    MapCodec<SubSystemConfig> getCodec();
}
