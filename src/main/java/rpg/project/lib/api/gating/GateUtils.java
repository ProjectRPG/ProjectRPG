package rpg.project.lib.api.gating;

import net.minecraft.resources.ResourceLocation;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.GateRegistry.Type;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;

public class GateUtils {
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
