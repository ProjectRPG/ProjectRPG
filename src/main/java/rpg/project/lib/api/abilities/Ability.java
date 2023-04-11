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

public record Ability(
    BiPredicate<Player, CompoundTag> conditions,
    CompoundTag propertyDefaults,
    BiFunction<Player, CompoundTag, CompoundTag> start,
    TriFunction<Player, CompoundTag, Integer, CompoundTag> tick,
    BiFunction<Player, CompoundTag, CompoundTag> stop,
    MutableComponent description,
    BiFunction<Player, CompoundTag, List<MutableComponent>> status) {
    
    public static class Builder {
        BiPredicate<Player, CompoundTag> conditions = (p, n) -> true;
        CompoundTag propertyDefaults = new CompoundTag();
        BiFunction<Player, CompoundTag, CompoundTag> start = (p, c) -> new CompoundTag();
        TriFunction<Player, CompoundTag, Integer, CompoundTag> tick = (p, c, i) -> new CompoundTag();
        BiFunction<Player, CompoundTag, CompoundTag> stop = (p, c) -> new CompoundTag();
        MutableComponent description = Component.empty();
        BiFunction<Player, CompoundTag, List<MutableComponent>> status = (p, s) -> List.of();
        
        protected Builder() { }
        
        public Builder addConditions(BiPredicate<Player, CompoundTag> conditions) {
            this.conditions = conditions;
            return this;
        }
        
        public Builder addDefaults(CompoundTag defaults) {
            this.propertyDefaults = defaults;
            return this;
        }
        
        public Builder setStart(BiFunction<Player, CompoundTag, CompoundTag> start) {
            this.start = start;
            return this;
        }
        
        public Builder setTick(TriFunction<Player, CompoundTag, Integer, CompoundTag> tick) {
            this.tick = tick;
            return this;
        }
        
        public Builder setStop(BiFunction<Player, CompoundTag, CompoundTag> stop) {
            this.stop = stop;
            return this;
        }
        
        public Builder setDescription(MutableComponent description) {
            this.description = description;
            return this;
        }
        
        public Builder setStatus(BiFunction<Player, CompoundTag, List<MutableComponent>> status) {
            this.status = status;
            return this;
        }
        
        public Ability build() {
            return new Ability(conditions, propertyDefaults, start, tick, stop, description, status);
        }
    }
    
    public static Builder begin() { return new Builder(); }
    public static Ability empty() { return new Builder().build(); }
    
    private boolean canActivate(Player player, CompoundTag settings) {
        return VALID_CONTEXT.test(player, settings) && conditions().test(player, settings);
    }
    
    public CompoundTag start(Player player, CompoundTag nbt) {
        return canActivate(player, nbt) ? start.apply(player, nbt) : new CompoundTag();
    }
    
    public CompoundTag tick(Player player, CompoundTag nbt, int elapsedTicks) {
        return canActivate(player, nbt) ? tick.apply(player, nbt, elapsedTicks) : new CompoundTag();
    }
    
    public CompoundTag stop(Player player, CompoundTag nbt) {
        return canActivate(player, nbt) ? stop.apply(player, nbt) : new CompoundTag();
    }
    
    public static final BiPredicate<Player, CompoundTag> VALID_CONTEXT = (player, src) -> {
        if (src.contains(AbilityUtils.COOLDOWN) && !Core.get(player.level).getAbilityRegistry().isAbilityCooledDown(player, src)) {
            return false;
        }
        if (src.contains(AbilityUtils.CHANCE) && src.getDouble(AbilityUtils.CHANCE) < player.level.random.nextDouble()) {
            return false;
        }
        if (src.contains(AbilityUtils.SKILLNAME)) {
            // TODO
            // if (src.contains(FireworkHandler.FIREWORK_SKILL) && !src.getString(AbilityUtils.SKILLNAME).equals(src.getString(FireworkHandler.FIREWORK_SKILL))) {
            //     return false;
            // }
            
            int skillLevel = src.getInt(AbilityUtils.SKILL_LEVEL);
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
