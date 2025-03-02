package rpg.project.lib.api.data;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

@FunctionalInterface
public interface TargetSelector {
    record Selection(ObjectType type, List<ResourceLocation> IDs) {}

    Selection read(String node, RegistryAccess access);
}
