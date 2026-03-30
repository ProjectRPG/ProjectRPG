package rpg.project.lib.api.data;

import net.minecraft.resources.Identifier;

import java.util.Map;

@FunctionalInterface
public interface NodeConsumer {
    void consume(String qualifier, String param, Identifier id, ObjectType type, Map<String, String> value);
}
