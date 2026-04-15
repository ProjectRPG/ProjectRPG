package rpg.project.lib.builtins.abilitypanels;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;

public class EffectAbilityPanel extends ReactiveWidget {
    protected EffectAbilityPanel(Player player, CompoundTag config) {
        super(0,0,0,0);

    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.BLOCK;
    }
}
