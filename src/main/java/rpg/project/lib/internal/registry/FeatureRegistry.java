package rpg.project.lib.internal.registry;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.feature.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeatureRegistry {
    private final Map<ResourceLocation, Feature> features = new HashMap<>();

    public void register(Feature feature) {features.put(feature.featureID(), feature);}

    public List<Feature> getFeaturesForContext(Hub core, ResourceLocation eventID, EventContext context) {
        List<Feature> validFeatures = new ArrayList<>();
        for (SubSystemConfig config : core.getFeatureData(context.getSubjectType(), context.getSubjectID(), eventID)) {
            ResourceLocation featureID = SubSystemCodecRegistry.lookup(config.getType());
            Feature feature = features.get(featureID);
            if (feature.isValidContext().test(eventID, context)) validFeatures.add(feature);
        }
        return validFeatures;
    }
}
