package rpg.project.lib.builtins.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import rpg.project.lib.api.client.components.SidePanelContentProvider;

public class VanillaProgressionPanel implements SidePanelContentProvider {
    Minecraft mc = Minecraft.getInstance();

    @Override
    public void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, float pPartialTick) {
        pGuiGraphics.drawString(mc.font, Component.literal("Vanilla Progression Panel"), left, top, 0xFFFFFF);
    }
}
