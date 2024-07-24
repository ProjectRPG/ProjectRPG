package rpg.project.lib.api.events;

import net.neoforged.bus.api.Event;

import java.util.List;

public interface EventProvider<T extends Event> {
    String registryPath();
    List<EventListenerSpecification<T>> getListenerSpecifications();
}
