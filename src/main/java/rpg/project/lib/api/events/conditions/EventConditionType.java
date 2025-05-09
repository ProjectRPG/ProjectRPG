package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;

import java.util.Map;

public interface EventConditionType<T extends EventCondition> {
    MapCodec<T> codec();
    T fromScripting(Map<String, String> value);
}
