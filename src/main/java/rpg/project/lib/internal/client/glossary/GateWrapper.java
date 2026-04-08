package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.internal.config.readers.MainSystemConfig;

import java.util.List;
import java.util.Map;

public class GateWrapper extends ReactiveWidget {


    protected GateWrapper(MainSystemConfig config) {
        super(0,0,0,0);

        //TODO for type, iterate over the subsystem panel getters
        for (Map.Entry<GateUtils.Type, List<SubSystemConfig>> entry : config.gates().entrySet()) {
            this.addString(entry.getKey().translation, PositionType.STATIC.constraint, textConstraint);
            for (SubSystemConfig gateConfig : entry.getValue()) {

            }
        }
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
