/*

The MIT License (MIT)

Copyright (c) 2020 Joseph Bettendorff a.k.a. "Commoble"

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

 */

package rpg.project.lib.internal.config.readers;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;

/**
 * Generic data loader for Codec-parsable data.
 * This works best if initialized during your mod's construction.
 * After creating the manager, subscribeAsSyncable can optionally be called on it to subscribe the manager
 * to the forge events necessary for syncing datapack data to clients.
 * @param <V> The type of the objects that the codec is parsing jsons as
 */
public class MergeableCodecDataManager<V> extends SimplePreparableReloadListener<Map<ResourceLocation, MainSystemConfig>>
{
	protected static final String JSON_EXTENSION = ".json";
	protected static final int JSON_EXTENSION_LENGTH = JSON_EXTENSION.length();
	protected static final Gson STANDARD_GSON = new Gson();
	@Nonnull
	/** Mutable, non-null map containing whatever data was loaded last time server datapacks were loaded */
	protected Map<ResourceLocation, MainSystemConfig> data = new HashMap<>();
	
	public final String folderName;
	private final Logger logger;
	private final Codec<MainSystemConfig> codec = MainSystemConfig.CODEC;
	private final Function<List<MainSystemConfig>, MainSystemConfig> merger;
	private final Consumer<Map<ResourceLocation, MainSystemConfig>> finalizer;
	private final Gson gson;
	private final Supplier<MainSystemConfig> defaultImpl = MainSystemConfig::new;
	private final ResourceKey<Registry<V>> registry;
	private final Map<ResourceLocation, MainSystemConfig> defaultSettings = new HashMap<>();
	private final Map<ResourceLocation, MainSystemConfig> overrideSettings = new HashMap<>();
	
	/**
	 * Initialize a data manager with the given folder name, codec, and merger
	 * @param folderName The name of the folder to load data from,
	 * e.g. "cheeses" would load data from "data/modid/cheeses" for all modids.
	 * Can include subfolders, e.g. "cheeses/sharp"
	 * @param logger A logger that will log parsing errors if they occur
	 * @param codec A codec that will be used to parse jsons. See drullkus's codec primer for help on creating these:
	 * https://gist.github.com/Drullkus/1bca3f2d7f048b1fe03be97c28f87910
	 * @param merger A merging function that uses a list of java-objects-that-were-parsed-from-json to create a final object.
	 * The list contains all successfully-parsed objects with the same ID from all mods and datapacks.
	 * (for a json located at "data/modid/folderName/name.json", the object's ID is "modid:name")
	 * As an example, consider vanilla's Tags: mods or datapacks can define tags with the same modid:name id,
	 * and then all tag jsons defined with the same ID are merged additively into a single set of items, etc
	 */
	public MergeableCodecDataManager(final String folderName, final Logger logger, final Function<List<MainSystemConfig>, MainSystemConfig> merger
			, final Consumer<Map<ResourceLocation, MainSystemConfig>> finalizer, ResourceKey<Registry<V>> registry)
	{
		this(folderName, logger, merger, finalizer, STANDARD_GSON, registry);
	}

	
	/**
	 * Initialize a data manager with the given folder name, codec, and merger, as well as a user-defined GSON instance.
	 * @param folderName The name of the folder to load data from,
	 * e.g. "cheeses" would load data from "data/modid/cheeses" for all modids.
	 * Can include subfolders, e.g. "cheeses/sharp"
	 * @param logger A logger that will log parsing errors if they occur
	 * @param codec A codec that will be used to parse jsons. See drullkus's codec primer for help on creating these:
	 * https://gist.github.com/Drullkus/1bca3f2d7f048b1fe03be97c28f87910
	 * @param merger A merging function that uses a list of java-objects-that-were-parsed-from-json to create a final object.
	 * The list contains all successfully-parsed objects with the same ID from all mods and datapacks.
	 * (for a json located at "data/modid/folderName/name.json", the object's ID is "modid:name")
	 * As an example, consider vanilla's Tags: mods or datapacks can define tags with the same modid:name id,
	 * and then all tag jsons defined with the same ID are merged additively into a single set of items, etc
	 * @param gson A GSON instance, allowing for user-defined deserializers. General not needed as the gson is only used to convert
	 * raw json to a JsonElement, which the Codec then parses into a proper java object.
	 */
	public MergeableCodecDataManager(final String folderName, final Logger logger, final Function<List<MainSystemConfig>, MainSystemConfig> merger
			, final Consumer<Map<ResourceLocation, MainSystemConfig>> finalizer, final Gson gson, ResourceKey<Registry<V>> registry)
	{
		this.folderName = folderName;
		this.logger = logger;
		this.merger = merger;
		this.finalizer = finalizer;
		this.gson = gson;
		this.registry = registry;
	}
	
	public Map<ResourceLocation, MainSystemConfig> getData() {return data;}
	
	public void clearData() {this.data = new HashMap<>();}
	
	public MainSystemConfig getData(ResourceLocation id) {
		return data.computeIfAbsent(id, res -> getGenericTypeInstance());
	}
	
	public MainSystemConfig getGenericTypeInstance() {return defaultImpl.get();}
	
	/**Adds default data to loader. This data is placed first in
	 * the load order before any data from file is read.  This
	 * data is intended to be overwritten by datapacks.
	 * <p><i>Note: this does not check for duplicates.
	 * Any registrations should be done during mod construction
	 * and never called again. The purpose of this registration
	 * is to cache the processor for repeated use at a
	 * later point, such as data reloads.
	 * 
	 * @param id the object registry ID
	 * @param data the object containing the specific data
	 */
	public void registerDefault(ResourceLocation id, MainSystemConfig data) {
		defaultSettings.merge(id, data, (currData, newData) -> (MainSystemConfig)currData.combine(newData));
	}
	
	/**Adds override data to the loader.  This data is applied on
	 * top of any other data.  This is a code-based hard overwrite.
	 * Unlike datapacks, this does not merge data. It hard overwrites
	 * the preceding data/configurations.
	 * <p><i>Note: this does not check for duplicates.
	 * Any registrations should be done during mod construction
	 * and never called again. The purpose of this registration
	 * is to cache the processor for repeated use at a
	 * later point, such as data reloads.
	 * 
	 * @param id the object registry ID
	 * @param data the object containing the specific data
	 */
	public void registerOverride(ResourceLocation id, MainSystemConfig data) {
		overrideSettings.merge(id, data, (currData, newData) -> (MainSystemConfig)currData.combine(newData));
	}

	/** Off-thread processing (can include reading files from hard drive) **/
	@Override
	protected Map<ResourceLocation, MainSystemConfig> prepare(final ResourceManager resourceManager, final ProfilerFiller profiler)
	{
		final Map<ResourceLocation, List<MainSystemConfig>> map = new HashMap<>();

		for (ResourceLocation resourceLocation : resourceManager.listResources(this.folderName, MergeableCodecDataManager::isStringJsonFile).keySet())
		{
			final String namespace = resourceLocation.getNamespace();
			final String filePath = resourceLocation.getPath();
			final String dataPath = filePath.substring(this.folderName.length() + 1, filePath.length() - JSON_EXTENSION_LENGTH);
			
			// this is a json with identifier "somemodid:somedata"
			final ResourceLocation jsonIdentifier = ResourceLocation.fromNamespaceAndPath(namespace, dataPath);
			// this is the list of all json objects with the given resource location (i.e. in multiple datapacks)
			final List<MainSystemConfig> unmergedRaws = new ArrayList<>();
			// it's entirely possible that there are multiple jsons with this identifier,
			// we can query the resource manager for these
			for (Resource resource : resourceManager.getResourceStack(resourceLocation))
			{
				try // with resources
				(final Reader reader = resource.openAsReader())
				{
					// read the json file and save the parsed object for later
					// this json element may return null
					final JsonElement jsonElement = GsonHelper.fromJson(this.gson, reader, JsonElement.class);
					this.codec.parse(JsonOps.INSTANCE, jsonElement)
						// resultOrPartial either returns a non-empty optional or calls the consumer given
						.resultOrPartial(MergeableCodecDataManager::throwJsonParseException)
						.ifPresent(unmergedRaws::add);
				}
				catch(RuntimeException | IOException exception)
				{
					this.logger.error("Data loader for {} could not read data {} from file {} in data pack {}", this.folderName, jsonIdentifier, resourceLocation, resource.sourcePackId(), exception); 
				}
			}			
			
			map.computeIfAbsent(jsonIdentifier, id -> new ArrayList<>()).addAll(unmergedRaws);
		}

		return MergeableCodecDataManager.mapValues(map, this.merger::apply);
	}
	
	static boolean isStringJsonFile(final ResourceLocation file)
	{
		return file.getPath().endsWith(".json");
	}
	
	static void throwJsonParseException(final String codecParseFailure)
	{
		throw new JsonParseException(codecParseFailure);
	}
	
	static <Key, In, Out> Map<Key, Out> mapValues(final Map<Key,In> inputs, final Function<In, Out> mapper)
	{
		final Map<Key,Out> newMap = new HashMap<>();
		
		inputs.forEach((key, input) -> {
			Out output = mapper.apply(input);
			if (output != null)
				newMap.put(key, output);
		});
		
		return newMap;
	}
	
	/** Main-thread processing, runs after prepare concludes **/
	@Override
	protected void apply(final Map<ResourceLocation, MainSystemConfig> processedData, final ResourceManager resourceManager, final ProfilerFiller profiler)
	{
		var debug = this.folderName;
		MsLoggy.INFO.log(LOG_CODE.DATA, "Beginning loading of data for data loader: {}", this.folderName);

		// now that we're on the main thread, we can finalize the data
		defaultSettings.forEach((key, value) -> {
			MsLoggy.DEBUG.log(LOG_CODE.DATA, "Loading API Default Data For: {} of: {}", key, value);
			processedData.merge(key, value, (existing, defaulted) -> (MainSystemConfig) existing.combine(defaulted));
		});
		MsLoggy.DEBUG.log(LOG_CODE.DATA, processedData, "Loaded Data For: {} of: {}");
		this.data.putAll(processedData);
		//Apply overrides
		MsLoggy.DEBUG.log(LOG_CODE.DATA, overrideSettings, "Loaded Override For: {} of: {}");
		this.data.putAll(overrideSettings);
	}
	
	public void postProcess(HolderLookup.Provider lookup) {
		if (registry == null) return;
		MsLoggy.DEBUG.log(LOG_CODE.DATA, "Begin PostProcessing for {}", folderName);
		for (Map.Entry<ResourceLocation, MainSystemConfig> dataRaw : new HashMap<>(this.data).entrySet()) {
			MainSystemConfig dataValue = dataRaw.getValue();
			if (dataValue.tagValues().isEmpty()) continue;
			MsLoggy.INFO.log(LOG_CODE.DATA, "Tag Data Found for {}", dataRaw.getKey().toString());
			List<ResourceLocation> tags = new ArrayList<>();
			for (String str : dataValue.tagValues()) {
				MsLoggy.INFO.log(LOG_CODE.DATA, "Applying Setting to Tag: {}", str);
				if (str.startsWith("#")) {
					lookup.lookupOrThrow(registry).get(TagKey.create(registry, ResourceLocation.parse(str.substring(1))))
					.ifPresent(holder -> tags.addAll(holder.stream()
							.map(h -> h.unwrapKey().get().location())
							.collect(Collectors.toSet())));
				}
				else if (str.endsWith(":*")) {
					tags.addAll(lookup.lookupOrThrow(registry).listTagIds()
							.filter(key -> key.location().getNamespace().equals(str.replace(":*", "")))
							.map(TagKey::location)
							.toList());
				}
				else
					tags.add(ResourceLocation.parse(str));
			}
			dataValue.tagValues().clear();
			tags.forEach(rl -> this.data.merge(rl, dataValue, (o, n) -> (MainSystemConfig)o.combine(n)));
		}
		//Execute post-processing behavior (mostly logging at this point).
		finalizer.accept(this.data);
	}

	/**
	 * This should be called at most once, during construction of your mod (static init of your main mod class is fine)
	 * (FMLCommonSetupEvent *may* work as well)
	 * Calling this method automatically subscribes a packet-sender to {@link OnDatapackSyncEvent}.
	 * @param packetFactory  A packet constructor or factory method that converts the given map to a packet object to send on the given channel
	 * @return this manager object
	 */
	public MergeableCodecDataManager<V> subscribeAsSyncable(
		final Function<Map<ResourceLocation, MainSystemConfig>, CustomPacketPayload> packetFactory)
	{
		NeoForge.EVENT_BUS.addListener(this.getDatapackSyncListener(packetFactory));
		return this;
	}
	
	/** Generate an event listener function for the on-datapack-sync event **/
	private Consumer<OnDatapackSyncEvent> getDatapackSyncListener(
		final Function<Map<ResourceLocation, MainSystemConfig>, CustomPacketPayload> packetFactory)
	{
		return event -> {
			ServerPlayer player = event.getPlayer();
			List<CustomPacketPayload> packets = new ArrayList<>();
			for (Map.Entry<ResourceLocation, MainSystemConfig> entry : new HashMap<>(this.data).entrySet()) {
				if (entry.getKey() == null) continue;
				packets.add(packetFactory.apply(Map.of(entry.getKey(), entry.getValue())));
			}
			if (player == null)
				packets.forEach(PacketDistributor::sendToAllPlayers);
			else
				packets.forEach(p -> PacketDistributor.sendToPlayer(player, p));
		};
	}
}
