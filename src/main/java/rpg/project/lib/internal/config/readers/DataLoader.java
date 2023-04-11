package rpg.project.lib.internal.config.readers;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import rpg.project.lib.api.data.DataSource;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid=Reference.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class DataLoader {
	private static final Logger DATA_LOGGER = LogManager.getLogger();	
	
	@SubscribeEvent
	public static void onTagLoad(TagsUpdatedEvent event) {
		
	}
	//TODO build the base codecs for everything
	
//	@SuppressWarnings("unchecked")
//	public <T extends DataSource<T>> void applyData(ObjectType type, Map<ResourceLocation, T> data) {
//		switch (type) {
//		case ITEM -> {ITEM_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends ObjectData>) data);}
//		case BLOCK -> {BLOCK_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends ObjectData>) data);}
//		case ENTITY -> {ENTITY_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends ObjectData>) data);}
//		case DIMENSION -> {DIMENSION_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends LocationData>) data);}
//		case BIOME -> {BIOME_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends LocationData>) data);}
//		case PLAYER -> {PLAYER_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends PlayerData>) data);}
//		case ENCHANTMENT -> {ENCHANTMENT_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends EnhancementsData>) data);}
//		case EFFECT -> {EFFECT_LOADER.data.putAll((Map<? extends ResourceLocation, ? extends EnhancementsData>) data);}
//		default -> {}}
//		printData((Map<ResourceLocation, ? extends Record>) data);
//	}
//	
//	public MergeableCodecDataManager<?, ?> getLoader(ObjectType type) {
//		return switch (type) {
//		case ITEM -> ITEM_LOADER;
//		case BLOCK -> BLOCK_LOADER;
//		case ENTITY -> ENTITY_LOADER;
//		case BIOME -> BIOME_LOADER;
//		case DIMENSION -> DIMENSION_LOADER;
//		case ENCHANTMENT -> ENCHANTMENT_LOADER;
//		case EFFECT -> EFFECT_LOADER;
//		case PLAYER -> PLAYER_LOADER;
//		default -> null;};
//	}
//	
//	public MergeableCodecDataManager<?, ?> getLoader(ModifierDataType type) {
//		return switch(type) {
//		case WORN, HELD -> ITEM_LOADER;
//		case DIMENSION -> DIMENSION_LOADER;
//		case BIOME -> BIOME_LOADER;
//		default -> null;};
//	}
//	
//	public static final ExecutableListener RELOADER = new ExecutableListener(() -> {
//		Core.get(LogicalSide.SERVER).getLoader().resetData();
//	});
//	
//	public void resetData() {
//		ITEM_LOADER.clearData();
//		BLOCK_LOADER.clearData();
//		ENTITY_LOADER.clearData();
//		BIOME_LOADER.clearData();
//		DIMENSION_LOADER.clearData();
//		PLAYER_LOADER.clearData();
//		ENCHANTMENT_LOADER.clearData();
//		EFFECT_LOADER.clearData();
//	}
//	public final MergeableCodecDataManager<DataSource<?>, Item> ITEM_LOADER = new MergeableCodecDataManager<>(
//			"prpg/items", DATA_LOGGER, ObjectData.CODEC, this::mergeLoaderData, this::printData, ObjectData::new, ForgeRegistries.ITEMS);
//	public final MergeableCodecDataManager<ObjectData, Block> BLOCK_LOADER = new MergeableCodecDataManager<>(
//			"prpg/blocks", DATA_LOGGER, ObjectData.CODEC, this::mergeLoaderData, this::printData, ObjectData::new, ForgeRegistries.BLOCKS);
//	public final MergeableCodecDataManager<ObjectData, EntityType<?>> ENTITY_LOADER = new MergeableCodecDataManager<>(
//			"prpg/entities", DATA_LOGGER, ObjectData.CODEC, this::mergeLoaderData, this::printData, ObjectData::new, ForgeRegistries.ENTITY_TYPES);
//	public final MergeableCodecDataManager<LocationData, Biome> BIOME_LOADER = new MergeableCodecDataManager<>(
//			"prpg/biomes", DATA_LOGGER, LocationData.CODEC, this::mergeLoaderData, this::printData, LocationData::new, ForgeRegistries.BIOMES);
//	public final MergeableCodecDataManager<LocationData, Level> DIMENSION_LOADER = new MergeableCodecDataManager<>(
//			"prpg/dimensions", DATA_LOGGER, LocationData.CODEC, this::mergeLoaderData, this::printData, LocationData::new, null);
//	public final MergeableCodecDataManager<PlayerData, Player> PLAYER_LOADER = new MergeableCodecDataManager<>(
//			"prpg/players", DATA_LOGGER, PlayerData.CODEC, this::mergeLoaderData, this::printData, PlayerData::new, null);
//	public final MergeableCodecDataManager<EnhancementsData, Enchantment> ENCHANTMENT_LOADER = new MergeableCodecDataManager<>(
//			"prpg/enchantments", DATA_LOGGER, EnhancementsData.CODEC, this::mergeLoaderData, this::printData, EnhancementsData::new, ForgeRegistries.ENCHANTMENTS);
//	public final MergeableCodecDataManager<EnhancementsData, MobEffect> EFFECT_LOADER = new MergeableCodecDataManager<>(
//			"prpg/effects", DATA_LOGGER, EnhancementsData.CODEC, this::mergeLoaderData, this::printData, EnhancementsData::new, ForgeRegistries.MOB_EFFECTS);
	
	
	private <T extends DataSource<T>> T mergeLoaderData(final List<T> raws) {
		T out = raws.stream().reduce((existing, element) -> existing.combine(element)).get();
		return out.isUnconfigured() ? null : out;
	}
	
	private void printData(Map<ResourceLocation, ? extends Record> data) {
		data.forEach((id, value) -> {
			if (id == null || value == null) return;
			MsLoggy.INFO.log(LOG_CODE.DATA, "Object: {} with Data: {}", id.toString(), value.toString());
		});
	}
}
