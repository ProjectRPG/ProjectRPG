package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.util.Reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public interface EventCondition extends Predicate<EventContext> {
    public static final DeferredRegister<EventConditionType> CONDITIONS = DeferredRegister.create(APIUtils.EVENT_CONDITIONS, Reference.MODID);

    public static final Codec<EventCondition> TYPED_CODEC = Codec.lazyInitialized(() -> CONDITIONS.getRegistry().get().byNameCodec()
            .dispatch("type", EventCondition::getType, EventConditionType::codec));

    public static final DeferredHolder<EventConditionType, EventConditionType> ALL_OF = CONDITIONS.register("and", EventConditionAnd.EventConditionAndType::new);
    public static final DeferredHolder<EventConditionType, EventConditionType> ANY_OF = CONDITIONS.register("any", EventConditionAny.EventConditionAnyType::new);
    public static final DeferredHolder<EventConditionType, EventConditionType> NOT = CONDITIONS.register("not", EventConditionNot.EventConditionNotType::new);
    public static final DeferredHolder<EventConditionType, EventConditionType> ENTITY_MATCHES = CONDITIONS.register("entity_matches", EventConditionEntityMatches.EventConditionEntityMatchesType::new);

    EventConditionType getType();

    public static List<EventCondition> fromScripting(String nodePrefix, Map<String, String> value) {
        List<String> conditionValues = value.keySet().stream().filter(str -> str.startsWith(nodePrefix)).toList();
        List<EventCondition> conditions = new ArrayList<>();
        for (String conditionValue : conditionValues) {
            ResourceLocation conditionKey = ResourceLocation.parse(value.get(conditionValue));
            Optional<EventConditionType> cType = CONDITIONS.getRegistry().get().getOptional(conditionKey);
            cType.ifPresent(type -> conditions.add(type.fromScripting(value)));
        }
        return conditions;
    }
}
