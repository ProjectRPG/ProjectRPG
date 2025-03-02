package rpg.project.lib.internal.config.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.ObjectType;
import rpg.project.lib.api.gating.GateUtils;
import rpg.project.lib.internal.config.readers.MainSystemConfig;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DatapackGenerator {
    public static final String PACKNAME = "generated_pack";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static boolean
            applyOverride = false,
            applyDefaults = false,
            applyDisabler = false,
            applySimple = false,
            applyObjects = true,
            applyConfigs = false;
    public static List<String> namespaceFilter = new ArrayList<>();

    private enum Category {
        ITEM(ObjectType.ITEM, server -> server.registryAccess().lookupOrThrow(Registries.ITEM).keySet()),
        BLOCK(ObjectType.BLOCK, server -> server.registryAccess().lookupOrThrow(Registries.BLOCK).keySet()),
        ENTITY(ObjectType.ENTITY, server -> server.registryAccess().lookupOrThrow(Registries.ENTITY_TYPE).keySet()),
        ENCHANT(ObjectType.ENCHANTMENT, server -> server.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).keySet()),
        DIMENSION(ObjectType.DIMENSION, server -> server.registryAccess().lookupOrThrow(Registries.DIMENSION_TYPE).keySet()),
        BIOME(ObjectType.BIOME, server -> server.registryAccess().lookupOrThrow(Registries.BIOME).keySet()),
        EFFECT(ObjectType.EFFECT, server -> server.registryAccess().lookupOrThrow(Registries.MOB_EFFECT).keySet()),
        EVENT(ObjectType.EVENT, server -> server.registryAccess().lookupOrThrow(APIUtils.GAMEPLAY_EVENTS).keySet());

        public ObjectType type;
        public Function<MinecraftServer, Set<ResourceLocation>> provider;
        Category(ObjectType type, Function<MinecraftServer, Set<ResourceLocation>> provider) {
            this.type = type;
            this.provider = provider;
        }
    }

    public static String getDefaultConfig(RegistryAccess access) {
        return gson.toJson(MainSystemConfig.CODEC.encodeStart(JsonOps.INSTANCE, new MainSystemConfig(false, List.of(),
                Arrays.stream(GateUtils.Type.values()).collect(Collectors.toMap(t -> t, type -> SubSystemCodecRegistry.getDefaults(APIUtils.SystemType.GATE, access))),
                SubSystemCodecRegistry.getDefaults(APIUtils.SystemType.PROGRESSION, access),
                SubSystemCodecRegistry.getDefaults(APIUtils.SystemType.ABILITY, access),
                SubSystemCodecRegistry.getDefaults(APIUtils.SystemType.FEATURE, access)
        )).result().orElse(gson.toJsonTree("{}")));
    }

    public static int generatePack(MinecraftServer server) {
        String defaultValue = getDefaultConfig(server.registryAccess());
        //create the filepath for our datapack.  this will do nothing if already created
        Path filepath = server.getWorldPath(LevelResource.DATAPACK_DIR).resolve(PACKNAME);
        filepath.toFile().mkdirs();
        /* checks for existence of the pack.mcmeta.  This will:
         * 1. create a new file if not present, using the disabler setting
         * 2. overwrite the existing file if the disabler setting conflicts*/
        Path packPath = filepath.resolve("pack.mcmeta");
        try {
            Files.writeString(
                    packPath,
                    gson.toJson(getPackObject(applyDisabler)),
                    Charset.defaultCharset(),
                    StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);

        } catch (IOException e) {System.out.println("Error While Generating pack.mcmeta for Generated Data: "+e.toString());}

        for (Category category : Category.values()) {
//            if (category.equals(Category.CONFIGS) && !applyConfigs)
//                continue;
//            if ((!category.equals(Category.CONFIGS) && !category.equals(Category.TAGS) && !applyObjects))
//                continue;
            Collection<ResourceLocation> filteredList = namespaceFilter.isEmpty() //|| category == Category.TAGS
                    ? category.provider.apply(server)
                    : category.provider.apply(server).stream().filter(id -> namespaceFilter.contains(id.getNamespace())).toList();
            for (ResourceLocation id : filteredList) {
                int index = id.getPath().lastIndexOf('/');
                String pathRoute = id.getPath().substring(0, Math.max(index, 0));
                Path finalPath = filepath.resolve("data/"+id.getNamespace()+"/"+category.type.getPath()+"/"+pathRoute);
                finalPath.toFile().mkdirs();
                try {
                    Files.writeString(
                            finalPath.resolve(id.getPath().substring(id.getPath().lastIndexOf('/')+1)+".json"),
                            defaultValue,
                            Charset.defaultCharset(),
                            StandardOpenOption.CREATE_NEW,
                            StandardOpenOption.WRITE);
                } catch (IOException e) {System.out.println("Error While Generating Pack File For: "+id.toString()+" ("+e.toString()+")");}
            }
        }
        return 0;
    }

    private static record Pack(String description, int format) {
        public static final Codec<Pack> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("description").forGetter(Pack::description),
                Codec.INT.fieldOf("pack_format").forGetter(Pack::format)
        ).apply(instance, Pack::new));
    }
    private static record BlockFilter(Optional<String> namespace, Optional<String> path) {
        public static final Codec<BlockFilter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.optionalFieldOf("namespace").forGetter(BlockFilter::namespace),
                Codec.STRING.optionalFieldOf("path").forGetter(BlockFilter::path)
        ).apply(instance, BlockFilter::new));
    }
    private static record Filter(List<BlockFilter> block) {
        public static final Codec<Filter> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BlockFilter.CODEC.listOf().fieldOf("block").forGetter(Filter::block)
        ).apply(instance, Filter::new));
    }
    private static record McMeta(Pack pack, Optional<Filter> filter) {
        public static final Codec<McMeta> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Pack.CODEC.fieldOf("pack").forGetter(McMeta::pack),
                Filter.CODEC.optionalFieldOf("filter").forGetter(McMeta::filter)
        ).apply(instance, McMeta::new));
    }
    private static final Filter defaultFilter = new Filter(List.of(new BlockFilter(Optional.empty(), Optional.of("pmmo"))));
    private static JsonElement getPackObject(boolean isDisabler) {
        McMeta pack = new McMeta(
                new Pack(isDisabler
                        ? "Generated Resources including a disabler filter for PMMO's defaults"
                        : "Generated Resources",
                        9),
                isDisabler
                        ? Optional.of(defaultFilter)
                        : Optional.empty());

        return McMeta.CODEC.encodeStart(JsonOps.INSTANCE, pack).result().get();
    }
}
