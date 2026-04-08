package rpg.project.lib.internal.registry;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.client.EventConditionGlossaryPanel;
import rpg.project.lib.api.client.SubSystemGlossaryPanel;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.api.events.conditions.EventConditionType;
import rpg.project.lib.api.gating.GateUtils;

import java.util.HashMap;
import java.util.Map;

public class ClientPanelRegistry {
    private static final Map<GateUtils.Type, Map<Identifier, SubSystemGlossaryPanel>> GATE_PANELS = new HashMap<>();
    private static final Map<Identifier, SubSystemGlossaryPanel> PROGRESSION_PANELS = new HashMap<>();
    private static final Map<Identifier, SubSystemGlossaryPanel> ABILITY_PANELS = new HashMap<>();
    private static final Map<Identifier, SubSystemGlossaryPanel> FEATURE_PANELS = new HashMap<>();
    private static final Map<Identifier, EventConditionGlossaryPanel> CONDITION_PANELS = new HashMap<>();

    public static void registerGatePanel(Identifier systemID, GateUtils.Type type, SubSystemGlossaryPanel panel) {
        GATE_PANELS.computeIfAbsent(type, t -> new HashMap<>()).put(systemID, panel);
    }
    public static SubSystemGlossaryPanel getGatePanel(Identifier systemID, GateUtils.Type type) {
    return GATE_PANELS.getOrDefault(type, new HashMap<>()).get(systemID);
    }

    public static void registerProgressionPanel(Identifier systemID, SubSystemGlossaryPanel panel) {
        PROGRESSION_PANELS.put(systemID, panel);
    }
    public static SubSystemGlossaryPanel getProgressionPanel(Identifier systemID) {
        return PROGRESSION_PANELS.get(systemID);
    }

    public static void registerAbilityPanel(Identifier systemID, SubSystemGlossaryPanel panel) {
        ABILITY_PANELS.put(systemID, panel);
    }
    public static SubSystemGlossaryPanel getAbilityPanel(Identifier systemID) {
        return ABILITY_PANELS.get(systemID);
    }

    public static void registerFeaturePanel(Identifier systemID, SubSystemGlossaryPanel panel) {
        FEATURE_PANELS.put(systemID, panel);
    }
    public static SubSystemGlossaryPanel getFeaturePanel(Identifier systemID) {
        return FEATURE_PANELS.get(systemID);
    }

    public static void registerConditionPanel(Identifier conditionID, EventConditionGlossaryPanel panelFactory) {
        CONDITION_PANELS.put(conditionID, panelFactory);
    }
    public static EventConditionGlossaryPanel getConditionPanel(RegistryAccess reg, EventCondition condition) {
        Identifier conditionID = reg.lookupOrThrow(APIUtils.EVENT_CONDITIONS).getKey(condition.getType());
        return CONDITION_PANELS.get(conditionID);
    }
}
