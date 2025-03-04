package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;

import java.util.List;

public record ConditionWrapper(List<EventCondition> conditions) {
    public static final Codec<ConditionWrapper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(EventCondition.TYPED_CODEC).fieldOf("conditions").forGetter(ConditionWrapper::conditions)
    ).apply(instance, ConditionWrapper::new));

    public boolean test(EventContext context) {
        return conditions.stream().allMatch(condition -> condition.test(context));
    }
}
