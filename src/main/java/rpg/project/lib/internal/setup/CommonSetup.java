package rpg.project.lib.internal.setup;

import java.util.function.Supplier;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryManager;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.builtins.vanilla.VanillaPartySystem;
import rpg.project.lib.builtins.vanilla.VanillaProgressionSystem;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.abilities.AbilityRegistration;
import rpg.project.lib.internal.commands.CmdRoot;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider.Locale;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid=Reference.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
	//TODO expose these via API to be overridden
	public static Supplier<PartySystem> partySupplier = () -> new VanillaPartySystem();
	public static Supplier<ProgressionSystem<?>> progressionSupplier = () -> new VanillaProgressionSystem();

	/**Registered to MOD BUS in mod constructor*/
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();
		
		if (event.includeClient()) {
			for (Locale locale : LangProvider.Locale.values()) {
				generator.addProvider(true, new LangProvider(packOutput, locale));
			}
		}
	}
	
	public static void init(final FMLCommonSetupEvent event) {
		//TODO this crashed on load.  
//		AbilityRegistration.init();
	}
	
	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		RegistryManager.ACTIVE.getRegistry(APIUtils.GAMEPLAY_EVENTS).getValues().forEach(els -> EventRegistry.registerListener(els));
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CmdRoot.register(event.getDispatcher());
	}
	
	@SubscribeEvent
	public static void onAddReloadListeners(AddReloadListenerEvent event) {
		event.addListener(DataLoader.RELOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().ITEM_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().BLOCK_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().ENTITY_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().BIOME_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().DIMENSION_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().PLAYER_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().ENCHANTMENT_LOADER);
		event.addListener(Core.get(LogicalSide.SERVER).getLoader().EFFECT_LOADER);
	}
}
