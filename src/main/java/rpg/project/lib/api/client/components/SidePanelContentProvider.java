package rpg.project.lib.api.client.components;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import rpg.project.lib.api.Hub;

/**Provides addons with a means to supply a renderable portion for Project RPG
 * GUIs.  Addons supply a getter value for an implementation of this interface
 */
@FunctionalInterface
public interface SidePanelContentProvider {
    /**Project RPG GUIs, which call this method, will provide as parameters the
     * constraints of the rendering area.  Implementations of this method should
     * be reactive and dynamic in their rendering.  The dimensions provided to
     * this method are limited by a scissor operation to not draw outside of the
     * provided bounds.
     *
     * @param pGuiGraphics the {@link GuiGraphics} instance used by the rendering GUI
     * @param top the Y position where this panel is placed on the screen
     * @param left the X position where this panel is placed on the screen
     * @param width the width of the renderable area
     * @param height the height of the renderable area
     * @param scale the GUI scale set by the client
     * @param pPartialTick the partial ticks of the rendering GUI
     * @param core the client's instance of the {@link Hub} object
     */
    void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, double scale, float pPartialTick, Hub core);
}
