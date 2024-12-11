package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;

public record EventConditionEntityMatches(LootContextParam<?> param, ResourceLocation value) implements EventCondition {
    public static final MapCodec<EventConditionEntityMatches> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("entity")
                    .xmap(rl -> new LootContextParam(rl), param -> param.getName())
                    .forGetter(EventConditionEntityMatches::param),
            ResourceLocation.CODEC.fieldOf("value").forGetter(EventConditionEntityMatches::value)
    ).apply(instance, EventConditionEntityMatches::new));

    @Override
    public EventConditionType getType() {
        return EventRegistry.ENTITY_MATCHES.get();
    }

    @Override
    public boolean test(EventContext context) {
        if (context.getParam(param()) instanceof Entity entity)
            return context.getLevel().registryAccess().registryOrThrow(Registries.ENTITY_TYPE)
                    .getKey(entity.getType()).equals(value());
        return false;
    }
}
