package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public record EventConditionAnd(List<EventCondition> conditions) implements EventCondition{
    public static final MapCodec<EventConditionAnd> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EventCondition.TYPED_CODEC.listOf().fieldOf("conditions").forGetter(eca -> ((EventConditionAnd)eca).conditions())
    ).apply(instance, EventConditionAnd::new));

    @Override
    public EventConditionType getType() {
        return EventCondition.ALL_OF.get();
    }

    @Override
    public boolean test(EventContext context) {
        return conditions.stream().allMatch(condition -> condition.test(context));
    }

    public static class EventConditionAndType implements EventConditionType<EventConditionAnd> {

        @Override
        public MapCodec<EventConditionAnd> codec() {
            return CODEC;
        }

        @Override
        public EventConditionAnd fromScripting(Map<String, String> value) {
            return new EventConditionAnd(EventCondition.fromScripting("and_if", value));
        }
    }
}
