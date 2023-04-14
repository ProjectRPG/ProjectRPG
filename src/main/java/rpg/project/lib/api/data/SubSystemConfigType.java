package rpg.project.lib.api.data;

import com.mojang.serialization.Codec;

public interface SubSystemConfigType {
	Codec<SubSystemConfig> getCodec();
}
