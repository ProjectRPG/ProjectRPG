package rpg.project.lib.api;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.enums.RegistrationSide;
import rpg.project.lib.api.events.EventListenerSpecification;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.registry.FeatureRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

import java.util.function.Supplier;

public class APIUtils {
	/**{@link ResourceKey} for use with a {@link net.neoforged.neoforge.registries.DeferredRegister DeferredRegister}
	 * to register custom events.  Custom events can be referenced by other addons' configurations to apply their
	 * system implementation behavior to your event specification.*/
	public static final ResourceKey<Registry<EventProvider<?>>> GAMEPLAY_EVENTS = ResourceKey.createRegistryKey(Reference.resource("gameplay_events"));
	
	public static Codec<SubSystemConfigType> getDispatchCodec() {
		return SubSystemCodecRegistry.CODEC;
	}

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
			SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.PARTY);
			return system.get();
		};
	}

	/**Registers a feature for configurations to use.  {@link RegistrationSide} allows
	 * for registration of sided features.  For features that should be on both sides,
	 * but with different behavior, register each side's implementation separately.
	 *
	 * @param feature the feature specification being registered
	 * @param config the config type used to read this feature from data
	 * @param sides the side this feature should be configured on.
	 */
	public static void registerFeature(Feature feature, SubSystemConfigType config, RegistrationSide sides) {
		SubSystemCodecRegistry.registerSubSystem(feature.featureID(), config, SubSystemCodecRegistry.SystemType.FEATURE);
		if (sides == RegistrationSide.BOTH || sides == RegistrationSide.SERVER)
			Core.get(LogicalSide.SERVER).getFeatures().register(feature);
		if (sides == RegistrationSide.BOTH || sides == RegistrationSide.CLIENT)
			Core.get(LogicalSide.CLIENT).getFeatures().register(feature);
	}
}
