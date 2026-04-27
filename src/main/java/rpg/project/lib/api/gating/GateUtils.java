package rpg.project.lib.api.gating;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.util.StringRepresentable;
import net.neoforged.neoforge.registries.RegisterEvent;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.datagen.LangProvider;
import rpg.project.lib.internal.util.Reference;

public class GateUtils {
	public static final ResourceKey<Registry<GateSystem>> GATES_EVENTS = ResourceKey.createRegistryKey(Reference.resource("gates_events"));
	public static final ResourceKey<Registry<GateSystem>> GATES_PROGRESS = ResourceKey.createRegistryKey(Reference.resource("gates_progress"));
	public static final ResourceKey<Registry<GateSystem>> GATES_FEATURES = ResourceKey.createRegistryKey(Reference.resource("gates_features"));
	public static final ResourceKey<Registry<GateSystem>> GATES_ABILITIES = ResourceKey.createRegistryKey(Reference.resource("gates_abilities"));

	/**Gameplay gating is divided into four types.  {@link GateSystem}
	 * registrations specify their applicable {@link Type} so the
	 * internal gating processor can apply them accordingly.  
	 */
	public enum Type implements StringRepresentable {
		/**Specifies gates that cancel or alter events.*/
		EVENT(GATES_EVENTS, LangProvider.GATE_TYPE_EVENT.asComponent()),
		/**Specifies gates that permit/deny progression advancement.*/
		PROGRESS(GATES_PROGRESS, LangProvider.GATE_TYPE_PROGRESSION.asComponent()),
		/**Specifies gates that permit/deny feature usage*/
		FEATURE(GATES_FEATURES, LangProvider.GATE_TYPE_FEATURE.asComponent()),
		/**Specifies gates that permit/deny ability usage*/
		ABILITY(GATES_ABILITIES, LangProvider.GATE_TYPE_ABILITY.asComponent());
		public ResourceKey<Registry<GateSystem>> key;
		public Component translation;
		Type(ResourceKey<Registry<GateSystem>> registryKey, Component translation) {
			this.key = registryKey;
			this.translation = translation;
		}
		
		public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
		private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getSerializedName, s -> s));
		public static Type create(String name) {return BY_NAME.get(name);}
		
		@Override
		public String getSerializedName() {return this.name();}
	}

	/**<p>Called during the {@link RegisterEvent}, registers a gating system
	 * for the specified gate type(s).</p>
	 * <p>Note the same config and config ID can be used for multiple
	 * systems of different types.  Uniqueness is not required</p>
	 *
	 * @param config a config type for this system
	 * @param system the system instance itself
	 */
	public static void registerGateSystem(RegisterEvent event, SubSystemConfigType config, Supplier<GateSystem> system) {
		event.register(APIUtils.SUBSYSTEM_CODECS, config.getId(), () -> config);
		system.get().applicableGateTypes().forEach(type -> {
			event.register(type.key, config.getId(), system);
		});
	}
}
