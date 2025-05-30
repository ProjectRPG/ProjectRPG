package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.Map;

public record EventConditionNot(EventCondition condition) implements EventCondition {
    public static final MapCodec<EventConditionNot> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
          EventCondition.TYPED_CODEC.fieldOf("condition").forGetter(eca -> ((EventConditionNot)eca).condition())
    ).apply(instance, EventConditionNot::new));

    @Override
    public EventConditionType getType() {
        return EventCondition.NOT.get();
    }

    @Override
    public boolean test(EventContext context) {
        return !condition.test(context);
    }

    public static class EventConditionNotType implements EventConditionType<EventConditionNot> {

        @Override
        public MapCodec<EventConditionNot> codec() {
            return CODEC;
        }

        @Override
        public EventConditionNot fromScripting(Map<String, String> value) {
            return null;
        }
    }
}
