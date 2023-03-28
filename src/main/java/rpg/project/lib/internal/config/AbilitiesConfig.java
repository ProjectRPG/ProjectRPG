package rpg.project.lib.internal.config;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraftforge.common.ForgeConfigSpec;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.api.enums.EventType;
import rpg.project.lib.internal.config.readers.TomlConfigHelper;
import rpg.project.lib.internal.config.readers.TomlConfigHelper.ConfigObject;
import rpg.project.lib.internal.util.TagBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AbilitiesConfig {
    public static ForgeConfigSpec SERVER_CONFIG;
    private static final Codec<Map<EventType, List<CompoundTag>>> CODEC = Codec.unboundedMap(EventType.CODEC, CompoundTag.CODEC.listOf());
    
    static {
        generateDefaults();
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        
        buildAbilitySettings(SERVER_BUILDER);
        
        SERVER_CONFIG = SERVER_BUILDER.build();
    }
    
    public static ConfigObject<Map<EventType, List<CompoundTag>>> ABILITY_SETTINGS;
    private static Map<EventType, List<CompoundTag>> defaultSettings;
    
    private static void buildAbilitySettings(ForgeConfigSpec.Builder builder) {
        builder.comment("These settings define which abilities are used and the settings which govern them.").push("Abilities");
        ABILITY_SETTINGS = TomlConfigHelper.defineObject(builder, "For_Event", CODEC, defaultSettings);
        builder.pop();
    }
    
    private static void generateDefaults() {
        defaultSettings = new HashMap<>();
        List<CompoundTag> bodyList = new ArrayList<>();
    
        //====================BREAK SPEED DEFAULTS========================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:break_speed").withString(AbilityUtils.SKILLNAME, "mining").withDouble("pickaxe_dig", 0.005).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:break_speed").withString(AbilityUtils.SKILLNAME, "excavation").withDouble("shovel_dig", 0.005).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:break_speed").withString(AbilityUtils.SKILLNAME, "woodcutting").withDouble("axe_dig", 0.005).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:break_speed").withString(AbilityUtils.SKILLNAME, "farming").withDouble("hoe_dig", 0.005).withDouble("shears_dig", 0.005).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:break_speed").withString(AbilityUtils.SKILLNAME, "farming").withDouble("sword_dig", 0.005).build());
        defaultSettings.put(EventType.BREAK_SPEED, new ArrayList<>(bodyList));
        bodyList.clear();
        //====================SKILL_UP DEFAULTS==========================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "mining").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:attribute").withString(AbilityUtils.SKILLNAME, "building")
                         .withString(AbilityUtils.ATTRIBUTE, "forge:reach_distance")
                         .withDouble(AbilityUtils.PER_LEVEL, 0.05)
                         .withDouble(AbilityUtils.MAX_BOOST, 10d).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "building").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "excavation").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "woodcutting").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "farming").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:attribute").withString(AbilityUtils.SKILLNAME, "agility")
                         .withString(AbilityUtils.ATTRIBUTE, "minecraft:generic.movement_speed")
                         .withDouble(AbilityUtils.PER_LEVEL, 0.000035)
                         .withDouble(AbilityUtils.MAX_BOOST, 1d).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "agility").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:attribute").withString(AbilityUtils.SKILLNAME, "endurance")
                         .withString(AbilityUtils.ATTRIBUTE, "minecraft:generic.max_health")
                         .withDouble(AbilityUtils.PER_LEVEL, 0.05)
                         .withDouble(AbilityUtils.MAX_BOOST, 10d).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "endurance").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:attribute").withString(AbilityUtils.SKILLNAME, "combat")
                         .withString(AbilityUtils.ATTRIBUTE, "minecraft:generic.attack_damage")
                         .withDouble(AbilityUtils.PER_LEVEL, 0.005)
                         .withDouble(AbilityUtils.MAX_BOOST, 1d).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "combat").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "gunslinging").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "archery").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "smithing").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "flying").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "swimming").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "sailing").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "fishing").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "crafting").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "magic").build());
        bodyList.add(TagBuilder.start().withString("ability", "ars_scalaes:mana_boost").withString(AbilityUtils.SKILLNAME, "magic")
                         .withDouble(AbilityUtils.MAX_BOOST, 3000d).withDouble(AbilityUtils.PER_LEVEL, 3.0d).build());
        bodyList.add(TagBuilder.start().withString("ability", "ars_scalaes:mana_regen").withString(AbilityUtils.SKILLNAME, "magic")
                         .withDouble(AbilityUtils.MAX_BOOST, 100d).withDouble(AbilityUtils.PER_LEVEL, 0.06d).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "slayer").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "hunter").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "taming").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "cooking").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fireworks").withString(AbilityUtils.SKILLNAME, "alchemy").build());
    
        defaultSettings.put(EventType.SKILL_UP, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================JUMP DEFAULTS=============================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:jump_boost").withString(AbilityUtils.SKILLNAME, "agility").withDouble("per_level", 0.0005).build());
        defaultSettings.put(EventType.JUMP, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================JUMP DEFAULTS=============================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:jump_boost").withString(AbilityUtils.SKILLNAME, "agility").withDouble("per_level", 0.001).build());
        defaultSettings.put(EventType.SPRINT_JUMP, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================JUMP DEFAULTS=============================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:jump_boost").withString(AbilityUtils.SKILLNAME, "agility").withDouble("per_level", 0.0015).build());
        defaultSettings.put(EventType.CROUCH_JUMP, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================SUBMERGED DEFAULTS========================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:breath").withString(AbilityUtils.SKILLNAME, "swimming").build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:effect").withString(AbilityUtils.SKILLNAME, "swimming")
                         .withString("effect", "minecraft:night_vision").build());
        defaultSettings.put(EventType.SUBMERGED, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================FROM_IMPACT==============================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fall_save").withString(AbilityUtils.SKILLNAME, "agility").withDouble("per_level", 0.005).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:fall_save").withString(AbilityUtils.SKILLNAME, "endurance").withDouble("per_level", 0.025).build());
        defaultSettings.put(EventType.FROM_IMPACT, new ArrayList<>(bodyList));
        bodyList.clear();
    
        //=====================DEAL_RANGED_DAMAGE=======================
        bodyList.add(TagBuilder.start().withString("ability", "prpg:damage_boost").withString(AbilityUtils.SKILLNAME, "archery").withList("applies_to", StringTag.valueOf("minecraft:bow"), StringTag.valueOf("mineraft:crossbow"), StringTag.valueOf("minecraft:trident")).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:damage_boost").withString(AbilityUtils.SKILLNAME, "magic").withList("applies_to", StringTag.valueOf("ars_nouveau:spell_bow")).build());
        bodyList.add(TagBuilder.start().withString("ability", "prpg:damage_boost").withString(AbilityUtils.SKILLNAME, "gunslinging").withList("applies_to", StringTag.valueOf("cgm:pistol"),StringTag.valueOf("cgm:shotgun"), StringTag.valueOf("cgm:rifle")).build());
        defaultSettings.put(EventType.DEAL_RANGED_DAMAGE, new ArrayList<>(bodyList));
    }
}
