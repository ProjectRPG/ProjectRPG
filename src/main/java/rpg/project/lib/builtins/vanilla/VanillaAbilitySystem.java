package rpg.project.lib.builtins.vanilla;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.builtins.vanilla.VanillaAbilityConfigType.VanillaAbilityConfig;

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
	public void abilityActivationCallback(Ability ability, CompoundTag data, Player player, EventContext context) {
		ability.status().apply(player, data, context).forEach(line -> ((VanillaAbilityPanel)getSidePanelProvider()).addLine(line));
	}

	@Override
	public SidePanelContentProvider getSidePanelProvider() {
		return VanillaAbilityPanel.INSTANCE;
	}
}
