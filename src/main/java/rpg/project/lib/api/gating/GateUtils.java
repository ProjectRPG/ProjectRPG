package rpg.project.lib.api.gating;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import com.mojang.serialization.Codec;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraftforge.common.IExtensibleEnum;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;

public class GateUtils {
	/**Gameplay gating is divided into four types.  {@link GateSystem}
	 * registrations specify their applicable {@link Type} so the 
	 * internal gating processor can apply them accordingly.  
	 */
	public static enum Type implements StringRepresentable, IExtensibleEnum{
		/**Specifies gates that cancel or alter events.*/
		EVENT,
		/**Specifies gates that permit/deny progression advancement.*/
		PROGRESS,
		/**Specifies gates that permit/deny feature usage*/
		FEATURE,
		/**Specifies gates that permit/deny ability usage*/
		ABILITY;
		
		public static final Codec<Type> CODEC = IExtensibleEnum.createCodecForExtensibleEnum(Type::values, Type::create);
		private static final Map<String, Type> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(Type::getSerializedName, s -> s));
		public static Type create(String name) {return BY_NAME.get(name);} 
		
		@Override
		public String getSerializedName() {return this.name();}
	}
	
	//TODO JavaDocs
	public static void registerEventGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config);
		GateRegistry.register(system, Type.EVENT);
	}
	public static void registerProgressGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config);
		GateRegistry.register(system, Type.PROGRESS);
	}
	public static void registerFeatureGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config);
		GateRegistry.register(system, Type.FEATURE);
	}
	public static void registerAbilityGate(ResourceLocation id, SubSystemConfigType config, GateSystem system) {
		SubSystemCodecRegistry.registerSubSystem(id, config);
		GateRegistry.register(system, Type.ABILITY);
	}
}
