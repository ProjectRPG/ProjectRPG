package rpg.project.lib.builtins.vanilla.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.abilities.AbilityUtils;
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
            Identifier abilityID = Identifier.parse(entry.ability().getStringOr(AbilityUtils.TYPE, "prpg:missing"));
            addString(ClientUtils.getAbilityName(entry.ability()), PositionType.STATIC.constraint, textConstraint);
            addChild((AbstractWidget) AbilityUtils.getAbilityPanel(abilityID).make(Minecraft.getInstance().player, entry.ability()),
                    PositionConstraints.offset(10, 0),
                    SizeConstraints.builder().internalHeight().build());
            entry.conditions().ifPresent(wrapper ->
                addChild((AbstractWidget) new ConditionPanel(wrapper),
                        PositionConstraints.offset(10,0),
                        SizeConstraints.builder().internalHeight().build())
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
