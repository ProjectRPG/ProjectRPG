package rpg.project.lib.internal.client.glossary;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;

public class DefaultAbilityPanel extends ReactiveWidget {
    public DefaultAbilityPanel(Player player, CompoundTag config) {
        super(0,0,0,0);
        for (var entry : config.entrySet()) {
            if (entry.getKey().equals(AbilityUtils.TYPE)) continue;
            Component text = Component.literal(entry.getKey() + ": " + entry.getValue()).withColor(0xFF5053fc);
            addString(text, PositionType.STATIC.constraint, textConstraint);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.BLOCK;
    }
}
