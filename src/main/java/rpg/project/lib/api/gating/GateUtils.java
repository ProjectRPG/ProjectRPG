package rpg.project.lib.api.gating;

import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.GateRegistry.Type;

public class GateUtils {
	//TODO JavaDocs
	public static void registerEventGate(GateSystem system) {
		GateRegistry.register(system, Type.EVENT);
	}
	public static void registerProgressGate(GateSystem system) {
		GateRegistry.register(system, Type.PROGRESS);
	}
	public static void registerFeatureGate(GateSystem system) {
		GateRegistry.register(system, Type.FEATURE);
	}
	public static void registerAbilityGate(GateSystem system) {
		GateRegistry.register(system, Type.ABILITY);
	}
}
