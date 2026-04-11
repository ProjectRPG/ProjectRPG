package rpg.project.lib.api.events.conditions;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.nbt.Case;
import rpg.project.lib.internal.util.nbt.Operator;
import rpg.project.lib.internal.util.nbt.PathReader;
import rpg.project.lib.internal.util.nbt.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record EventConditionNBT(List<Case> logic) implements EventCondition {
    public static final MapCodec<EventConditionNBT> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Case.CODEC.listOf().fieldOf("logic").forGetter(EventConditionNBT::logic)
    ).apply(instance, EventConditionNBT::new));
    private static final Map<Pair<CompoundTag, List<Case>>, Boolean> cache = new HashMap<>();

    @Override
    public EventConditionType<? extends EventCondition> getType() {
        return EventCondition.NBT.get();
    }

    @Override
    public boolean test(EventContext eventContext) {
        CompoundTag nbt = eventContext.hasParam(EventContext.NBT) ? eventContext.getParam(EventContext.NBT) : new CompoundTag();
        if (cache.containsKey(Pair.of(nbt, logic))) return MsLoggy.DEBUG.logAndReturn(cache.get(Pair.of(nbt, logic)), MsLoggy.LOG_CODE.DATA, "NBT Cache Used");
        for (Case caseIterant : logic) {
            for (String path : caseIterant.paths()) {
                for (Case.Criteria critObj : caseIterant.criteria()) {
                    Operator operator = critObj.operator();
                    List<String> comparison = PathReader.getNBTValues(getActualPath(path), nbt);
                    for (String compare : comparison) {
                        if (!operator.equals(Operator.EXISTS)) {
                            for (String comparators : critObj.comparators().orElseGet(ArrayList::new)) {
                                String comparator = getActualConstant(comparators);
                                if (new Result(operator, comparator, compare).compares()) {
                                    cache.put(Pair.of(nbt, logic), true);
                                    return true;
                                }
                            }
                        } else {
                            if (new Result(operator, "", compare).compares()) {
                                cache.put(Pair.of(nbt, logic), true);
                                return true;
                            }
                        }
                    }
                }
            }
        }
        cache.put(Pair.of(nbt, logic), false);
        return false;
    }

    private String getActualPath(String key) {
        return key.contains("#") ? Config.GLOBALS.get().getOrDefault(key.replace("#", ""), key.replace("#", "")) : key;
    }
    private String getActualConstant(String key) {
        return key.contains("#") ? Config.CONSTANTS.get().getOrDefault(key.replace("#", ""), key.replace("#", "")) : key;
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
