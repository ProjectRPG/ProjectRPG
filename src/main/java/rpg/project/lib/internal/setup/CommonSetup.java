package rpg.project.lib.internal.setup;

import java.util.function.Supplier;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.builtins.vanilla.VanillaPartySystem;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.setup.datagen.LangProvider.Locale;

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
}
