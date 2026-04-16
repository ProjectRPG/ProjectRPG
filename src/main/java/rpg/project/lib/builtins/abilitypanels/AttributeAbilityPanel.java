package rpg.project.lib.builtins.abilitypanels;

import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.client.ReactiveWidget;
import rpg.project.lib.api.client.ResponsiveLayout;
import rpg.project.lib.api.client.types.DisplayType;
import rpg.project.lib.api.client.types.PositionType;
import rpg.project.lib.api.client.wrappers.PositionConstraints;
import rpg.project.lib.api.client.wrappers.SizeConstraints;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class AttributeAbilityPanel extends ReactiveWidget {
    public AttributeAbilityPanel(Player player, CompoundTag config) {
        super(0,0,0,0);
        Identifier id = Identifier.parse(config.getStringOr(AbilityUtils.ATTRIBUTE, "prpg:missing"));
        Attribute attribute = player.registryAccess().lookupOrThrow(Registries.ATTRIBUTE).getValue(id);
        Component attrText = attribute == null ? Component.literal(id.toString()) : Component.translatable(attribute.getDescriptionId());
        addString(LangProvider.ATTRIBUTE_STATUS0.asComponent(attrText), PositionType.STATIC.constraint, textConstraint);

        Component base = Component.literal(String.valueOf(config.getIntOr(AbilityUtils.BASE, 0)));
        Component perLevel = Component.literal(String.valueOf(config.getIntOr(AbilityUtils.PER_LEVEL, 0)));
        Component max = Component.literal(String.valueOf(config.getDoubleOr(AbilityUtils.MAX_BOOST, 0)));
        addString(LangProvider.ATTRIBUTE_STATUS2.asComponent(base, perLevel, max), PositionType.STATIC.constraint, textConstraint);

        String containerRaw = config.getStringOr(AbilityUtils.CONTAINER_NAME, "");
        if (!containerRaw.isEmpty()) {
            Component container = Component.literal(containerRaw);
            addString(LangProvider.ATTRIBUTE_STATUS3.asComponent(container), PositionType.STATIC.constraint, textConstraint);
        }

        Component multiplicative = config.getBooleanOr(AbilityUtils.MULTIPLICATIVE, true)
                ? LangProvider.ATTRIBUTE_MULTIPLICATIVE.asComponent()
                : LangProvider.ATTRIBUTE_ADDITIVE.asComponent();
        addString(multiplicative, PositionType.STATIC.constraint, textConstraint);
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
