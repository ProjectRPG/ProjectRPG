package rpg.project.lib.api;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.enums.RegistrationSide;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.internal.Core;
//import rpg.project.lib.internal.config.scripting.NodeConsumer;
//import rpg.project.lib.internal.config.scripting.TargetSelector;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

import java.util.function.Supplier;

public class APIUtils {
	/**{@link ResourceKey} for use with a {@link net.neoforged.neoforge.registries.DeferredRegister DeferredRegister}
	 * to register custom events.  Custom events can be referenced by other addons' configurations to apply their
	 * system implementation behavior to your event specification.*/
	public static final ResourceKey<Registry<EventProvider<?>>> GAMEPLAY_EVENTS = ResourceKey.createRegistryKey(Reference.resource("gameplay_events"));
	public static final ResourceKey<Registry<Feature>> FEATURE = ResourceKey.createRegistryKey(Reference.resource("feature"));
	public static final ResourceKey<Registry<Ability>> ABILITY = ResourceKey.createRegistryKey(Reference.resource("ability"));
//	public static final ResourceKey<Registry<NodeConsumer>> FUNCTION = ResourceKey.createRegistryKey(Reference.resource("function"));
//	public static final ResourceKey<Registry<TargetSelector>> TARGETOR = ResourceKey.createRegistryKey(Reference.resource("targetor"));

	public static final DeferredRegister<Feature> FEATURES = DeferredRegister.create(FEATURE, Reference.MODID);
	public static final DeferredRegister<Ability> ABILITIES = DeferredRegister.create(ABILITY, Reference.MODID);
//	public static final DeferredRegister<NodeConsumer> FUNCTIONS = DeferredRegister.create(FUNCTION, Reference.MODID);
//	public static final DeferredRegister<TargetSelector> TARGETORS = DeferredRegister.create(TARGETOR, Reference.MODID);

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

	/**Registers a feature's config specification for configurations to use.
	 *
	 * To register the feature itself, use a deferred register and the registry key
	 * of {@link APIUtils#FEATURE}.
	 *
	 * @param featureId the ID of the feature specification being registered
	 * @param config the config type used to read this feature from data
	 */
	public static void registerFeature(ResourceLocation featureId, SubSystemConfigType config) {
		SubSystemCodecRegistry.registerSubSystem(featureId, config, SubSystemCodecRegistry.SystemType.FEATURE);
	}
}
