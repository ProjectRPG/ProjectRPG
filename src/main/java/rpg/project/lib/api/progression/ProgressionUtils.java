package rpg.project.lib.api.progression;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;

public class ProgressionUtils {
	/**<p>Set's the ecosystem's XP system.</p>  
	 * <p>There can only be one XP system in an instance.
	 * If this is invoked more than once, the latter
	 * invocation will set the ultimate xp system.</p>
	 * 
	 * @param id an identifier used in object configs as the
	 * "type" for the configuration. This "type" specifies that
	 * the internal config reader should use your config when
	 * parsing the user-defined configurations
	 * @param config the config type used to obtain a codec to
	 * parse user-defined configurations.
	 * @param system an {@link ProgressionSystem} implementation
	 */
	public static void registerXpSystem(ResourceLocation id, SubSystemConfigType config, Supplier<ProgressionSystem<?>> system) {
		CommonSetup.progressionSupplier = () -> {
			SubSystemCodecRegistry.registerSubSystem(id, config);
			return system.get();
		};
	}
}
