package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.function.Predicate;

public interface EventCondition extends Predicate<EventContext> {
    public static final Codec<EventCondition> TYPED_CODEC = EventRegistry.CONDITIONS.getRegistry().get().byNameCodec()
            .dispatch("condition", EventCondition::getType, EventConditionType::codec);
    EventConditionType getType();
}
