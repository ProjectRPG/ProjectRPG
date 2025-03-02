package rpg.project.lib.api.progression;

import java.util.function.Supplier;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

public class ProgressionUtils {
	/**Progression addons are registered using a deferred register and this ResourceKey*/
	public static final ResourceKey<Registry<ProgressionAddon>> PROGRESSION_ADDON = ResourceKey.createRegistryKey(Reference.resource("progression_addon"));

	/**<p>Set's the ecosystem's progression system.</p>
	 * <p>There can only be one progression system in an instance.
	 * If this method is invoked more than once, the latter
	 * invocation will set the ultimate system.</p>
	 *
	 * @param system an {@link ProgressionSystem} implementation
	 */
	public static void registerProgressionSystem(Supplier<ProgressionSystem<?>> system) {
		CommonSetup.progressionSupplier = system;
	}
}
