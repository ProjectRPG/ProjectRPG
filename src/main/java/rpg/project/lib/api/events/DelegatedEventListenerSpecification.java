package rpg.project.lib.api.events;

import net.neoforged.bus.api.Event;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public record DelegatedEventListenerSpecification<T extends Event>(
        String registryPath,
        List<EventListenerSpecification<T>> getListenerSpecifications) implements EventProvider<T>{
    public DelegatedEventListenerSpecification(EventListenerSpecification<T> baseSpec, List<Function<T, EventContext>> variations) {
        this(baseSpec.registryPath(), Stream.concat(Stream.of(baseSpec), variations.stream().map(context -> new EventListenerSpecification<T>(
                baseSpec.registryID(),
                baseSpec.priority(),
                baseSpec.validEventClass(),
                baseSpec.validEventContext(),
                context,
                baseSpec.cancellationCallback(),
                baseSpec.contextCallback()
        ))).toList());
    }
}
