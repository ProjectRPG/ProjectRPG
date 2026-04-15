package rpg.project.lib.internal.registry;

import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.abilities.AbilityPanelProvider;
import rpg.project.lib.api.client.EventConditionGlossaryPanel;
import rpg.project.lib.api.client.SubSystemGlossaryPanel;
import rpg.project.lib.api.events.conditions.EventCondition;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.internal.client.glossary.DefaultAbilityPanel;

import java.util.HashMap;
import java.util.Map;

public class ClientPanelRegistry {
    private static final Map<GateUtils.Type, Map<Identifier, SubSystemGlossaryPanel>> GATE_PANELS = new HashMap<>();
    private static final Map<Identifier, SubSystemGlossaryPanel> PROGRESSION_PANELS = new HashMap<>();
    private static final Map<Identifier, SubSystemGlossaryPanel> ABILITY_SYSTEM_PANELS = new HashMap<>();
    private static final Map<Identifier, AbilityPanelProvider> ABILITY_PANELS = new HashMap<>();
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
        ABILITY_SYSTEM_PANELS.put(systemID, panel);
    }
    public static SubSystemGlossaryPanel getAbilitySystemPanel(Identifier systemID) {
        return ABILITY_SYSTEM_PANELS.get(systemID);
    }
    public static void registerAbilityPanel(Identifier abilityID, AbilityPanelProvider panelProvider) {
        ABILITY_PANELS.put(abilityID, panelProvider);
    }
    public static AbilityPanelProvider getAbilityPanel(Identifier abilityID) {
        return ABILITY_PANELS.getOrDefault(abilityID, DefaultAbilityPanel::new);
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
