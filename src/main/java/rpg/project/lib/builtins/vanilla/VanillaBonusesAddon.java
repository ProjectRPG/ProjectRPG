package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
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
						RegistryUtil.getDimension(context.getLevel()))
				.orElse(null);

		VanillaBonusConfig biomeConfig = (VanillaBonusConfig) core
				.getProgressionData(
						VanillaBonusConfigType.IMPL,
						ObjectType.BIOME,
						RegistryUtil.getId(context.getLevel().getBiome(context.getParam(LootContextParams.THIS_ENTITY).blockPosition())))
				.orElse(null);
		
		List<VanillaBonusConfig> allConfigs = getItemConfigs(core, context.getActor());
		allConfigs.add(biomeConfig);
		allConfigs.add(dimConfig);
		
		//TODO figure out what I was doing with this to modify XP
		return null;
	}

	private List<VanillaBonusConfig> getItemConfigs(Hub core, Player player) {
		List<VanillaBonusConfig> outList = new ArrayList<>();
		List<ItemStack> wornAndHeld = Arrays.asList(
				player.getItemBySlot(EquipmentSlot.MAINHAND),
				player.getItemBySlot(EquipmentSlot.OFFHAND),
				player.getItemBySlot(EquipmentSlot.HEAD),
				player.getItemBySlot(EquipmentSlot.CHEST),
				player.getItemBySlot(EquipmentSlot.LEGS),
				player.getItemBySlot(EquipmentSlot.FEET));
		
		for (ItemStack stack : wornAndHeld) {
			if (stack.isEmpty()) continue;
			core.getProgressionData(VanillaBonusConfigType.IMPL, ObjectType.ITEM, RegistryUtil.getId(stack))
				.ifPresent(config -> outList.add((VanillaBonusConfig) config));
		}
		return outList;
	}
}
