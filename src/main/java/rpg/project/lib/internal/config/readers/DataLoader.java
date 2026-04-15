package rpg.project.lib.internal.config.readers;

import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import net.minecraft.core.RegistryAccess;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.resources.Identifier;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.scripting.Scripting;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID)
public class DataLoader {
	public static final Logger DATA_LOGGER = LogManager.getLogger();
	private final EnumMap<ObjectType, MergeableCodecDataManager<?>> loaders = new EnumMap<>(ObjectType.class);

	public DataLoader() {
		Arrays.stream(ObjectType.values()).forEach(type -> loaders.put(type, type.createLoader()));
	}
	
	@SubscribeEvent
	public static void onTagLoad(TagsUpdatedEvent event) {
		Core core = Core.get(event.getUpdateCause() == TagsUpdatedEvent.UpdateCause.CLIENT_PACKET_RECEIVED ? LogicalSide.CLIENT : LogicalSide.SERVER);
		core.getLoader().all().forEach(loader -> loader.getValue().postProcess(event.getLookupProvider()));
	}
	
	public void applyData(ObjectType type, Map<Identifier, MainSystemConfig> data) {
		loaders.get(type).getData().putAll(data);
		ObjectType.printData(data);
	}

	public MergeableCodecDataManager<?> getLoader(ObjectType type) {
		return loaders.get(type);
	}
	public Set<Map.Entry<ObjectType, MergeableCodecDataManager<?>>> all() {
		return loaders.entrySet();
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
