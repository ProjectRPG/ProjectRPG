package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;

public record EventConditionType(MapCodec<? extends EventCondition> codec) {}
