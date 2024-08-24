package rpg.project.lib.internal.client.glossary;

import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.neoforged.neoforge.client.gui.widget.ScrollPanel;

public class ObjectScroll extends ScrollPanel {

    public ObjectScroll(int width, int height, int top, int left) {
        super(Minecraft.getInstance(), width, height, top, left);
    }

    @Override
    protected int getContentHeight() {
        return 0;
    }

    @Override
    protected void drawPanel(GuiGraphics guiGraphics, int entryRight, int relativeY, Tesselator tess, int mouseX, int mouseY) {

    }

    @Override
    public NarrationPriority narrationPriority() {return NarrationPriority.NONE;}

    @Override
    public void updateNarration(NarrationElementOutput pNarrationElementOutput) {}
}
