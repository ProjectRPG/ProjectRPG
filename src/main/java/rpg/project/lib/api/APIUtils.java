package rpg.project.lib.api;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.data.NodeConsumer;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.data.TargetSelector;
import rpg.project.lib.api.enums.RegistrationSide;
import rpg.project.lib.api.events.EventProvider;
import rpg.project.lib.api.events.conditions.EventConditionType;
import rpg.project.lib.api.feature.Feature;
import rpg.project.lib.api.gating.GateSystem;
import rpg.project.lib.api.party.PartySystem;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

import java.util.function.Supplier;

public class APIUtils {
	/**SystemType is used by {@link SubSystemConfigType} to specify which systems a
	 * given config type are used for. */
	public enum SystemType{
		ABILITY,
		FEATURE,
		GATE,
		PARTY,
		PROGRESSION,
		PROGRESSION_DATA
	}
	/**{@link ResourceKey} for use with a {@link net.neoforged.neoforge.registries.DeferredRegister DeferredRegister}
	 * to register custom events.  Custom events can be referenced by other addons' configurations to apply their
	 * system implementation behavior to your event specification.*/
	public static final ResourceKey<Registry<EventProvider<?>>> GAMEPLAY_EVENTS = ResourceKey.createRegistryKey(Reference.resource("gameplay_events"));
	public static final ResourceKey<Registry<EventConditionType>> EVENT_CONDITIONS = ResourceKey.createRegistryKey(Reference.resource("event_conditions"));
	/**<p>{@link ResourceKey} for use with a {@link DeferredRegister} to register data parsers for all systems within
	 * Project RPG.  Registry IDs for {@link SubSystemConfigType}s must be identical to their registered systems.
	 * It is strongly recommended that IDs be descriptive and unique to avoid overlap with other addons.</p>
	 *
	 * <p></p>You do not need to register a config type if your addon does not have configurable components or if your
	 * configurations are handled separately from the datapack and scripting systems.</p>*/
	public static final ResourceKey<Registry<SubSystemConfigType>> SUBSYSTEM_CODECS = ResourceKey.createRegistryKey(Reference.resource("codecs"));
	public static final ResourceKey<Registry<Feature>> FEATURE = ResourceKey.createRegistryKey(Reference.resource("feature"));
	public static final ResourceKey<Registry<Ability>> ABILITY = ResourceKey.createRegistryKey(Reference.resource("ability"));
	public static final ResourceKey<Registry<NodeConsumer>> FUNCTION = ResourceKey.createRegistryKey(Reference.resource("function"));
	public static final ResourceKey<Registry<TargetSelector>> TARGETOR = ResourceKey.createRegistryKey(Reference.resource("targetor"));


//	public static final DeferredRegister<NodeConsumer> FUNCTIONS = DeferredRegister.create(FUNCTION, Reference.MODID);
//	public static final DeferredRegister<TargetSelector> TARGETORS = DeferredRegister.create(TARGETOR, Reference.MODID);

	/**Calls the subsystem codec registry and obtains a dispatch codec
	 * which discriminates codec types based on their registry IDs and
	 * the key "type".
	 *
	 * @return the ecosystem's codec registry dispatch codec
	 */
	public static Codec<SubSystemConfigType> getDispatchCodec() {
		return SubSystemCodecRegistry.CODEC;
	}

	/**<p>Sets the party system to be used by the entire ecosystem.</p>
	 * <p>Only one party system can be registered and the ultimate
	 * system will be determined by the last registered system at
	 * the time the system is loaded by the library.</p>
	 *
	 * @param system a supplier, used to get a new instance of the
	 * party system.  This supplier is invoked during the server load
	 * stage of the lifecycle and assumptions regarding that are safe
	 * for instantiating your party system.
	 */
	public static void registerPartySystem(Supplier<PartySystem> system) {
		CommonSetup.partySupplier = system;
	}
}
