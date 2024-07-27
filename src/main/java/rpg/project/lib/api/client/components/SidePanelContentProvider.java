package rpg.project.lib.api.client.components;

import net.minecraft.client.gui.GuiGraphics;
import rpg.project.lib.api.Hub;

public interface SidePanelContentProvider {
    void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, float pPartialTick, Hub core);
}
