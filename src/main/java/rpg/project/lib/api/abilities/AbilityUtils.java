package rpg.project.lib.api.abilities;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Supplier;

import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.LogicalSide;
import org.checkerframework.checker.nullness.qual.NonNull;

import rpg.project.lib.api.APIUtils;
import rpg.project.lib.api.data.SubSystemConfigType;
import rpg.project.lib.api.enums.RegistrationSide;
import rpg.project.lib.api.events.EventContext;
import rpg.project.lib.internal.Core;
import rpg.project.lib.internal.registry.SubSystemCodecRegistry;
import rpg.project.lib.internal.setup.CommonSetup;
import rpg.project.lib.internal.util.Reference;

public class AbilityUtils {
	/**The key to specify which ability this config applies to.
	 * Values associated with this key must reflect the ID used
	 * when the ability was registered.*/
	public static final String TYPE = "type";
	
    public static final String PER_LEVEL = "per_level";
    public static final String MAX_BOOST = "max_boost";
    public static final String RATIO = "ratio";
    public static final String MODIFIER = "modifier";
    public static final String MIN_LEVEL = "min_level";
    public static final String MAX_LEVEL = "max_level";
    public static final String MILESTONES = "milestones";
    public static final String MODULUS = "per_x_level";
    public static final String CHANCE = "chance";
    public static final String COOLDOWN = "cooldown";
    public static final String MULTIPLICATIVE = "multiplicative";
    public static final String BASE = "base";
    /**Sets how long this ability should tick for before stopping*/
    public static final String DURATION = "duration";
    public static final String REDUCTION = "reduce";
    public static final ContextKey<Float> REDUCE = new ContextKey<>(Reference.resource(REDUCTION));
    
    public static final String BLOCK_POS = "block_pos";
    public static final String CONTAINER_NAME = "progress_type";
    public static final String PROGRESS_LEVEL = "progress";
    
    public static final ContextKey<Float> BREAK_SPEED_INPUT_VALUE =  new ContextKey<>(Reference.resource("speed_in"));
    public static final ContextKey<Float> BREAK_SPEED_OUTPUT_VALUE = new ContextKey<>(Reference.resource("speed"));
    
    public static final String DAMAGE_IN = "damageIn";
    public static final String DAMAGE_OUT ="damage";
    public static final String DAMAGE_TYPES = "damage_types";
    
    public static final String ATTRIBUTE = "attribute";
    public static final String JUMP_OUT = "jump_boost_output";
    
    public static final String STACK = "stack";
    public static final String PLAYER_ID = "player_id";
    
    public static final String ENCHANT_LEVEL = "enchant_level";
    public static final String ENCHANT_NAME = "enchant_name";
    
    public static final String AMBIENT = "ambient";
    public static final String VISIBLE = "visible";
    
    public static final String EFFECTS = "effects";
    
    /**Sets the ability system for the ecosystem.  Only one system can be active.
     * 
     * @param id the system configuration ID used to specify the format of configuration settings
     * @param config the configuration specification used by this system.
     * @param system supplies a new instance of the AbilitySystem to be registered.
     */
    public static void registerAbilitySystem(ResourceLocation id, SubSystemConfigType config, Supplier<AbilitySystem> system) {
    	CommonSetup.abilitySupplier = () ->{
    		SubSystemCodecRegistry.registerSubSystem(id, config, SubSystemCodecRegistry.SystemType.ABILITY);
    		return system.get();
    	};    	
    }

    public static AbilityGetter get(RegistryAccess access) {
        return new AbilityGetter(access.lookupOrThrow(APIUtils.ABILITY));
    }

    public record AbilityGetter(Registry<Ability> registry) {
        public MutableComponent getDescription(ResourceLocation id) {
            return registry().getOptional(id).orElse(Ability.empty()).description();
        }

        public List<MutableComponent> getStatusLines(ResourceLocation id, Player player, CompoundTag settings, EventContext context) {
            return registry().getOptional(id).orElse(Ability.empty()).status().apply(player, settings, context);
        }

        public List<CompoundTag> getDefaults() {
            return registry().entrySet().stream().map(entry -> {
                var nbt = entry.getValue().propertyDefaults().copy();
                nbt.putString(AbilityUtils.TYPE, entry.getKey().toString());
                return nbt;
            }).toList();
        }

        public Ability getAbility(ResourceLocation id) {
            return registry().getOptional(id).orElse(Ability.empty());
        }
    }
}
