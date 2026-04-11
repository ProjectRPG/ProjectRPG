package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.events.conditions.EventConditionNBT;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.nbt.Case;

import java.util.List;

public class NBTConditionPanel extends ReactiveWidget {
    public NBTConditionPanel(EventConditionNBT condition) {
        super(0,0,0,0);
        RegistryAccess reg = Minecraft.getInstance().player.registryAccess();
        addString(LangProvider.CONDITION_NBT.asComponent(), PositionConstraints.offset(1,1), textConstraint);
        for (Case caso : condition.logic()) {
            addString(LangProvider.GLOSSARY_NBT_PATH.asComponent(), PositionConstraints.offset(11, 0), textConstraint);
            for (String path : caso.paths()) {
                addString(Component.literal(path), PositionConstraints.offset(21, 0), textConstraint);
            }
            addString(LangProvider.GLOSSARY_NBT_CRITERIA.asComponent(), PositionConstraints.offset(11, 0), textConstraint);
            for (Case.Criteria criteria : caso.criteria()) {
                addString(criteria.operator().translation.asComponent(MsLoggy.listToString(criteria.comparators().orElse(List.of()))),
                        PositionConstraints.offset(21, 0), textConstraint);
            }
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
