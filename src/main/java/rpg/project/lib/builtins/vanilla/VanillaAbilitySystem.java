package rpg.project.lib.builtins.vanilla;

import java.util.List;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.client.components.SidePanelContentProvider;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.builtins.vanilla.VanillaAbilityConfigType.VanillaAbilityConfig;

public class VanillaAbilitySystem implements AbilitySystem{

	@Override
	public List<CompoundTag> getAbilitiesForContext(Hub core, ResourceLocation eventID, EventContext context) {
		return core.getAbilityData(VanillaAbilityConfigType.IMPL, ObjectType.EVENT, eventID)
			.map(config -> ((VanillaAbilityConfig)config).data())
			.orElse(List.of());
	}

	@Override
	public SidePanelContentProvider getSidePanelProvider() {
		return new VanillaAbilityPanel();
	}
}
