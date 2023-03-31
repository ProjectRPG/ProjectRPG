package rpg.project.lib.api.gating;

import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.internal.registry.GateRegistry;
import rpg.project.lib.internal.registry.GateRegistry.Type;

public class GateUtils {
	//TODO JavaDocs
	public static void registerEventGate(GateSystem<?> system) {
		GateRegistry.register(system, Type.EVENT);
	}
	public static void registerProgressGate(GateSystem<String> system) {
		GateRegistry.register(system, Type.PROGRESS);
	}
	//TODO replace <Object> with feature interface when created
	public static void registerFeatureGate(GateSystem<Object> system) {
		GateRegistry.register(system, Type.FEATURE);
	}
	public static void registerAbilityGate(GateSystem<Ability> system) {
		GateRegistry.register(system, Type.ABILITY);
	}
}
