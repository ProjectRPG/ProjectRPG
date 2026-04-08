package rpg.project.lib.api.client;

import rpg.project.lib.api.data.SubSystemConfig;

@FunctionalInterface
public interface SubSystemGlossaryPanel {
    ReactiveWidget make(SubSystemConfig config);
}
