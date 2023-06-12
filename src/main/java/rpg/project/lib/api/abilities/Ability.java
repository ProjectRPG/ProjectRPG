package rpg.project.lib.api.abilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import org.apache.commons.lang3.function.TriFunction;
import rpg.project.lib.internal.Core;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

/**<p>Abilities are special behavior that can be activated
 * by the player under certain conditions.  The
 * {@link AbilitySystem} is responsible for determining
 * how abilities are made available to the player and 
 * the configuration format for doing so.</p>
 * <p>Any addon can add abilities to Project RPG but only
 * one addon sets the AbilitySystem.</p> 
 * <p>Abilities function with the following behavior:<ol>
 * <li>Conditions are tested to ensure the ability can execute
 * when invoked.  This check is independent of the AbilitySystem
 * check and is used for internal checking to ensure the properties
 * provided in the configuration are valid for the intended use
 * of the ability.  {@link #propertyDefaults} are used to supply
 * default settings in case a configuration omits them. </li>
 * <li>The {@link #start} behavior is called.  For abilities with
 * instantaneous behavior, this may be the end of the execution.</li>
 * <li>Next, {@link #tick} behavior executes.  This may be nothing.
 * Conditions must still be met for the duration of the tick lifespan
 * for the tick function to continue to completion.</li>
 * <li>If the conditions are no longer met or the tick duration is
 * reached, the {@link #stop} behavior will execute and condluce the
 * ability's functionality.</li></ol></p>
 * <p>The last attributes of the ability are the {@link #description}
 * and {@link #status}. These are used by the GUI systems to display
 * information about the ability.</p>
 */
public record Ability(
	/**Determines if the ability can start and if it should continue ticking.*/
    BiPredicate<Player, CompoundTag> conditions,
    /**Default settings to be supplied if the configuration omits them*/
    CompoundTag propertyDefaults,
    /**The initial behavior of this ability.*/
    BiFunction<Player, CompoundTag, CompoundTag> start,
    /**continued behavior after {@link #start} that lasts for the duration
     * set by the configuration via {@link AbilityUtils#DURATION}*/
    TriFunction<Player, CompoundTag, Integer, CompoundTag> tick,
    /**The final behavior of this ability.  This is always called when ticking
     * completes and should be used to clean up anything the ability does not 
     * want to persist beyond its lifespan*/
    BiFunction<Player, CompoundTag, CompoundTag> stop,
    /**A displayable explanation of what the ability does.*/
    MutableComponent description,
    /**Consumes the player and configuration setting to produce information
     * about the ability that best reflects attributes of the ability when
     * invoked.  This should be used to provide information to the player 
     * about how the ability will behave specifical for them.  For example:
     * how much an attributes will be boosted, the strength behind a manuever
     * or the quantity of some form of output.*/
    BiFunction<Player, CompoundTag, List<MutableComponent>> status) {
    
    public static class Builder {
        private BiPredicate<Player, CompoundTag> conditions = (p, n) -> true;
        private CompoundTag propertyDefaults = new CompoundTag();
        private BiFunction<Player, CompoundTag, CompoundTag> start = (p, c) -> new CompoundTag();
        private TriFunction<Player, CompoundTag, Integer, CompoundTag> tick = (p, c, i) -> new CompoundTag();
        private BiFunction<Player, CompoundTag, CompoundTag> stop = (p, c) -> new CompoundTag();
        private MutableComponent description = Component.empty();
        private BiFunction<Player, CompoundTag, List<MutableComponent>> status = (p, s) -> List.of();
        
        protected Builder() { }
        /**Sets the custom conditions for this ability.  By default,
         * all abilities use the {@link Ability#VALID_CONTEXT} as a
         * basic condition.  This method adds to those.
         * 
         * @param conditions sets the {@link Ability#conditions}
         * @return the builder instance
         */
        public Builder addConditions(BiPredicate<Player, CompoundTag> conditions) {
            this.conditions = conditions;
            return this;
        }
        /**@param defaults sets the {@link Ability#propertyDefaults}
         * @return the builder instance
         */
        public Builder addDefaults(CompoundTag defaults) {
            this.propertyDefaults = defaults;
            return this;
        }
        /**@param start sets the {@link Ability#start}
         * @return the builder instance
         */
        public Builder setStart(BiFunction<Player, CompoundTag, CompoundTag> start) {
            this.start = start;
            return this;
        }
        /**@param tick sets the {@link Ability#tick}
         * @return the builder instance
         */
        public Builder setTick(TriFunction<Player, CompoundTag, Integer, CompoundTag> tick) {
            this.tick = tick;
            return this;
        }
        /**@param stop sets the {@link Ability#stop}
         * @return the builder instance
         */
        public Builder setStop(BiFunction<Player, CompoundTag, CompoundTag> stop) {
            this.stop = stop;
            return this;
        }
        /**@param description sets the {@link Ability#description}
         * @return the builder instance
         */
        public Builder setDescription(MutableComponent description) {
            this.description = description;
            return this;
        }
        /**@param status sets the {@link Ability#status}
         * @return the builder instance
         */
        public Builder setStatus(BiFunction<Player, CompoundTag, List<MutableComponent>> status) {
            this.status = status;
            return this;
        }
        /**@return an assembled {@link Ability}*/
        public Ability build() {
            return new Ability(conditions, propertyDefaults, start, tick, stop, description, status);
        }
    }
    
    /**@return a new {@link Builder} instance */
    public static Builder begin() { return new Builder(); }
    /**@return a default ability with no unique behavior*/
    public static Ability empty() { return new Builder().build(); }
    
    /**Interally checks both the {@link #VALID_CONTEXT} and {@link #conditions}
     * to determine if this ability can start and whether it should continue to
     * execute tick behavior.
     * 
     * @param player the player executing the ability
     * @param settings the configuration settings for this ability
     * @return whether this ability can start and continue ticking
     */
    private boolean canActivate(Player player, CompoundTag settings) {
        return VALID_CONTEXT.test(player, settings) && conditions().test(player, settings);
    }
    
    /**If conditions are met, executes the initial behavior of the ability
     * 
     * @param player the player executing the ability
     * @param nbt the configuration settings for this ability
     * @return the start behavior output tag
     */
    public CompoundTag start(Player player, CompoundTag nbt) {
        return canActivate(player, nbt) ? start.apply(player, nbt) : new CompoundTag();
    }
    
    /**If conditions are still met, executes the tick behavior of the ability
     * 
     * @param player the player executing the ability
     * @param nbt the configuration settings for this ability
     * @param elapsedTicks the number of ticks already elapsed
     * @return the tick behavior output tag
     */
    public CompoundTag tick(Player player, CompoundTag nbt, int elapsedTicks) {
        return canActivate(player, nbt) ? tick.apply(player, nbt, elapsedTicks) : new CompoundTag();
    }
    
    /**When the ability is not longer conditionally valid or reaches its tick
     * duration, executes ths stop behavior of the ability.
     * 
     * @param player the player executing the ability
     * @param nbt the configuration settings for this ability
     * @return the stop behavior output tag
     */
    public CompoundTag stop(Player player, CompoundTag nbt) {
        return stop.apply(player, nbt);
    }
    
    /**Common conditions for all abilities.  Whether specified or not, users can provide
     * values to these settings to have any ability tested against them.*/
    public static final BiPredicate<Player, CompoundTag> VALID_CONTEXT = (player, src) -> {
        if (src.contains(AbilityUtils.COOLDOWN) && !Core.get(player.level()).getAbilities().isAbilityCooledDown(player, src)) {
            return false;
        }
        if (src.contains(AbilityUtils.CHANCE) && src.getDouble(AbilityUtils.CHANCE) < player.level().random.nextDouble()) {
            return false;
        }
        if (src.contains(AbilityUtils.CONTAINER_NAME)) {            
            int skillLevel = src.getInt(AbilityUtils.PROGRESS_LEVEL);
            if (src.contains(AbilityUtils.MAX_LEVEL) && skillLevel > src.getInt(AbilityUtils.MAX_LEVEL)) {
                return false;
            }
            
            if (src.contains(AbilityUtils.MIN_LEVEL) && skillLevel < src.getInt(AbilityUtils.MIN_LEVEL)) {
                return false;
            }
            
            boolean modulus = src.contains(AbilityUtils.MODULUS), milestone = src.contains(AbilityUtils.MILESTONES);
            if (modulus || milestone) {
                boolean modulus_match = modulus, milestone_match = milestone;
                if (modulus && skillLevel % Math.max(1, src.getInt(AbilityUtils.MODULUS)) != 0) {
                    modulus_match = false;
                }
                if (milestone && !src.getList(AbilityUtils.MILESTONES, Tag.TAG_DOUBLE).stream().map(tag -> ((DoubleTag) tag).getAsInt()).toList().contains(skillLevel)) {
                    milestone_match = false;
                }
                if (!modulus_match && !milestone_match) {
                    return false;
                }
            }
        }
        return true;
    };
}
