package rpg.project.lib.internal.registry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.common.base.Preconditions;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.progression.ProgressionAddon;

public class ProgressionAddonRegistry {
	private static final Set<ProgressionAddon> registeredAddons = new HashSet<>();
	
	public static void registerAddon(ResourceLocation configID, SubSystemConfigType configType, ProgressionAddon addon) {
		Preconditions.checkNotNull(configID);
		Preconditions.checkNotNull(configType);
		Preconditions.checkNotNull(addon);
		SubSystemCodecRegistry.registerSubSystem(configID, configType);
		registeredAddons.add(addon);
	}
	
	public static List<ProgressionAddon> getAddons() {
		return registeredAddons.stream().toList();
	}
}
