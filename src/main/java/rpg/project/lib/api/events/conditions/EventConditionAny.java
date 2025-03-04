package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.List;

public record EventConditionAny(List<EventCondition> conditions) implements EventCondition{
    public static final MapCodec<EventConditionAny> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionAny.TYPED_CODEC.listOf().fieldOf("conditions").forGetter(eca -> ((EventConditionAny)eca).conditions())
    ).apply(instance, EventConditionAny::new));

    @Override
    public EventConditionType getType() {
        return EventCondition.ANY_OF.get();
    }

    @Override
    public boolean test(EventContext context) {
        return conditions.stream().anyMatch(condition -> condition.test(context));
    }
}
