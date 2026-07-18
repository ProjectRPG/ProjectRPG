package rpg.project.lib.builtins.vanilla;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.api.progression.ProgressionAddon;
import rpg.project.lib.api.progression.ProgressionUtils;

public class VanillaCodecs {
    private static final DeferredRegister<SubSystemConfigType> CODECS = DeferredRegister.create(APIUtils.SUBSYSTEM_CODECS, "minecraft");
    private static final DeferredRegister<ProgressionAddon> PROGRESSION_ADDONS = DeferredRegister.create(ProgressionUtils.PROGRESSION_ADDON, "minecraft");

    public static void init(IEventBus bus, ModContainer container) {
        CODECS.register(VanillaAbilityConfigType.ID.getPath(), VanillaAbilityConfigType::new);
        CODECS.register(VanillaBonusConfigType.ID.getPath(), VanillaAbilityConfigType::new);
        CODECS.register(VanillaPartyConfigType.ID.getPath(), VanillaPartyConfigType::new);
        CODECS.register(VanillaProgressionConfigType.ID.getPath(), VanillaProgressionConfigType::new);
        CODECS.register(VanillaProgressionDataType.ID.getPath(), VanillaProgressionDataType::new);
        CODECS.register(bus);

        PROGRESSION_ADDONS.register(VanillaBonusConfigType.ID.getPath(), VanillaBonusesAddon::new);
        PROGRESSION_ADDONS.register(bus);

        container.getEventBus().addListener(RegisterEvent.class, event -> GateUtils.registerGateSystem(event, VanillaGateConfigType.IMPL, VanillaGates::new));
    }
}
