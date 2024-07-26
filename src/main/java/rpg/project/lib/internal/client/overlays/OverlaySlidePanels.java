package rpg.project.lib.internal.client.overlays;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.neoforge.common.ModConfigSpec;
import rpg.project.lib.internal.config.Config;
import rpg.project.lib.internal.util.Reference;

public class OverlaySlidePanels implements LayeredDraw.Layer {
    private double openAmount = 5.0;
    private final double ratioX;
    private final double ratioY;
    private final double ratioWidth;
    private final double ratioHeight;
    private final boolean extendLeft;
    private final ModConfigSpec.BooleanValue openSetting;
    private final Minecraft mc = Minecraft.getInstance();

    public OverlaySlidePanels(boolean openLeft, double percentFromTop, double percentHeight, double percentWidth, ModConfigSpec.BooleanValue openSetting) {
        this.extendLeft = openLeft;
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
        setExtentionValue(width);
        pGuiGraphics.blit(anchorX, anchorY, 0, width, height, mc.getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(MissingTextureAtlasSprite.getLocation()));
    }

    private void setExtentionValue(int maxWidth) {
        if (openSetting.get() && openAmount < maxWidth)
            openAmount += Config.SIDE_MENU_SPEED.get();
        else if (!openSetting.get() && openAmount > 0)
            openAmount -= Config.SIDE_MENU_SPEED.get();
    }
}
