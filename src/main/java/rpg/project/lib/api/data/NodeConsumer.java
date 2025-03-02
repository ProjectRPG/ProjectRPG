package rpg.project.lib.api.data;

import net.minecraft.resources.ResourceLocation;

import java.util.Map;

@FunctionalInterface
public interface NodeConsumer {
    void consume(String param, ResourceLocation id, ObjectType type, Map<String, String> value);
}
