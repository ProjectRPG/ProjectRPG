package rpg.project.lib.internal.client.glossary.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.Level;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.api.data.ObjectType;

public class EffectHeader extends ReactiveWidget {
    private final Identifier id, sprite;
    private final String name;
    public EffectHeader(Holder<MobEffect> mobEffectHolder) {
        super(0,0,50,20);
        Minecraft mc = Minecraft.getInstance();
        this.id = mobEffectHolder.unwrapKey().get().identifier();
        this.name = id.toString();
        this.sprite = Identifier.fromNamespaceAndPath(id.getNamespace(),"textures/mob_effect/" + id.getPath() + ".png");
        addChild(new MobIcon(sprite),
                PositionConstraints.grid(0,0),
                SizeConstraints.builder().absoluteHeight(18).absoulteWidth(18).build());
        addString(Component.literal(name), PositionConstraints.grid(0, 1), textConstraint);
    }

    @Override
    public boolean applyFilter(Filter filter) {
        return !(filter.matchesTextFilter(id.toString())
                || filter.matchesTextFilter(name))
                || !filter.matchesObject(ObjectType.EFFECT);
    }

    @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    @Override public DisplayType getDisplayType() {return DisplayType.GRID;}

    private static class MobIcon extends AbstractWidget {
        private final Identifier sprite;
        protected MobIcon(Identifier sprite) {
            super(0,0,0,0, Component.literal(sprite.toString()));
            this.sprite = sprite;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int i, int i1, float v) {
            graphics.blit(RenderPipelines.GUI_TEXTURED, sprite, this.getX(), this.getY(), 0, 0, this.width, this.height, 18, 18, 18, 18);
        }

        @Override protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
    }
}
