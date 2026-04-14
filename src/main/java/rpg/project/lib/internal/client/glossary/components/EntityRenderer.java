package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public class EntityRenderer extends AbstractWidget {
    private final LivingEntity entity;

    public EntityRenderer(EntityType<?> entity) {
        super(0,0,0,0, entity.getDescription());
        Entity rawEntity = entity.create(Minecraft.getInstance().level, EntitySpawnReason.NATURAL);
        this.entity = rawEntity instanceof LivingEntity living ? living : null;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int i, int i1, float v) {
        if (entity != null)
            InventoryScreen.renderEntityInInventoryFollowsAngle(graphics, getX(), getY(), getX()+16, getY()+16, Math.max(1, 10 / Math.max(1, (int) entity.getBoundingBox().getSize())), 0, 0f, 0f, entity);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
