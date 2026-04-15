package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.data.ObjectType;

public class EventHeader extends ReactiveWidget {
    private final Identifier id;
    private final String name;
    public EventHeader(Identifier event) {
        super(0,0,50,20);
        this.id = event;
        this.name = id.toString();
        addString(Component.literal(name), PositionConstraints.grid(0, 0), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.EVENT);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}
}
