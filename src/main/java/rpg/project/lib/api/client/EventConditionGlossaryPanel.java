package rpg.project.lib.api.client;

import rpg.project.lib.api.events.conditions.EventCondition;

@FunctionalInterface
public interface EventConditionGlossaryPanel {
    ReactiveWidget make(EventCondition condition);
}
