package rpg.project.lib.api.data;

import com.mojang.serialization.Codec;

/**A type identifier used to reference and reverse-get
 * a configuration object.
 */
public interface SubSystemConfigType {
	/**@return the codec used to parse the associated config object
	 */
	Codec<SubSystemConfig> getCodec();
}
