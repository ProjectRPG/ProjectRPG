package rpg.project.lib.internal.registry;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;
import org.jetbrains.annotations.NotNull;
import rpg.project.lib.api.abilities.Ability;
import rpg.project.lib.api.abilities.AbilityUtils;
import rpg.project.lib.internal.config.AbilitiesConfig;
import rpg.project.lib.internal.util.MsLoggy;
import rpg.project.lib.internal.util.MsLoggy.LOG_CODE;
import rpg.project.lib.internal.util.TagUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class AbilityRegistry {
    private final Map<ResourceLocation, Ability> abilities = new HashMap<>();
    
    public void registerAbility(ResourceLocation abilityID, Ability ability) {
        Preconditions.checkNotNull(abilityID);
        Preconditions.checkNotNull(ability);
        abilities.put(abilityID, ability);
        MsLoggy.DEBUG.log(LOG_CODE.API, "Registered Ability: " + abilityID);
    }
    
    public void registerClientClone(ResourceLocation abilityID, Ability ability) {
        Preconditions.checkNotNull(abilityID);
        Preconditions.checkNotNull(ability);
        
        Ability clientCopy = new Ability(ability.conditions(), ability.propertyDefaults(), (a, b) -> new CompoundTag(), (a, b, c) -> new CompoundTag(), (a, b) -> new CompoundTag(), ability.description(), ability.status());
        abilities.putIfAbsent(abilityID, clientCopy);
    }
    
    public MutableComponent getDescription(ResourceLocation id) {
        return abilities.getOrDefault(id, Ability.empty()).description();
    }
    
    public List<MutableComponent> getStatusLines(ResourceLocation id, Player player, CompoundTag settings) {
        return abilities.getOrDefault(id, Ability.empty()).status().apply(player, settings);
    }
    
    public CompoundTag executeAbility(ResourceLocation cause, Player player, @NotNull CompoundTag dataIn) {
        if (player == null) { return new CompoundTag(); }
        
        CompoundTag output = new CompoundTag();
        AbilitiesConfig.ABILITY_SETTINGS.get().getOrDefault(cause, new ArrayList<>()).forEach(src -> {
            ResourceLocation abilityID = new ResourceLocation(src.getString("type"));
            Ability ability = abilities.getOrDefault(abilityID, Ability.empty());
            src = ability.propertyDefaults().merge(src.merge(dataIn));
            // TODO
            // src.putInt(APIUtils.SKILL_LEVEL, src.contains(APIUtils.SKILLNAME) ? Core.get(player.level).getData().getPlayerSkillLevel(src.getString(APIUtils.SKILLNAME), player.getUUID()) : 0);
            
            CompoundTag executionOutput = ability.start(player, src);
            tickTracker.add(new TickSchedule(ability, player, src, new AtomicInteger(0)));
            
            if (src.contains(AbilityUtils.COOLDOWN)) {
                coolTracker.add(new AbilityCooldown(abilityID, player, src, player.level.getGameTime()));
            }
            
            output.merge(TagUtils.mergeTags(output, executionOutput));
        });
        return output;
    }
    
    private record TickSchedule(Ability ability, Player player, CompoundTag src, AtomicInteger ticksElapsed) {
        public boolean shouldTick() {
            return src.contains(AbilityUtils.DURATION) && ticksElapsed.get() <= src.getInt(AbilityUtils.DURATION);
        }
        
        public void tick() {
            ticksElapsed().getAndIncrement();
            ability.tick(player, src, ticksElapsed.get());
        }
    }
    
    private record AbilityCooldown(ResourceLocation abilityID, Player player, CompoundTag src, long lastUse) {
        public boolean cooledDown(Level level) {
            return level.getGameTime() > lastUse + src.getInt(AbilityUtils.COOLDOWN);
        }
    }
    
    private final List<TickSchedule> tickTracker = new ArrayList<>();
    private final List<AbilityCooldown> coolTracker = new ArrayList<>();
    
    public void executeAbilityTicks(TickEvent.LevelTickEvent event) {
        coolTracker.removeIf(tracker -> tracker.cooledDown(event.level));
        new ArrayList<>(tickTracker).forEach(schedule -> {
            if (schedule.shouldTick()) {
                schedule.tick();
            } else {
                schedule.ability().stop(schedule.player(), schedule.src());
            }
            tickTracker.remove(schedule);
        });
    }
    
    public boolean isAbilityCooledDown(Player player, CompoundTag src) {
        ResourceLocation abilityID = new ResourceLocation(src.getString("ability"));
        return coolTracker.stream().noneMatch(cd -> cd.player().equals(player) && cd.abilityID().equals(abilityID));
    }
}
