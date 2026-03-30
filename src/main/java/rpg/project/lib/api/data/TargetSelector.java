package rpg.project.lib.api.data;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;

import java.util.List;

@FunctionalInterface
public interface TargetSelector {
    record Selection(ObjectType type, List<Identifier> IDs) {}

    Selection read(String node, RegistryAccess access);
}
