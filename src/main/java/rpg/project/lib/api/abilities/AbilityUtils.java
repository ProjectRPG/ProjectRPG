package rpg.project.lib.api.abilities;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.LogicalSide;
import org.checkerframework.checker.nullness.qual.NonNull;
import rpg.project.lib.api.enums.AbilitySide;
import rpg.project.lib.internal.Core;

public class AbilityUtils {
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
    public static final String DURATION = "duration";
    
    public static final String BLOCK_POS = "block_pos";
    public static final String CONTAINER_NAME = "progress_type";
    public static final String PROGRESS_LEVEL = "progress";
    
    public static final String BREAK_SPEED_INPUT_VALUE = "speedIn";
    public static final String BREAK_SPEED_OUTPUT_VALUE = "speed";
    
    public static final String DAMAGE_IN = "damageIn";
    public static final String DAMAGE_OUT ="damage";
    
    public static final String ATTRIBUTE = "attribute";
    public static final String JUMP_OUT = "jump_boost_output";
    
    public static final String STACK = "stack";
    public static final String PLAYER_ID = "player_id";
    
    public static final String ENCHANT_LEVEL = "enchant_level";
    public static final String ENCHANT_NAME = "enchant_name";
    
    public static final String AMBIENT = "ambient";
    public static final String VISIBLE = "visible";
    
    public static final String EFFECTS = "effects";
    
    /**Called during common setup, this method is used to register custom abilities
     * to PMMO so that players can use them in their configurations.  It is
     * strongly recommended that you document your abilities so that users have a
     * full understanding of how to use it. This includes inputs and outputs,
     * reasonable triggers, and sidedness.
     *
     * @param abilityID a custom id for your ability that can be used in abilities.json to reference this ability
     * @param propertyDefaults keys used by your abilities and default values to supply if omitted
     * @param customConditions a predicate for checks outside the standard built in checks
     * @param onStart the function executing the behavior of this ability when triggered
     * @param onTick the function to execute each tick for the duration configured
     * @param onStop the function executing the behavior of this ability when expected to end
     * @param description summarizes what the function does for the glossary
     * @param status provides a multiline description of the status of the ability for the supplied player
     * @param side the logical sides this ability should execute on.  Your implementation should factor in sidedness to avoid crashes.
     */
    public static void registerAbility(@NonNull ResourceLocation abilityID, @NonNull Ability ability, @NonNull AbilitySide side) {
        switch (side) {
            case SERVER -> {
                Core.get(LogicalSide.SERVER).getAbilityRegistry().registerAbility(abilityID, ability);
                Core.get(LogicalSide.CLIENT).getAbilityRegistry().registerClientClone(abilityID, ability);
            }
            case CLIENT -> Core.get(LogicalSide.CLIENT).getAbilityRegistry().registerAbility(abilityID, ability);
            case BOTH -> {
                Core.get(LogicalSide.SERVER).getAbilityRegistry().registerAbility(abilityID, ability);
                Core.get(LogicalSide.CLIENT).getAbilityRegistry().registerAbility(abilityID, ability);
            }
        }
    }
}
