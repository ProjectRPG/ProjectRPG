package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.data.ObjectType;

public class DimensionHeader extends ReactiveWidget {
    private final Identifier id;
    private final String name;
    public DimensionHeader(ResourceKey<Level> dimension) {
        super(0,0,50,20);
        Minecraft mc = Minecraft.getInstance();
        this.id = dimension.identifier();
        this.name = id.toString();
        addString(Component.literal(name), PositionConstraints.grid(0, 0), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.DIMENSION);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}
}
