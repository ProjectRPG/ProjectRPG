package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionDataType;
import rpg.project.lib.builtins.vanilla.VanillaBonusConfigType.VanillaBonusConfig;
import rpg.project.lib.internal.util.RegistryUtil;

public class VanillaBonusesAddon implements ProgressionAddon{

	@Override
	public ProgressionDataType modifyProgression(Hub core, EventContext context, ProgressionDataType dataIn) {
		VanillaBonusConfig dimConfig = (VanillaBonusConfig) core
				.getProgressionData(
						VanillaBonusConfigType.IMPL, 
						ObjectType.DIMENSION, 
						context.actor().level().dimension().location())
				.orElse(null);
		
		VanillaBonusConfig biomeConfig = (VanillaBonusConfig) core
				.getProgressionData(
						VanillaBonusConfigType.IMPL, 
						ObjectType.BIOME, 
						RegistryUtil.getId(context.actor().level().getBiome(context.actor().blockPosition())))
				.orElse(null);
		
		List<VanillaBonusConfig> allConfigs = getItemConfigs(core, context.actor());
		allConfigs.add(biomeConfig);
		allConfigs.add(dimConfig);
		
		//TODO figure out what I was doing with this to modify XP
		return null;
	}

	private List<VanillaBonusConfig> getItemConfigs(Hub core, Player player) {
		List<VanillaBonusConfig> outList = new ArrayList<>();
		List<ItemStack> wornAndHeld = Arrays.asList(
				player.getInventory().offhand.get(0),
				player.getInventory().getSelected(),
				player.getInventory().getArmor(0),
				player.getInventory().getArmor(1),
				player.getInventory().getArmor(2),
				player.getInventory().getArmor(3));
		
		for (ItemStack stack : wornAndHeld) {
			if (stack.isEmpty()) continue;
			core.getProgressionData(VanillaBonusConfigType.IMPL, ObjectType.ITEM, RegistryUtil.getId(stack))
				.ifPresent(config -> outList.add((VanillaBonusConfig) config));
		}
		return outList;
	}
}
