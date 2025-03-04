package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.util.Reference;

import java.util.function.Predicate;

public interface EventCondition extends Predicate<EventContext> {
    public static final DeferredRegister<EventConditionType> CONDITIONS = DeferredRegister.create(APIUtils.EVENT_CONDITIONS, Reference.MODID);

    public static final Codec<EventCondition> TYPED_CODEC = Codec.lazyInitialized(() -> CONDITIONS.getRegistry().get().byNameCodec()
            .dispatch("type", EventCondition::getType, EventConditionType::codec));

    public static final DeferredHolder<EventConditionType, EventConditionType> ALL_OF = condition("and", EventConditionAnd.CODEC);
    public static final DeferredHolder<EventConditionType, EventConditionType> ANY_OF = condition("any", EventConditionAny.CODEC);
    public static final DeferredHolder<EventConditionType, EventConditionType> NOT = condition("not", EventConditionNot.CODEC);
    public static final DeferredHolder<EventConditionType, EventConditionType> ENTITY_MATCHES = condition("entity_matches", EventConditionEntityMatches.CODEC);

    private static DeferredHolder<EventConditionType, EventConditionType> condition(String name, MapCodec<? extends EventCondition> codec) {
        return CONDITIONS.register(name, () -> new EventConditionType(codec));
    }



    EventConditionType getType();
}
