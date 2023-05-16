package rpg.project.lib.api.progression;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.ProgressionAddonRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;

public class ProgressionUtils {
	/**<p>Set's the ecosystem's XP system.</p>  
	 * <p>There can only be one XP system in an instance.
	 * If this is invoked more than once, the latter
	 * invocation will set the ultimate xp system.</p>
	 * 
	 * @param systemConfigId an identifier used in object configs as the
	 * "type" for the configuration. This "type" specifies that
	 * the internal config reader should use your config when
	 * parsing the user-defined configurations
	 * @param systemConfig the config type used to obtain a codec to
	 * parse user-defined configurations.
	 * @param system an {@link ProgressionSystem} implementation
	 */
	public static void registerXpSystem(
			ResourceLocation systemConfigId, 
			SubSystemConfigType systemConfig, 
			ResourceLocation systemDataTypeId,
			SubSystemConfigType systemDataType,
			Supplier<ProgressionSystem<?>> system) {
		CommonSetup.progressionSupplier = () -> {
			SubSystemCodecRegistry.registerSubSystem(systemConfigId, systemConfig);
			SubSystemCodecRegistry.registerSubSystem(systemDataTypeId, systemDataType);
			return system.get();
		};
	}
	
	/**Registers an addon to customize behavior of the 
	 * xp system.  Multiple addons can be registered.
	 * 
	 * @param configID the configuration key for this addon
	 * @param configType the cofiguration implementation reference
	 * @param addon the addon instance being registered
	 */
	public static void registerAddon(
			ResourceLocation configID,
			SubSystemConfigType configType,
			ProgressionAddon addon) {
		ProgressionAddonRegistry.registerAddon(configID, configType, addon);
	}
}
