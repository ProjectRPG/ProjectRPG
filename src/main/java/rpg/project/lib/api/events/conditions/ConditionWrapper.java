package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import rpg.project.lib.api.events.EventContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/// A Helper object for inserting conditions into subsystems.  This wrapper handles
/// "AND" and "OR" conditions for you and provides you with a single call {@link #test(EventContext)}
/// to use inside your subsystem logic.  All EventConditions are compatible with this
/// wrapper and will be configurable inside your subsystem without additional support.
///
/// **IT IS STRONGLY RECOMMENDED** that all subsystems implement this wrapper as an
/// optional field and used conditions in their implementations.
public record ConditionWrapper(List<EventCondition> and, List<EventCondition> or) {
    public ConditionWrapper() {this(new ArrayList<>(), new ArrayList<>());}

    /**Codec for use in subsystem codecs when implementing this wrapper as an object.*/
    public static final Codec<ConditionWrapper> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.list(EventCondition.TYPED_CODEC).optionalFieldOf("all_of").forGetter(cw -> Optional.of(cw.and)),
            Codec.list(EventCondition.TYPED_CODEC).optionalFieldOf("any_of").forGetter(cw -> Optional.of(cw.or))
    ).apply(instance, (and, or) -> new ConditionWrapper(and.orElse(new ArrayList<>()), or.orElse(new ArrayList<>()))));

    /**Merges two wrappers together.
     *
     * @param other the wrapper being merged into this one.
     * @return a new wrapper instance with the combined values
     */
    public ConditionWrapper combine(ConditionWrapper other) {
        List<EventCondition> all_of = new ArrayList<>(this.and);
        List<EventCondition> any_of = new ArrayList<>(this.or);
        all_of.addAll(other.and);
        any_of.addAll(other.or);
        return new ConditionWrapper(all_of, any_of);
    }

    /**Merges two wrappers that are already wrapped in optionals.
     * Acts as a helper method for subsystems which don't require
     * conditions.
     *
     * @param other an Optionally-wrapped wrapper
     * @return a new optional containing the combined or empty result
     * of a merged wrapper.
     */
    public Optional<ConditionWrapper> combine(Optional<ConditionWrapper> other) {
        if (other.isEmpty()) return this.isEmpty() ? Optional.empty() : Optional.of(this);
        ConditionWrapper combined = this.combine(other.get());
        return this.isEmpty() ? Optional.empty() : Optional.of(combined);
    }

    public boolean isEmpty() {return and.isEmpty() && or.isEmpty();}

    /**Method to parse a condition wrapper from the scripting engine.
     *
     * @param values key-value pairs passed in from the scripting engine
     * @return a constructed wrapper
     */
    public static ConditionWrapper fromScripting(Map<String, String> values) {
        List<EventCondition> all_of = ((EventConditionAnd.EventConditionAndType)EventCondition.ALL_OF.get()).fromScripting(values).conditions();
        List<EventCondition> any_of = ((EventConditionAny.EventConditionAnyType)EventCondition.ANY_OF.get()).fromScripting(values).conditions();
        return new ConditionWrapper(all_of, any_of);
    }

    /**A comprehensive check of the conditions contained within this wrapper.
     * This will return true if, and only if, ALL of the and conditions are true
     * and at least one OR condition is true.  If either collection of conditions
     * is empty, that aspect is considered true.
     *
     * @param context the context passed in from the event system
     * @return whether the conditions are met, as configured.
     */
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
