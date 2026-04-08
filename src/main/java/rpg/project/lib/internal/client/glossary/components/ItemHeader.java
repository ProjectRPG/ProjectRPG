package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.util.RegistryUtil;

public class ItemHeader extends ReactiveWidget {
    private final Identifier id;
    private final String name;
    public ItemHeader(ItemStack stack) {
        super(0,0,50,20);
        Minecraft mc = Minecraft.getInstance();
        this.id = RegistryUtil.getId(mc.player.registryAccess(), stack);
        this.name = stack.getDisplayName().getString();
        addChild(new ItemDisplayWidget(mc, 0,0,16,16, stack.getDisplayName(), stack, false, true)
                , PositionConstraints.grid(0,0)
                , SizeConstraints.builder().absoluteHeight(16).absoulteWidth(16).build());
        addString(stack.getDisplayName(), PositionConstraints.grid(0, 1), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.ITEM);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}
}
