package rpg.project.lib.api.progression;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;

public interface ProgressionSystem<T> {
	T getProgress(UUID playerID, String container);
	void setProgress(UUID playerID, String container, T value);
	List<String> getContextuallyAffectedContainers(Hub core, ResourceLocation eventID, EventContext context);
	void applyContextuallyApplicableProgress(Hub core, ResourceLocation eventID, EventContext context, Predicate<String> filter);
}
