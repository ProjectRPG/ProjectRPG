package rpg.project.lib.api.progression;

import java.util.UUID;

public interface ProgressionSystem<T> {
	T getProgress(UUID playerID, String container);
	void setProgress(UUID playerID, String container, T value);
}
