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
	
	/**Supplies a value-less instance of this object.  This is 
	 * used by the internal config writer to build config files
	 * for users.  This should prepopulate map keys when
	 * applicable so that users have an exaple of what options
	 * are available to them.
	 * 
	 * @return a default instance of this object.
	 */
    SubSystemConfig getDefault();
}
