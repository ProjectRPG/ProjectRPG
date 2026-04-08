package rpg.project.lib.api.events.conditions;

import com.mojang.serialization.MapCodec;
import rpg.project.lib.api.events.EventContext;

import java.util.Map;

//TODO implement List<Logic> and CompoundTag as params
public record EventConditionNBT() implements EventCondition{
    public static final MapCodec<EventConditionNBT> CODEC = null;

    @Override
    public EventConditionType getType() {
        return null;
    }

    @Override
    public boolean test(EventContext eventContext) {
        return false;
    }

    public static class EventConditionNBTType implements EventConditionType<EventConditionNBT> {

        @Override
        public MapCodec<EventConditionNBT> codec() {return CODEC;}

        @Override
        public EventConditionNBT fromScripting(Map<String, String> value) {
            return null;
        }
    }
}
