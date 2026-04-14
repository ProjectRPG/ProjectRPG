package rpg.project.lib.builtins.vanilla.client.glossary;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.components.ConditionPanel;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.types.SystemOptions;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.builtins.vanilla.VanillaProgressionConfigType.VanillaProgressionConfig;

public class VanillaProgressionPanel extends ReactiveWidget {

    public VanillaProgressionPanel(SubSystemConfig config) {this((VanillaProgressionConfig) config);}
    public VanillaProgressionPanel(VanillaProgressionConfig config) {
        super(0,0,0,0);
        for (var entry : config.eventToXp().entrySet()) {
            addString(Component.literal(entry.getKey().toString()), PositionType.STATIC.constraint, textConstraint);
            for (VanillaProgressionConfig.ExpData xpRef : entry.getValue()) {
                addString(Component.literal("XP +").append(String.valueOf(xpRef.xp())), PositionConstraints.offset(10, 0), textConstraint);
                xpRef.conditions().ifPresent(wrapper ->
                    addChild((AbstractWidget) new ConditionPanel(wrapper), PositionConstraints.offset(10,0), SizeConstraints.builder().internalHeight().build())
                );
            }
        }
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !filter.matchesSelection(SystemOptions.PROGRESSION);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
