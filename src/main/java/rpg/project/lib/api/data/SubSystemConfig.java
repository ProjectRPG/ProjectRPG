package rpg.project.lib.api.data;

import com.mojang.serialization.Codec;

public interface SubSystemConfig extends MergeableData {
	/**
	 * 
	 * @return
	 */
	public SubSystemConfigType getType();
	
	/**
	 * 
	 * @return
	 */
	public Codec<SubSystemConfig> getCodec();
}
