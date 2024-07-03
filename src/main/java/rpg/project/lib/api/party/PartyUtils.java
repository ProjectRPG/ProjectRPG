package rpg.project.lib.api.party;

import java.util.function.Supplier;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;

public class PartyUtils {

	/**<p>Sets the party system to be used by the entire ecosystem.</p>
	 * <p>Only one party system can be registered and the ultimate
	 * system will be determined by the last registered system at
	 * the time the system is loaded by the library.</p>
	 * 
	 * @param id a unique identifier used to create a party system
	 * config.  The namespace and path will be used to create the 
	 * config and will display like "projectrpg-namespace-path.toml"
	 * @param config the config type used to obtain the config codec
	 * used in generating the config file.
	 * @param system a supplier, used to get a new instance of the
	 * party system.  This supplier is invoked during the server load
	 * stage of the lifecycle and assumptions regarding that are safe
	 * for instantiating your party system.
	 */
	public static void registerPartySystem(ResourceLocation id, SubSystemConfigType config, Supplier<PartySystem> system) {
		CommonSetup.partySupplier = () -> {
			SubSystemCodecRegistry.registerSubSystem(id, config);
			return system.get();
		};
	}
}
