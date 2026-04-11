package rpg.project.lib.internal.client.glossary;

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
import rpg.project.lib.api.events.conditions.EventConditionNot;
import rpg.project.lib.internal.registry.ClientPanelRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class NotConditionPanel extends ReactiveWidget {
    public NotConditionPanel(EventConditionNot condition) {
        super(0,0,0,0);
        RegistryAccess reg = Minecraft.getInstance().player.registryAccess();
        addString(LangProvider.CONDITION_NOT.asComponent(), PositionConstraints.offset(1,1), textConstraint);
        EventConditionGlossaryPanel panelFactory = ClientPanelRegistry.getConditionPanel(reg, condition.condition());
        addChild((AbstractWidget) panelFactory.make(condition.condition()), PositionConstraints.offset(11, 0), SizeConstraints.builder().internalHeight().build());
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        GuiGraphicsExtractor.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFFFF0000);
        super.extractWidgetRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
