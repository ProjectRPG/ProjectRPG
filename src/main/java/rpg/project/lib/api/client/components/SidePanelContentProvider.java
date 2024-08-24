package rpg.project.lib.api.client.components;

import net.minecraft.client.gui.GuiGraphics;
import rpg.project.lib.api.Hub;

public interface SidePanelContentProvider {
    void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, double scale, float pPartialTick, Hub core);
}
