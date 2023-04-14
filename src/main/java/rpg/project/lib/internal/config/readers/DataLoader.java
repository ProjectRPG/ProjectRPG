package rpg.project.lib.internal.config.readers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent.UpdateCause;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid=Reference.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class DataLoader {
	private static final Logger DATA_LOGGER = LogManager.getLogger();	
	
	@SubscribeEvent
	public static void onTagLoad(TagsUpdatedEvent event) {
		Core core = Core.get(event.getUpdateCause() == UpdateCause.CLIENT_PACKET_RECEIVED ? LogicalSide.CLIENT : LogicalSide.SERVER);
		core.getLoader().ITEM_LOADER.postProcess();
		core.getLoader().BLOCK_LOADER.postProcess();
		core.getLoader().ENTITY_LOADER.postProcess();
		//core.getLoader().DIMENSION_LOADER.postProcess();
		core.getLoader().BIOME_LOADER.postProcess();
	}
	
	public void applyData(ObjectType type, Map<ResourceLocation, MainSystemConfig> data) {
		switch (type) {
		case ITEM -> ITEM_LOADER.data.putAll(data);
		case BLOCK -> BLOCK_LOADER.data.putAll(data);
		case ENTITY -> ENTITY_LOADER.data.putAll(data);
		case DIMENSION -> DIMENSION_LOADER.data.putAll(data);
		case BIOME -> BIOME_LOADER.data.putAll(data);
		case PLAYER -> PLAYER_LOADER.data.putAll(data);
		case ENCHANTMENT -> ENCHANTMENT_LOADER.data.putAll(data);
		case EFFECT -> EFFECT_LOADER.data.putAll(data);
		default -> {}}
		printData((Map<ResourceLocation, ? extends Record>) data);
	}
//	
	public MergeableCodecDataManager<?> getLoader(ObjectType type) {
		return switch (type) {
		case ITEM -> ITEM_LOADER;
		case BLOCK -> BLOCK_LOADER;
		case ENTITY -> ENTITY_LOADER;
		case BIOME -> BIOME_LOADER;
		case DIMENSION -> DIMENSION_LOADER;
		case ENCHANTMENT -> ENCHANTMENT_LOADER;
		case EFFECT -> EFFECT_LOADER;
		case PLAYER -> PLAYER_LOADER;
		default -> null;};
	}
	
	public static final ExecutableListener RELOADER = new ExecutableListener(() -> {
		Core.get(LogicalSide.SERVER).getLoader().resetData();
	});
	
	public void resetData() {
		ITEM_LOADER.clearData();
		BLOCK_LOADER.clearData();
		ENTITY_LOADER.clearData();
		BIOME_LOADER.clearData();
		DIMENSION_LOADER.clearData();
		PLAYER_LOADER.clearData();
		ENCHANTMENT_LOADER.clearData();
		EFFECT_LOADER.clearData();
	}
	
	public final MergeableCodecDataManager<Item> ITEM_LOADER = new MergeableCodecDataManager<Item>(
			"prpg/items", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.ITEMS);
	public final MergeableCodecDataManager<Block> BLOCK_LOADER = new MergeableCodecDataManager<>(
			"prpg/blocks", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.BLOCKS);
	public final MergeableCodecDataManager<EntityType<?>> ENTITY_LOADER = new MergeableCodecDataManager<>(
			"prpg/entities", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.ENTITY_TYPES);
	public final MergeableCodecDataManager<Biome> BIOME_LOADER = new MergeableCodecDataManager<>(
			"prpg/biomes", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.BIOMES);
	public final MergeableCodecDataManager<Level> DIMENSION_LOADER = new MergeableCodecDataManager<>(
			"prpg/dimensions", DATA_LOGGER, this::mergeLoaderData, this::printData, null);
	public final MergeableCodecDataManager<Player> PLAYER_LOADER = new MergeableCodecDataManager<>(
			"prpg/players", DATA_LOGGER, this::mergeLoaderData, this::printData, null);
	public final MergeableCodecDataManager<Enchantment> ENCHANTMENT_LOADER = new MergeableCodecDataManager<>(
			"prpg/enchantments", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.ENCHANTMENTS);
	public final MergeableCodecDataManager<MobEffect> EFFECT_LOADER = new MergeableCodecDataManager<>(
			"prpg/effects", DATA_LOGGER, this::mergeLoaderData, this::printData, ForgeRegistries.MOB_EFFECTS);
	
	
	private MainSystemConfig mergeLoaderData(final List<MainSystemConfig> raws) {
		MainSystemConfig out = (MainSystemConfig) raws.stream().reduce((existing, element) -> (MainSystemConfig)existing.combine(element)).get();
		return out.isUnconfigured() ? null : out;
	}
	
	private void printData(Map<ResourceLocation, ? extends Record> data) {
		data.forEach((id, value) -> {
			if (id == null || value == null) return;
			MsLoggy.INFO.log(LOG_CODE.DATA, "Object: {} with Data: {}", id.toString(), value.toString());
		});
	}
}
