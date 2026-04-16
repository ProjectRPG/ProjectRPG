package rpg.project.lib.api.abilities;

import net.minecraft.resources.Identifier;
import java.util.function.Supplier;
import net.minecraft.util.context.ContextKey;
import rpg.project.lib.internal.registry.ClientPanelRegistry;
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
     * @param system supplies a new instance of the AbilitySystem to be registered.
     */
    public static void registerAbilitySystem(Supplier<AbilitySystem> system) {
    	CommonSetup.abilitySupplier = system;
    }

    /**Registers a GUI provider for rendering ability information in the glossary.  This is
     * an optional registration step when creating abilities.  Project RPG provides a default
     * implementation of the GUI provider which uses your ability's description plus the key-
     * value information from the config.
     *
     * If your ability requires more nuanced explanation of its config values, it is strongly
     * recommended that you register a provider using this method.
     *
     * @param abilityID the same ID as your ability
     * @param panelProvider the provider.
     */
    public static void registerAbilityPanel(Identifier abilityID, AbilityPanelProvider panelProvider) {
        ClientPanelRegistry.registerAbilityPanel(abilityID, panelProvider);
    }

    /**Helper method for implementations of AbilitySystem to obtain ability providers as
     * applicable to their configurations.
     *
     * @param abilityID the ability ID being queried
     * @return the registered provider or the default provider if not present.
     */
    public static AbilityPanelProvider getAbilityPanel(Identifier abilityID) {
        return ClientPanelRegistry.getAbilityPanel(abilityID);
    }
}
