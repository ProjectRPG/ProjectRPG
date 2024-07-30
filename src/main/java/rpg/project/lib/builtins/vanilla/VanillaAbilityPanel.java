package rpg.project.lib.builtins.vanilla;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.MutableComponent;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider;

import java.util.ArrayList;
import java.util.List;

public class VanillaAbilityPanel implements SidePanelContentProvider {
    Minecraft mc = Minecraft.getInstance();
    private final List<MutableComponent> gainList = new ArrayList<>();

    public static final VanillaAbilityPanel INSTANCE = new VanillaAbilityPanel();
    private VanillaAbilityPanel() {
        gainList.add(LangProvider.ABILITY_SIDE_PANEL_HEADER.asComponent());
    }

    public void addLine(MutableComponent component) {
        gainList.add(component);
        if (gainList.size() > 12)
            gainList.remove(1);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int top, int left, int width, int height, float pPartialTick, Hub core) {
        for (int i = 0; i < gainList.size(); i++) {
            pGuiGraphics.drawScrollingString(mc.font, gainList.get(i), left, width - left, top + (i * 12), 0xFFFFFF);
        }
    }
}
