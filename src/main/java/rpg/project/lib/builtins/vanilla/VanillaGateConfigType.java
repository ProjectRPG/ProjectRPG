package rpg.project.lib.builtins.vanilla;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.Identifier;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.MergeableData;
import rpg.project.lib.api.data.SubSystemConfig;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.progression.ProgressionDataType;

import java.util.EnumSet;
import java.util.Map;
import java.util.stream.Collectors;

public record VanillaGateConfigType() implements SubSystemConfigType {
    public static final Identifier ID = Identifier.withDefaultNamespace("gates");
    public static final VanillaGateConfigType IMPL = new VanillaGateConfigType();

    @Override public Identifier getId() {return ID;}
    @Override public MapCodec<SubSystemConfig> getCodec() {return VanillaGateConfig.CODEC;}
    @Override public EnumSet<APIUtils.SystemType> applicableSystemTypes() {return EnumSet.of(APIUtils.SystemType.GATE);}
    @Override public SubSystemConfig getDefault(RegistryAccess access) {return new VanillaGateConfig(true, Map.of());}

    @Override
    public SubSystemConfig fromScript(Map<String, String> values) {
        return new VanillaGateConfig(true, Map.of()); //TODO implement scripting
    }

    public record VanillaGateConfig(
            boolean strict,
            Map<String, ProgressionDataType> containerReference
    ) implements SubSystemConfig {
        public static final MapCodec<SubSystemConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.BOOL.optionalFieldOf("strict", true).forGetter(ssc -> ((VanillaGateConfig)ssc).strict),
                Codec.unboundedMap(Codec.STRING, APIUtils.getDispatchCodec().dispatch("type", SubSystemConfig::getType, SubSystemConfigType::getCodec))
                        .fieldOf("requirements")
                        .forGetter(ssc -> ((VanillaGateConfig)ssc).containerReference.entrySet().stream()
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))) //yes, we mutate just to get class type changes
        ).apply(instance, (strict, map) -> new VanillaGateConfig(strict,
                map.entrySet().stream().filter(entry -> entry.getValue() instanceof ProgressionDataType)
                        .collect(Collectors.toMap(Map.Entry::getKey, entry -> (ProgressionDataType)entry.getValue())))));

        @Override public SubSystemConfigType getType() {return IMPL;}
        @Override public MapCodec<SubSystemConfig> getCodec() {return CODEC;}
        @Override public boolean isPriorityData() {return false;}
        @Override public boolean isUnconfigured() {return this.containerReference.isEmpty();}

        @Override
        public MergeableData combine(MergeableData two) {
            if (two instanceof VanillaGateConfig second) {
                var map = this.containerReference;
                map.putAll(second.containerReference);
                boolean isStrict = this.strict || second.strict;
                return new VanillaGateConfig(isStrict, map);
            }
            return this;
        }
    }
}
