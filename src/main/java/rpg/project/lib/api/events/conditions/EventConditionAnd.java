package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.List;

public record EventConditionAnd(List<EventCondition> conditions) implements EventCondition{
    public static final MapCodec<EventConditionAnd> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventConditionAnd.TYPED_CODEC.listOf().fieldOf("conditions").forGetter(eca -> ((EventConditionAnd)eca).conditions())
    ).apply(instance, EventConditionAnd::new));

    @Override
    public EventConditionType getType() {
        return EventRegistry.ALL_OF.get();
    }

    @Override
    public boolean test(EventContext context) {
        return conditions.stream().allMatch(condition -> condition.test(context));
    }
}
