package rpg.project.lib.api.progression;

public interface ProgressionSystem<T> {
	T getProgress(String container);
	void setProgress(String container, T value);
}
