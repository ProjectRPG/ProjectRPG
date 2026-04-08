package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.client.EventConditionGlossaryPanel;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.events.conditions.EventConditionEntityMatches;
import rpg.project.lib.api.events.conditions.EventConditionNot;
import rpg.project.lib.internal.registry.ClientPanelRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class EntityMatchConditionPanel extends ReactiveWidget {
    public EntityMatchConditionPanel(EventConditionEntityMatches condition) {
        super(0,0,0,0);
        RegistryAccess reg = Minecraft.getInstance().player.registryAccess();
        addString(LangProvider.CONDITION_ENTITY_MATCH.asComponent(fromID(condition.param().name()), condition.value().toString()), PositionConstraints.offset(1,1), textConstraint);
    }

    private Component fromID(Identifier id) {
        return Component.translatable("prpg.entity_param." + id.getNamespace() + "." + id.getPath());
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        GuiGraphicsExtractor.outline(this.getX(), this.getY(), this.getWidth(), this.getHeight(), 0xFF00FF00);
        super.extractWidgetRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}
}
