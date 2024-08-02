package rpg.project.lib.api.gating;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;

public class GateUtils {
	/**Gameplay gating is divided into four types.  {@link GateSystem}
	 * registrations specify their applicable {@link Type} so the 
	 * internal gating processor can apply them accordingly.  
	 */
	public enum Type implements StringRepresentable {
		/**Specifies gates that cancel or alter events.*/
		EVENT,
		/**Specifies gates that permit/deny progression advancement.*/
		PROGRESS,
		/**Specifies gates that permit/deny feature usage*/
		FEATURE,
		/**Specifies gates that permit/deny ability usage*/
		ABILITY;
		
		public static final Codec<Type> CODEC = StringRepresentable.fromEnum(Type::values);
		private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getSerializedName, s -> s));
		public static Type create(String name) {return BY_NAME.get(name);} 
		
		@Override
		public String getSerializedName() {return this.name();}
	}
	
	/**<p>Registers a gating system specifically for gating events.</p>
	 * <p>Note the same config and config ID can be used for multiple
	 * systems of different types.  Uniqueness is not required</p>
	 * 
	 * @param id the "type" id for the config type
	 * @param config a config type for this system
	 * @param system the system instance itself
	 */
	public static void registerEventGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.GATE);
		GateRegistry.register(system, Type.EVENT);
	}
	
	/**<p>Registers a gating system specifically for gating progress.</p>
	 * <p>Note the same config and config ID can be used for multiple
	 * systems of different types.  Uniqueness is not required</p>
	 * 
	 * @param id the "type" id for the config type
	 * @param config a config type for this system
	 * @param system the system instance itself
	 */
	public static void registerProgressGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.PROGRESSION);
		GateRegistry.register(system, Type.PROGRESS);
	}

	/**<p>Registers a gating system specifically for gating features.</p>
	 * <p>Note the same config and config ID can be used for multiple
	 * systems of different types.  Uniqueness is not required</p>
	 * 
	 * @param id the "type" id for the config type
	 * @param config a config type for this system
	 * @param system the system instance itself
	 */
	public static void registerFeatureGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.FEATURE);
		GateRegistry.register(system, Type.FEATURE);
	}


	/**<p>Registers a gating system specifically for gating abilities.
	 * This can be redundant to unlocks of ability systems, so be aware
	 * your system might be better suited as an ability system depending
	 * on its function.</p>
	 * <p>Note the same config and config ID can be used for multiple
	 * systems of different types.  Uniqueness is not required</p>
	 * 
	 * @param id the "type" id for the config type
	 * @param config a config type for this system
	 * @param system the system instance itself
	 */
	public static void registerAbilityGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.ABILITY);
		GateRegistry.register(system, Type.ABILITY);
	}
}
