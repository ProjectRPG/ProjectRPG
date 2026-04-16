package rpg.project.lib.builtins.abilitypanels;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.ResponsiveLayout;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class EffectAbilityPanel extends ReactiveWidget {
    public EffectAbilityPanel(Player player, CompoundTag config) {
        super(0,0,0,0);
        addString(LangProvider.PERK_EFFECT_DESC.asComponent(), PositionType.STATIC.constraint, textConstraint);

        Identifier effectID = Identifier.parse(config.getStringOr("effect", "prpg:missing"));
        MobEffect effect = player.registryAccess().lookupOrThrow(Registries.MOB_EFFECT).getValue(effectID);
        addString(LangProvider.PERK_EFFECT_STATUS_1.asComponent(effect == null ? Component.literal(effectID.toString()) : effect.getDisplayName()), PositionConstraints.offset(10, 0), textConstraint);

        Component modifier = Component.literal(String.valueOf(config.getIntOr(AbilityUtils.MODIFIER, -1 ) + 1));
        Component duration = Component.literal(String.valueOf(config.getIntOr(AbilityUtils.DURATION, 0)));
        addString(LangProvider.PERK_EFFECT_STATUS_2.asComponent(modifier, duration), PositionConstraints.offset(10, 0), textConstraint);

        Component ambient = Component.literal(String.valueOf(config.getBooleanOr(AbilityUtils.AMBIENT, false)));
        Component visible = Component.literal(String.valueOf(config.getBooleanOr(AbilityUtils.VISIBLE, true)));
        addString(LangProvider.PERK_EFFECT_STATUS_3.asComponent(ambient, visible), PositionConstraints.offset(10, 0), textConstraint);
    }

    //override to force the universal application of the text color
    @Override
    public ResponsiveLayout addString(Component text, PositionConstraints type, SizeConstraints constraints) {
        MutableComponent newText = text.copy().withColor(0xFF5053fc);
        return super.addString(newText, type, constraints);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    public DisplayType getDisplayType() {
        return DisplayType.BLOCK;
    }
}
