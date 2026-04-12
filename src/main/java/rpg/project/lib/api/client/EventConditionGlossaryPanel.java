package rpg.project.lib.api.client;

import rpg.project.lib.api.events.conditions.EventCondition;

/**A functional interface for obtaining a glossary panel widget from
 * an {@link EventCondition} instance.
 */
@FunctionalInterface
public interface EventConditionGlossaryPanel {
    /**Creates a glossary panel from an event condition or subclass thereof
     *
     * @param condition the condition, whose data is, used to generate the widget
     * @return a new widget representing the event condition's contents
     */
    ReactiveWidget make(EventCondition condition);
}
