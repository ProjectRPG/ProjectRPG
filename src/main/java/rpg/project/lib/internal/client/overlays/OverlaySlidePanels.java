package rpg.project.lib.internal.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.ModConfigSpec;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.util.Reference;

public class OverlaySlidePanels implements LayeredDraw.Layer {
    protected static final ResourceLocation TEXTURE_LOCATION = Reference.resource("textures/overlay_background.png");

    private SidePanelContentProvider provider;
    private double openAmount = 5.0;
    private final double ratioX;
    private final double ratioY;
    private final double ratioWidth;
    private final double ratioHeight;
    private final ModConfigSpec.BooleanValue openSetting;
    private final Core core = Core.get(LogicalSide.CLIENT);

    public OverlaySlidePanels(boolean openLeft, double percentFromTop, double percentHeight, double percentWidth, ModConfigSpec.BooleanValue openSetting) {
        provider = openLeft
                ? Config.PROG_ON_LEFT.get() ? core.getAbility().getSidePanelProvider() : core.getProgression().getSidePanelProvider()
                : Config.PROG_ON_LEFT.get() ? core.getProgression().getSidePanelProvider() : core.getAbility().getSidePanelProvider();
        this.ratioX = openLeft ? 1.0: 0.0;
        this.ratioY = percentFromTop;
        this.ratioHeight = percentHeight;
        this.ratioWidth = percentWidth;
        this.openSetting = openSetting;
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, DeltaTracker pDeltaTracker) {
        final int anchorX = (int)((pGuiGraphics.guiWidth() * ratioX) - openAmount);
        final int anchorY = (int)(pGuiGraphics.guiHeight() * ratioY);
        final int height = (int)(pGuiGraphics.guiHeight() * ratioHeight);
        final int width = (int)(pGuiGraphics.guiWidth() * ratioWidth);
        final double scale = Minecraft.getInstance().getWindow().getGuiScale();
        final int offset = scale == 1d ? 20 : 10;
        setExtensionValue(width);
        pGuiGraphics.blit(TEXTURE_LOCATION, anchorX, anchorY, 0, 0, 0, width, height, width, height);
        //Constrain and render the delegated content.
        pGuiGraphics.enableScissor(anchorX + offset, anchorY + offset, anchorX + width - offset, anchorY + height - offset);
        provider.render(pGuiGraphics, anchorY + offset, anchorX + offset, width, height, scale, pDeltaTracker.getGameTimeDeltaPartialTick(true), core);
        pGuiGraphics.disableScissor();
    }

    /**Change the position of the panel based on current position relative
     * to the open/closed setting.
     *
     * @param maxWidth the upper limit for this to extend to when open.
     */
    private void setExtensionValue(int maxWidth) {
        if (openSetting.get() && openAmount < maxWidth)
            openAmount += Config.SIDE_MENU_SPEED.get();
        else if (!openSetting.get() && openAmount > 0)
            openAmount -= Config.SIDE_MENU_SPEED.get();
    }
}
