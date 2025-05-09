package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record ConditionWrapper(List<EventCondition> and, List<EventCondition> or) {
    public ConditionWrapper() {this(new ArrayList<>(), new ArrayList<>());}

    public static final Codec<ConditionWrapper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(EventCondition.TYPED_CODEC).optionalFieldOf("all_of").forGetter(cw -> Optional.of(cw.and)),
            Codec.list(EventCondition.TYPED_CODEC).optionalFieldOf("any_of").forGetter(cw -> Optional.of(cw.or))
    ).apply(instance, (and, or) -> new ConditionWrapper(and.orElse(new ArrayList<>()), or.orElse(new ArrayList<>()))));

    public ConditionWrapper combine(ConditionWrapper other) {
        List<EventCondition> all_of = new ArrayList<>(this.and);
        List<EventCondition> any_of = new ArrayList<>(this.or);
        all_of.addAll(other.and);
        any_of.addAll(other.or);
        return new ConditionWrapper(all_of, any_of);
    }

    public Optional<ConditionWrapper> combine(Optional<ConditionWrapper> other) {
        if (other.isEmpty()) return this.isEmpty() ? Optional.empty() : Optional.of(this);
        ConditionWrapper combined = this.combine(other.get());
        return this.isEmpty() ? Optional.empty() : Optional.of(combined);
    }

    public boolean isEmpty() {return and.isEmpty() && or.isEmpty();}

    public static ConditionWrapper fromScripting(Map<String, String> values) {
        List<EventCondition> all_of = ((EventConditionAnd.EventConditionAndType)EventCondition.ALL_OF.get()).fromScripting(values).conditions();
        List<EventCondition> any_of = ((EventConditionAny.EventConditionAnyType)EventCondition.ANY_OF.get()).fromScripting(values).conditions();
        return new ConditionWrapper(all_of, any_of);
    }

    public boolean test(EventContext context) {
        return and.stream().allMatch(condition -> condition.test(context))
                && (or.isEmpty() || or.stream().anyMatch(condition -> condition.test(context)));
    }

    @Override
    public String toString() {
        return "ConditionWrapper{" +
                "and=" + and +
                ", or=" + or +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConditionWrapper that = (ConditionWrapper) o;
        return Objects.equals(or, that.or) && Objects.equals(and, that.and);
    }

    @Override
    public int hashCode() {
        return Objects.hash(and, or);
    }
}
