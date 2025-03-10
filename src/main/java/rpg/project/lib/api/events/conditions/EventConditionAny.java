package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.events.EventContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

    public static class EventConditionAnyType implements EventConditionType<EventConditionAny> {

        @Override
        public MapCodec<EventConditionAny> codec() {
            return CODEC;
        }

        @Override
        public EventConditionAny fromScripting(Map<String, String> value) {
            return new EventConditionAny(EventCondition.fromScripting("or_if", value));
        }
    }
}
