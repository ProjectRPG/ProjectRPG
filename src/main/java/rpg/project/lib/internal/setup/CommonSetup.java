package rpg.project.lib.internal.setup;

import java.util.function.Supplier;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.LevelTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilitySystem;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.api.gating.GateSystem;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionSystem;
import rpg.project.lib.api.progression.ProgressionUtils;
import rpg.project.lib.builtins.vanilla.VanillaAbilitySystem;
import rpg.project.lib.builtins.vanilla.VanillaPartySystem;
import rpg.project.lib.builtins.vanilla.VanillaProgressionSystem;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.commands.CmdRoot;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.config.readers.ExecutableListener;
import rpg.project.lib.internal.network.Networking;
import rpg.project.lib.internal.registry.EventRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider.Locale;
import rpg.project.lib.internal.util.Reference;

@EventBusSubscriber(modid=Reference.MODID)
public class CommonSetup {
	public static final DeferredRegister<SubSystemConfigType> CODECS = DeferredRegister.create(APIUtils.SUBSYSTEM_CODECS, Reference.MODID);
	public static final DeferredRegister<Feature> FEATURES = DeferredRegister.create(APIUtils.FEATURE, Reference.MODID);
	public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(APIUtils.ABILITY, Reference.MODID);
	public static final DeferredRegister<GateSystem> GATES_EVENTS = DeferredRegister.create(GateUtils.GATES_EVENTS, Reference.MODID);
	public static final DeferredRegister<GateSystem> GATES_PROGRESS = DeferredRegister.create(GateUtils.GATES_PROGRESS, Reference.MODID);
	public static final DeferredRegister<GateSystem> GATES_FEATURES = DeferredRegister.create(GateUtils.GATES_FEATURES, Reference.MODID);
	public static final DeferredRegister<GateSystem> GATES_ABILITIES = DeferredRegister.create(GateUtils.GATES_ABILITIES, Reference.MODID);
	public static final DeferredRegister<ProgressionAddon> PROGRESSION_ADDONS = DeferredRegister.create(ProgressionUtils.PROGRESSION_ADDON, Reference.MODID);

	public static Supplier<PartySystem> partySupplier = VanillaPartySystem::new;
	public static Supplier<ProgressionSystem<?>> progressionSupplier = VanillaProgressionSystem::new;
	public static Supplier<AbilitySystem> abilitySupplier = VanillaAbilitySystem::new;

	/**Registered to MOD BUS in mod constructor*/
	public static void gatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		PackOutput packOutput = generator.getPackOutput();

		for (Locale locale : LangProvider.Locale.values()) {
			generator.addProvider(true, new LangProvider(packOutput, locale));
		}
	}
	
	public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> EventRegistry.EVENTS.getRegistry().get().stream().forEach(EventRegistry::registerListener));
	}
	
	@SubscribeEvent(priority = EventPriority.HIGH)
    public static void tickPerks(LevelTickEvent.Pre event) {
        Core.get(event.getLevel()).executeAbilityTicks(event);
    }
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CmdRoot.register(event.getDispatcher());
	}
	
	@SubscribeEvent
	public static void onAddReloadListeners(AddServerReloadListenersEvent event) {
		Core.get(LogicalSide.SERVER).getLoader().RELOADER = new ExecutableListener(event.getRegistryAccess(), DataLoader.RELOADER_FUNCTION);
		event.addListener(Reference.resource("reloader"), Core.get(LogicalSide.SERVER).getLoader().RELOADER);
		Core.get(LogicalSide.SERVER).getLoader().all().forEach(listener -> event.addListener(Reference.resource(listener.getValue().folderName), listener.getValue()));
		Networking.registerDataSyncPackets();
	}
}
