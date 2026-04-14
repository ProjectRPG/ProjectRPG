package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ItemDisplayWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.util.RegistryUtil;

public class EntityHeader extends ReactiveWidget {
    private final Identifier id;
    private final String name;
    public EntityHeader(EntityType<?> entity) {
        super(0,0,50,20);
        Minecraft mc = Minecraft.getInstance();
        this.id = RegistryUtil.getId(mc.player.registryAccess(), entity);
        this.name = entity.getDescription().toString();
        addChild(new EntityRenderer(entity)
                , PositionConstraints.grid(0,0)
                , SizeConstraints.builder().absoluteHeight(16).absoulteWidth(16).build());
        addString(entity.getDescription(), PositionConstraints.grid(0, 1), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.ENTITY);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}
}
