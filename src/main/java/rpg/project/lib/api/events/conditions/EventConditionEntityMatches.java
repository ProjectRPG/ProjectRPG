package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.Entity;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

import java.util.Map;

public record EventConditionEntityMatches(ContextKey<?> param, ResourceLocation value) implements EventCondition {
    public static final MapCodec<EventConditionEntityMatches> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity")
                    .xmap(rl -> new ContextKey(rl), param -> param.name())
                    .forGetter(EventConditionEntityMatches::param),
            ResourceLocation.CODEC.fieldOf("value").forGetter(EventConditionEntityMatches::value)
    ).apply(instance, EventConditionEntityMatches::new));

    @Override
    public EventConditionType getType() {
        return EventCondition.ENTITY_MATCHES.get();
    }

    @Override
    public boolean test(EventContext context) {
        if (context.getParam(param()) instanceof Entity entity)
            return context.getLevel().registryAccess().lookupOrThrow(Registries.ENTITY_TYPE)
                    .getKey(entity.getType()).equals(value());
        return false;
    }

    public static class EventConditionEntityMatchesType implements EventConditionType<EventConditionEntityMatches> {

        @Override
        public MapCodec<EventConditionEntityMatches> codec() {
            return CODEC;
        }

        @Override
        public EventConditionEntityMatches fromScripting(Map<String, String> value) {
            return null;
        }
    }
}
