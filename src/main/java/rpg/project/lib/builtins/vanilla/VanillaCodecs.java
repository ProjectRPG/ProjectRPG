package rpg.project.lib.builtins.vanilla;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.SubSystemConfigType;

public class VanillaCodecs {
    private static final DeferredRegister<SubSystemConfigType> CODECS = DeferredRegister.create(APIUtils.SUBSYSTEM_CODECS, "minecraft");
    public static void init(IEventBus bus) {
        CODECS.register(VanillaAbilityConfigType.ID.getPath(), VanillaAbilityConfigType::new);
        CODECS.register(VanillaBonusConfigType.ID.getPath(), VanillaAbilityConfigType::new);
        CODECS.register(VanillaPartyConfigType.ID.getPath(), VanillaPartyConfigType::new);
        CODECS.register(VanillaProgressionConfigType.ID.getPath(), VanillaProgressionConfigType::new);
        CODECS.register(VanillaProgressionDataType.ID.getPath(), VanillaProgressionDataType::new);
        CODECS.register(bus);
    }
}
