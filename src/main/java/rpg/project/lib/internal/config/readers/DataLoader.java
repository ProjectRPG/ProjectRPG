package rpg.project.lib.internal.config.readers;

import java.util.List;
import java.util.Map;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
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
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID, bus= EventBusSubscriber.Bus.GAME)
public class DataLoader {
	private static final Logger DATA_LOGGER = LogManager.getLogger();	
	
	@SubscribeEvent
	public static void onTagLoad(TagsUpdatedEvent event) {
		Core core = Core.get(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED ? LogicalSide.CLIENT : LogicalSide.SERVER);
		core.getLoader().ITEM_LOADER.postProcess(event.getRegistryAccess());
		core.getLoader().BLOCK_LOADER.postProcess(event.getRegistryAccess());
		core.getLoader().ENTITY_LOADER.postProcess(event.getRegistryAccess());
		//core.getLoader().DIMENSION_LOADER.postProcess();
		core.getLoader().BIOME_LOADER.postProcess(event.getRegistryAccess());
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
		case EVENT -> EVENT_LOADER.data.putAll(data);
		default -> {}}
		printData(data);
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
		case EVENT -> EVENT_LOADER;
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
		EVENT_LOADER.clearData();
	}
	
	public final MergeableCodecDataManager<Item> ITEM_LOADER = new MergeableCodecDataManager<>(
			"prpg/items", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.ITEM);
	public final MergeableCodecDataManager<Block> BLOCK_LOADER = new MergeableCodecDataManager<>(
			"prpg/blocks", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.BLOCK);
	public final MergeableCodecDataManager<EntityType<?>> ENTITY_LOADER = new MergeableCodecDataManager<>(
			"prpg/entities", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.ENTITY_TYPE);
	public final MergeableCodecDataManager<Biome> BIOME_LOADER = new MergeableCodecDataManager<>(
			"prpg/biomes", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.BIOME);
	public final MergeableCodecDataManager<Level> DIMENSION_LOADER = new MergeableCodecDataManager<>(
			"prpg/dimensions", DATA_LOGGER, this::mergeLoaderData, this::printData, null);
	public final MergeableCodecDataManager<Player> PLAYER_LOADER = new MergeableCodecDataManager<>(
			"prpg/players", DATA_LOGGER, this::mergeLoaderData, this::printData, null);
	public final MergeableCodecDataManager<Enchantment> ENCHANTMENT_LOADER = new MergeableCodecDataManager<>(
			"prpg/enchantments", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.ENCHANTMENT);
	public final MergeableCodecDataManager<MobEffect> EFFECT_LOADER = new MergeableCodecDataManager<>(
			"prpg/effects", DATA_LOGGER, this::mergeLoaderData, this::printData, Registries.MOB_EFFECT);
	public final MergeableCodecDataManager<EventProvider<?>> EVENT_LOADER = new MergeableCodecDataManager<>(
			"prpg/events", DATA_LOGGER, this::mergeLoaderData, this::printData, APIUtils.GAMEPLAY_EVENTS);
	
	
	private MainSystemConfig mergeLoaderData(final List<MainSystemConfig> raws) {
		MainSystemConfig out = raws.stream().reduce((existing, element) -> (MainSystemConfig)existing.combine(element)).get();
		return out.isUnconfigured() ? null : out;
	}
	
	private void printData(Map<ResourceLocation, ? extends Record> data) {
		data.forEach((id, value) -> {
			if (id == null || value == null) return;
			MsLoggy.INFO.log(LOG_CODE.DATA, "Object: {} with Data: {}", id.toString(), value.toString());
		});
	}
}
