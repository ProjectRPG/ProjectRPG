package rpg.project.lib.api.feature;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.events.EventContext;

@FunctionalInterface
public interface FeatureFunction {
    void execute(Hub hub, ResourceLocation eventID, EventContext context, float gating);
}
