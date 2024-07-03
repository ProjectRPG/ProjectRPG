package rpg.project.lib.internal.setup;

import java.util.function.Supplier;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerAboutToStartEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.builtins.Abilities;
import rpg.project.lib.builtins.vanilla.VanillaAbilityConfigType;
import rpg.project.lib.builtins.vanilla.VanillaAbilitySystem;
import rpg.project.lib.builtins.vanilla.VanillaPartyConfigType;
import rpg.project.lib.builtins.vanilla.VanillaPartySystem;
import rpg.project.lib.builtins.vanilla.VanillaProgressionConfigType;
import rpg.project.lib.builtins.vanilla.VanillaProgressionDataType;
import rpg.project.lib.builtins.vanilla.VanillaProgressionSystem;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.commands.CmdRoot;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider.Locale;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID, bus= EventBusSubscriber.Bus.GAME)
public class CommonSetup {
	public static Supplier<PartySystem> partySupplier = () -> {
		SubSystemCodecRegistry.registerSubSystem(VanillaPartyConfigType.ID, VanillaPartyConfigType.IMPL);
		return new VanillaPartySystem();
	};
	public static Supplier<ProgressionSystem<?>> progressionSupplier = () -> {
		SubSystemCodecRegistry.registerSubSystem(VanillaProgressionConfigType.ID, VanillaProgressionConfigType.IMPL);
		SubSystemCodecRegistry.registerSubSystem(VanillaProgressionDataType.ID, VanillaProgressionDataType.IMPL);
		return new VanillaProgressionSystem();
	};
	public static Supplier<AbilitySystem> abilitySupplier = () -> {
		SubSystemCodecRegistry.registerSubSystem(VanillaAbilityConfigType.ID, VanillaAbilityConfigType.IMPL);
		return new VanillaAbilitySystem();
	};

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
		Abilities.init();
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void tickPerks(LevelTickEvent.Pre event) {
        Core.get(event.getLevel()).getAbilities().executeAbilityTicks(event);
    }
	
	@SubscribeEvent
	public static void onServerAboutToStart(ServerStartedEvent event) {
		event.getServer().registryAccess().registryOrThrow(EventRegistry.EVENTS.getRegistryKey()).stream().forEach(EventRegistry::registerListener);
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
