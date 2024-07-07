package rpg.project.lib.internal.registry;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.feature.Feature;

import java.util.List;

public class FeatureRegistry {
    public List<Feature> getFeaturesForContext(Hub core, ResourceLocation eventID, EventContext context) {
        return List.of();
    }
}
