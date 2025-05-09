package rpg.project.lib.internal.config.readers;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import net.minecraft.core.RegistryAccess;
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
import rpg.project.lib.internal.config.scripting.Scripting;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID, bus= EventBusSubscriber.Bus.GAME)
public class DataLoader {
	public static final Logger DATA_LOGGER = LogManager.getLogger();
	private final EnumMap<ObjectType, MergeableCodecDataManager<?>> loaders = new EnumMap<>(ObjectType.class);

	public DataLoader() {
		Arrays.stream(ObjectType.values()).forEach(type -> loaders.put(type, type.createLoader()));
	}
	
	@SubscribeEvent
	public static void onTagLoad(TagsUpdatedEvent event) {
		Core core = Core.get(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED ? LogicalSide.CLIENT : LogicalSide.SERVER);
		core.getLoader().all().forEach(loader -> loader.postProcess(event.getLookupProvider()));
	}
	
	public void applyData(ObjectType type, Map<ResourceLocation, MainSystemConfig> data) {
		loaders.get(type).getData().putAll(data);
		ObjectType.printData(data);
	}

	public MergeableCodecDataManager<?> getLoader(ObjectType type) {
		return loaders.get(type);
	}
	public Collection<MergeableCodecDataManager<?>> all() {
		return loaders.values();
	}
	
	public ExecutableListener RELOADER;
	public static final Consumer<RegistryAccess> RELOADER_FUNCTION = access -> {
		Core.get(LogicalSide.SERVER).getLoader().resetData();
		Scripting.readFiles(access);
	};
	
	public void resetData() {
		loaders.forEach((type, loader) -> loader.clearData());
	}
}
