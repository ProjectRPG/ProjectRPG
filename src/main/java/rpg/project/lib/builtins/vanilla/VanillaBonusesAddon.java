package rpg.project.lib.builtins.vanilla;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionDataType;
import rpg.project.lib.builtins.vanilla.VanillaBonusConfigType.VanillaBonusConfig;
import rpg.project.lib.internal.util.RegistryUtil;

public class VanillaBonusesAddon implements ProgressionAddon{

	@Override
	public <T extends ProgressionDataType> T modifyProgression(Hub core, EventContext context, T dataIn) {
		Identifier eventID = context.getParam(EventContext.EVENT_ID);
		List<VanillaBonusConfig.Bonus> dimConfig = get(eventID, context, dataIn,
				core.getProgressionData(
					VanillaBonusConfigType.IMPL,
					ObjectType.DIMENSION,
					RegistryUtil.getDimension(context.getLevel())));

		List<VanillaBonusConfig.Bonus> biomeConfig = get(eventID, context, dataIn,
				core.getProgressionData(
					VanillaBonusConfigType.IMPL,
					ObjectType.BIOME,
					RegistryUtil.getId(context.getLevel().getBiome(context.getParam(LootContextParams.THIS_ENTITY).blockPosition()))));
		
		List<VanillaBonusConfig.Bonus> allConfigs = getItemConfigs(core, context.getActor(), eventID, context, dataIn);
		allConfigs.addAll(biomeConfig);
		allConfigs.addAll(dimConfig);
		
		for (VanillaBonusConfig.Bonus bonus : allConfigs) {
			dataIn.modify(bonus.modifier(), bonus.value());
		}
		return dataIn;
	}

	private <T extends ProgressionDataType> List<VanillaBonusConfig.Bonus> get(Identifier eventID, EventContext context, T dataIn, Optional<SubSystemConfig> config) {
		return config.map(ssc -> ((VanillaBonusConfig)ssc).values().getOrDefault(eventID, List.of()).stream()
				.filter(bonus -> bonus.value().getType().equals(dataIn.getType())
						&& bonus.conditions().map(wrapper -> wrapper.test(context)).orElse(false)).toList())
				.orElse(List.of());
	}

	private <T extends ProgressionDataType> List<VanillaBonusConfig.Bonus> getItemConfigs(Hub core, Player player, Identifier eventID, EventContext context, T dataIn) {
		List<VanillaBonusConfig.Bonus> outList = new ArrayList<>();
		List<ItemStack> wornAndHeld = Arrays.asList(
				player.getItemBySlot(EquipmentSlot.MAINHAND),
				player.getItemBySlot(EquipmentSlot.OFFHAND),
				player.getItemBySlot(EquipmentSlot.HEAD),
				player.getItemBySlot(EquipmentSlot.CHEST),
				player.getItemBySlot(EquipmentSlot.LEGS),
				player.getItemBySlot(EquipmentSlot.FEET));
		
		for (ItemStack stack : wornAndHeld) {
			if (stack.isEmpty()) continue;
			outList.addAll(get(eventID, context, dataIn,
					core.getProgressionData(VanillaBonusConfigType.IMPL, ObjectType.ITEM, RegistryUtil.getId(player.registryAccess(), stack))));
		}
		return outList;
	}
}
