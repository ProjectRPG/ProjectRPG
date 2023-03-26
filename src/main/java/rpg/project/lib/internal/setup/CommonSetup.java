package rpg.project.lib.internal.setup;

import java.util.function.Supplier;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryManager;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.builtins.vanilla.VanillaPartySystem;
import rpg.project.lib.internal.commands.CmdRoot;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider.Locale;
import rpg.project.lib.internal.util.Reference;

@Mod.EventBusSubscriber(modid=Reference.MODID, bus=Mod.EventBusSubscriber.Bus.FORGE)
public class CommonSetup {
	public static Supplier<PartySystem> partySupplier = () -> new VanillaPartySystem();

	/**Registered to MOD BUS in mod constructor*/
	public static void gatherData(GatherDataEvent event) {
		DataGenerator generator = event.getGenerator();
		if (event.includeClient()) {
			for (Locale locale : LangProvider.Locale.values()) {
				generator.addProvider(true, new LangProvider(generator.getPackOutput(), locale.str));
			}
		}
	}
	
	@SubscribeEvent
	public static void onServerAboutToStart(ServerAboutToStartEvent event) {
		for (EventListenerSpecification<?> els : RegistryManager.ACTIVE.getRegistry(APIUtils.GAMEPLAY_EVENTS).getValues()) {
			els.registerListener();
		}
	}
	
	@SubscribeEvent
	public static void registerCommands(RegisterCommandsEvent event) {
		CmdRoot.register(event.getDispatcher());
	}
}
