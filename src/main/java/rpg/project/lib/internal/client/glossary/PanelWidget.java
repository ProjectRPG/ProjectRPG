package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.gui.components.AbstractWidget;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.SubSystemGlossaryPanel;
import rpg.project.lib.api.client.types.DisplayType;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.registry.ClientPanelRegistry;

import java.util.List;
import java.util.Map;

public class PanelWidget extends ReactiveWidget {
    private final int color;
    private final ReactiveWidget objectComponent;
    public PanelWidget(int color, int width, MainSystemConfig config, ReactiveWidget objectDisplayer) {
        super(0,0, width, 0);
        this.color = color;
        this.objectComponent = objectDisplayer;
        addChild((AbstractWidget) objectComponent, PositionType.STATIC.constraint, SizeConstraints.builder().internalHeight().build());
        //gates
        for (Map.Entry<GateUtils.Type, List<SubSystemConfig>> gates : config.gates().entrySet()) {
            for (SubSystemConfig sys : gates.getValue()) {
                SubSystemGlossaryPanel panel = ClientPanelRegistry.getGatePanel(sys.getType().getId(), gates.getKey());
                if (panel == null) continue;
                addChild((AbstractWidget) panel.make(sys),
                        PositionConstraints.offset(10, 0),
                        SizeConstraints.builder().internalHeight().build());
            }
        }
        //progression
        for (SubSystemConfig sys : config.progression()) {
            SubSystemGlossaryPanel panel = ClientPanelRegistry.getProgressionPanel(sys.getType().getId());
            if (panel == null) continue;
            addChild((AbstractWidget) panel.make(sys),
                    PositionConstraints.offset(10, 0),
                    SizeConstraints.builder().internalHeight().build());
        }
        //abilities
        for (SubSystemConfig sys : config.abilities()) {
            SubSystemGlossaryPanel panel = ClientPanelRegistry.getAbilityPanel(sys.getType().getId());
            if (panel == null) continue;
            addChild((AbstractWidget) panel.make(sys),
                    PositionConstraints.offset(10, 0),
                    SizeConstraints.builder().internalHeight().build());
        }
        //features
        for (SubSystemConfig sys : config.features()) {
            SubSystemGlossaryPanel panel = ClientPanelRegistry.getFeaturePanel(sys.getType().getId());
            if (panel == null) continue;
            addChild((AbstractWidget) panel.make(sys),
                    PositionConstraints.offset(10, 0),
                    SizeConstraints.builder().internalHeight().build());
        }
        this.arrangeElements();
        this.setHeight(this.visibleChildren().stream()
                .map(poser -> poser.get().getHeight())
                .reduce(Integer::sum).orElse(0));
    }

    @Override public DisplayType getDisplayType() {return DisplayType.BLOCK;}

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getRight(), this.getBottom(), color);
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}

    @Override
    public boolean applyFilter(Filter filter) {
        //run through each child to see if it needs to filter
        getChildren().forEach(poser -> {
            if (poser.get() instanceof ReactiveWidget widget)
                widget.visible = !widget.applyFilter(filter);
        });
        //if either the main object or the children aren't displayed, filter this panel
        this.setHeight(visibleChildren().stream().map(poser -> poser.get().getHeight()).reduce(Integer::sum).orElse(0));
        return !objectComponent.visible || visibleChildren().isEmpty();
    }
}
