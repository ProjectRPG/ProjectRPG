package rpg.project.lib.builtins.vanilla;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.client.components.ClientUtils;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.builtins.vanilla.VanillaAbilityConfigType.VanillaAbilityConfig;
import rpg.project.lib.internal.setup.datagen.LangProvider;

public class VanillaAbilitySystem implements AbilitySystem{

	@Override
	public List<CompoundTag> getAbilitiesForContext(Hub core, ResourceLocation eventID, EventContext context) {
		return ((VanillaAbilityConfig)core.getAbilityData(VanillaAbilityConfigType.IMPL, ObjectType.EVENT, eventID)
			.orElse(new VanillaAbilityConfig(List.of()))).data().stream()
			.filter(wrapper -> wrapper.conditions().stream().allMatch(c -> c.test(context)))
			.map(VanillaAbilityConfig.ConditionalAbility::ability)
			.toList();
	}

	@Override
	public void abilityActivationCallback(Ability ability, CompoundTag data, Player player, EventContext context, ResourceLocation eventID) {
		VanillaAbilityPanel panel = (VanillaAbilityPanel) getSidePanelProvider();
		panel.addLine(LangProvider.ABILITY_SIDE_PANEL_EVENT_HEADER.asComponent(
			ClientUtils.getAbilityName(AbilityUtils.get(player.level().registryAccess()).registry().getKey(ability)),
			ClientUtils.getEventName(eventID)
		).setStyle(Style.EMPTY.withBold(true).withColor(TextColor.fromLegacyFormat(ChatFormatting.BLUE))));
		ability.status().apply(player, data, context).forEach(panel::addLine);
	}

	@Override
	public SidePanelContentProvider getSidePanelProvider() {
		return VanillaAbilityPanel.INSTANCE;
	}
}
