package rpg.project.lib.internal.config.scripting;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.fml.LogicalSide;
import rpg.project.lib.api.data.NodeConsumer;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.data.TargetSelector;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.config.readers.DataLoader;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.config.readers.MergeableCodecDataManager;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.Reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

public class Functions {
    //Map of keywords and their "registered" consumers
    public static final Map<String, NodeConsumer> KEYWORDS = new HashMap<>();
    public static final Map<String, TargetSelector> TARGETORS = new HashMap<>();
    public static final Pattern operationRegex = Pattern.compile("(>=|<=|>|<|=)\\s*([-+]?\\d*\\.?\\d+(?:[eE][-+]?\\d+)?)");
    public static final Pattern attributeRegex = Pattern.compile("(.*?)(\\*\\*|\\+|\\*)(.*)");

    private static void gateConsumer(String param, ResourceLocation id, ObjectType type, Map<String, String> value, GateUtils.Type gate) {
        SubSystemConfigType subsystem = CommonSetup.CODECS.getRegistry().get().getValue(ResourceLocation.parse(param));
        MergeableCodecDataManager<?> loader = Core.get(LogicalSide.SERVER).getLoader().getLoader(type);
        MainSystemConfig config = MainSystemConfig.getForScripts();
        config.gates().put(gate, List.of(subsystem.fromScript(value)));
        MainSystemConfig existing = loader.getData(id);
        loader.registerOverride(id, (MainSystemConfig) existing.combine(config));
    }

    static {

        KEYWORDS.put("eventgates", (param, id, type, value) -> gateConsumer(param, id, type, value, GateUtils.Type.EVENT));
        KEYWORDS.put("featuregates", (param, id, type, value) -> gateConsumer(param, id, type, value, GateUtils.Type.FEATURE));
        KEYWORDS.put("abilitygates", (param, id, type, value) -> gateConsumer(param, id, type, value, GateUtils.Type.ABILITY));
        KEYWORDS.put("progressiongates", (param, id, type, value) -> gateConsumer(param, id, type, value, GateUtils.Type.PROGRESS));
        KEYWORDS.put("progression", (param, id, type, value) -> {
            SubSystemConfigType subsystem = CommonSetup.CODECS.getRegistry().get().getValue(ResourceLocation.parse(param));
            MergeableCodecDataManager<?> loader = Core.get(LogicalSide.SERVER).getLoader().getLoader(type);
            MainSystemConfig config = MainSystemConfig.getForScripts();
            config.progression().add(subsystem.fromScript(value));
            MainSystemConfig existing = loader.getData(id);
            loader.registerOverride(id, (MainSystemConfig) existing.combine(config));
        });
        KEYWORDS.put("ability", (param, id, type, value) -> {
            SubSystemConfigType subsystem = CommonSetup.CODECS.getRegistry().get().getValue(ResourceLocation.parse(param));
            MergeableCodecDataManager<?> loader = Core.get(LogicalSide.SERVER).getLoader().getLoader(type);
            MainSystemConfig config = MainSystemConfig.getForScripts();
            config.abilities().add(subsystem.fromScript(value));
            MainSystemConfig existing = loader.getData(id);
            loader.registerOverride(id, (MainSystemConfig) existing.combine(config));
        });
        KEYWORDS.put("feature", (param, id, type, value) -> {
            SubSystemConfigType subsystem = CommonSetup.CODECS.getRegistry().get().getValue(ResourceLocation.parse(param));
            MergeableCodecDataManager<?> loader = Core.get(LogicalSide.SERVER).getLoader().getLoader(type);
            MainSystemConfig config = MainSystemConfig.getForScripts();
            config.features().add(subsystem.fromScript(value));
            MainSystemConfig existing = loader.getData(id);
            loader.registerOverride(id, (MainSystemConfig) existing.combine(config));
        });

        TARGETORS.put("food", (param, access) -> {
            String[] exprStr = param.split(",");
            //left equals nutrition, right equals saturation
            float nutVal = 0f;
            float satVal = 0f;
            Operator nutOp = Operator.GTE;
            Operator satOp = Operator.GTE;
            for (String str : exprStr) {
                var match = operationRegex.matcher(str);                ;
                if (!match.find()) continue;
                if (str.startsWith("nutrition")) {
                    nutOp = Operator.fromString(match.group(1));
                    nutVal = Float.valueOf(match.group(2));
                }
                else if (str.startsWith("saturation")) {
                    satOp = Operator.fromString(match.group(1));
                    satVal = Float.valueOf(match.group(2));
                }
            }
            final Pair<Float, Float> values = Pair.of(nutVal, satVal);
            final Pair<Operator, Operator> ops = Pair.of(nutOp, satOp);
            List<ResourceLocation> food = access.lookupOrThrow(Registries.ITEM).entrySet().stream()
                    .filter(entry -> entry.getValue().getDefaultInstance().get(DataComponents.FOOD) instanceof FoodProperties props
                            && ops.getFirst().evaluation.test(Integer.valueOf(props.nutrition()).floatValue(), values.getFirst())
                            && ops.getSecond().evaluation.test(props.saturation(), values.getSecond()))
                    .map(entry -> entry.getKey().location())
                    .toList();
            return new TargetSelector.Selection(ObjectType.ITEM, food);
        });
        TARGETORS.put("tool", (param, access) -> {
            List<ResourceLocation>  tools = new ArrayList<>();
            if (param.isEmpty())
                tools.addAll(access.lookupOrThrow(Registries.ITEM).entrySet().stream()
                        .filter(entry -> entry.getValue().components().has(DataComponents.TOOL))
                        .map(entry -> entry.getKey().location())
                        .toList());
            else {
                ResourceLocation tag = Reference.resource(param);
                tools.addAll(access.lookupOrThrow(Registries.ITEM).get(TagKey.create(Registries.ITEM, tag))
                        .map(named -> named.stream()
                                .filter(holder -> holder.value().components().has(DataComponents.TOOL))
                                .map(holder -> holder.getKey().location()).toList())
                        .orElse(List.of()));
            }
            return new TargetSelector.Selection(ObjectType.ITEM, tools);
        });
        TARGETORS.put("armor", (param, access) -> {
            List<ResourceLocation>  tools = new ArrayList<>();
            if (param.isEmpty())
                tools.addAll(access.lookupOrThrow(Registries.ITEM).entrySet().stream()
                        .filter(entry -> entry.getValue() instanceof ArmorItem)
                        .map(entry -> entry.getKey().location())
                        .toList());
            else {
                ResourceLocation tag = Reference.resource(param);
                tools.addAll(access.lookupOrThrow(Registries.ITEM).get(TagKey.create(Registries.ITEM, tag))
                        .map(named -> named.stream()
                                .filter(holder -> holder.value() instanceof ArmorItem)
                                .map(holder -> holder.getKey().location()).toList())
                        .orElse(List.of()));
            }
            return new TargetSelector.Selection(ObjectType.ITEM, tools);
        });
        TARGETORS.put("weapon", (param, access) -> {
            List<ResourceLocation>  tools = new ArrayList<>();
            if (param.isEmpty())
                tools.addAll(access.lookupOrThrow(Registries.ITEM).entrySet().stream()
                        .filter(entry -> entry.getValue().components().has(DataComponents.DAMAGE))
                        .map(entry -> entry.getKey().location())
                        .toList());
            else {
                ResourceLocation tag = Reference.resource(param);
                tools.addAll(access.lookupOrThrow(Registries.ITEM).get(TagKey.create(Registries.ITEM, tag))
                        .map(named -> named.stream()
                                .filter(holder -> holder.value().components().has(DataComponents.DAMAGE))
                                .map(holder -> holder.getKey().location()).toList())
                        .orElse(List.of()));
            }
            return new TargetSelector.Selection(ObjectType.ITEM, tools);
        });
    }

    private enum Operator {
        GT((one, two) -> one > two),
        LT((one, two) -> one < two),
        EQ(Float::equals),
        GTE((one, two) -> one >= two),
        LTE((one, two) -> one <= two);

        public final BiPredicate<Float, Float> evaluation;
        Operator(BiPredicate<Float, Float> evaluation) {
            this.evaluation = evaluation;
        }

        public static Operator fromString(String str) {
            return switch (str) {
                case ">" -> GT;
                case "<" -> LT;
                case "=" -> EQ;
                case ">=" -> GTE;
                case "<=" -> LTE;
                default -> null;
            };
        }
    }
//
//    public static Map<String, Long> mapValue(String value) {
//        Map<String, Long> outMap = new HashMap<>();
//        String[] elements = value.replaceAll("\\)", "").split(",");
//        for (int i = 0; i <= elements.length-2; i += 2) {
//            outMap.put(elements[i], Long.valueOf(elements[i+1]));
//        }
//        return outMap;
//    }
//
//    public static Map<String, Double> doubleMap(String value) {
//        Map<String, Double> outMap = new HashMap<>();
//        String[] elements = value.replaceAll("\\)", "").split(",");
//        for (int i = 0; i <= elements.length-2; i += 2) {
//            outMap.put(elements[i], Double.valueOf(elements[i+1]));
//        }
//        return outMap;
//    }

//    public static double getDouble(Map<String, String> values) {
//        return Double.parseDouble(values.getOrDefault("value", "0"));
//    }
//    public static ResourceLocation getId(Map<String, String> values) {
//        return Reference.resource(values.getOrDefault("value", "pmmo_scripting:missing_value"));
//    }
//    public static boolean getBool(Map<String, String> values) {
//        return Boolean.parseBoolean(values.getOrDefault("value", "false"));
//    }
//    public static float getFloat(Map<String, String> values) {
//        return Float.parseFloat(values.getOrDefault("value", "0"));
//    }
//    public static long getLong(Map<String, String> values) {
//        return Long.parseLong(values.getOrDefault("value", "0"));
//    }
//    public static int getInt(Map<String, String> values) {
//        return Integer.parseInt(values.getOrDefault("value", "0"));
//    }
}
