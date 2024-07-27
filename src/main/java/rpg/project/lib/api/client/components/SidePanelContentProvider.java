package rpg.project.lib.api.client.components;

import net.minecraft.client.gui.GuiGraphics;

public interface SidePanelContentProvider {
    void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, float pPartialTick);
}
