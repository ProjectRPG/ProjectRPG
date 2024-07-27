package rpg.project.lib.builtins.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.client.components.SidePanelContentProvider;

public class VanillaAbilityPanel implements SidePanelContentProvider {
    Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, float pPartialTick, Hub core) {
        pGuiGraphics.drawString(mc.font, Component.literal("Vanilla Ability Panel"), left, top, 0xFFFFFF);
    }
}
