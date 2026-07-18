package rpg.project.lib.builtins.vanilla;

import net.minecraft.resources.Identifier;
import rpg.project.lib.api.Hub;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.api.gating.GateSystem;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.api.progression.ProgressionDataType;
import rpg.project.lib.api.progression.ProgressionDataType.Comparison;
import rpg.project.lib.builtins.vanilla.VanillaGateConfigType.VanillaGateConfig;
import rpg.project.lib.internal.registry.GateRegistry;

import java.util.EnumSet;
import java.util.Map;
import java.util.Optional;

public class VanillaGates implements GateSystem {
    @Override
    public float isActionPermitted(GateUtils.Type gateType, EventContext context, Hub core, Identifier event, String reference) {
        Optional<SubSystemConfig> gateData = core.getGateData(
                VanillaGateConfigType.IMPL,
                gateType == GateUtils.Type.EVENT ? ObjectType.EVENT : context.getSubjectType(),
                gateType,
                gateType == GateUtils.Type.EVENT ? event : context.getSubjectID());
        return gateData.map(gate -> {
            VanillaGateConfig config = (VanillaGateConfig)gate;
            float permission = 1f;
            for (Map.Entry<String, ProgressionDataType> gates : config.containerReference().entrySet()) {
                ProgressionDataType progress = core.getProgression().getProgress(context.getActor().getUUID(), gates.getKey());
                float diff = progress.compare(Comparison.GREATER_THAN_OR_EQUAL, gates.getValue());
                permission = Math.min(diff, permission);
            }
            return config.strict() && permission < GateRegistry.HARD_PASS ? GateRegistry.HARD_FAIL : permission;
        }).orElse(GateRegistry.HARD_PASS);
    }

    @Override
    public EnumSet<GateUtils.Type> applicableGateTypes() {
        return EnumSet.allOf(GateUtils.Type.class);
    }
}
