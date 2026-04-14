package rpg.project.lib.builtins.vanilla.client.glossary;

import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import rpg.project.lib.api.client.ClientUtils;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.components.ConditionPanel;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.types.SystemOptions;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.builtins.vanilla.VanillaAbilityConfigType.VanillaAbilityConfig;

public class VanillaAbilityPanel extends ReactiveWidget {

    public VanillaAbilityPanel(SubSystemConfig config) {this((VanillaAbilityConfig) config);}
    public VanillaAbilityPanel(VanillaAbilityConfig config) {
        super(0,0,0,0);
        for (var entry : config.data()) {
            addString(ClientUtils.getAbilityName(entry.ability()), PositionType.STATIC.constraint, textConstraint);
            entry.conditions().ifPresent(wrapper ->
                addChild((AbstractWidget) new ConditionPanel(wrapper), PositionConstraints.offset(10,0), SizeConstraints.builder().internalHeight().build())
            );
        }
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !filter.matchesSelection(SystemOptions.ABILITY);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
