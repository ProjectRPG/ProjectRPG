package rpg.project.lib.api.client.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import rpg.project.lib.api.client.EventConditionGlossaryPanel;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.events.conditions.ConditionWrapper;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.internal.registry.ClientPanelRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class ConditionPanel extends ReactiveWidget {

    public ConditionPanel(ConditionWrapper wrapper) {
        super(0,0,0,0);
        RegistryAccess reg = Minecraft.getInstance().player.registryAccess();

        if (!wrapper.and().isEmpty())
            addString(LangProvider.CONDITION_ALL_OF.asComponent(), PositionConstraints.offset(1,1), textConstraint);
        for (EventCondition condition : wrapper.and()) {
            EventConditionGlossaryPanel panelFactory = ClientPanelRegistry.getConditionPanel(reg, condition);
            if (panelFactory != null)
                addChild((AbstractWidget) panelFactory.make(condition),
                        PositionConstraints.offset(11, 0),
                        SizeConstraints.builder().internalHeight().build());
        }

        if (!wrapper.or().isEmpty())
            addString(LangProvider.CONDITION_ANY_OF.asComponent(), PositionConstraints.offset(1,0), textConstraint);
        for (EventCondition condition : wrapper.or()) {
            EventConditionGlossaryPanel panelFactory = ClientPanelRegistry.getConditionPanel(reg, condition);
            if (panelFactory != null)
                addChild((AbstractWidget) panelFactory.make(condition),
                        PositionConstraints.offset(11, 0),
                        SizeConstraints.builder().internalHeight().build());
        }
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        GuiGraphicsExtractor.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFF0000);
        super.extractWidgetRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
